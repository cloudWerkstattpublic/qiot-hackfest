package main

import (
	"encoding/binary"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"io"
	"log"
	"net/http"

        "sync"

	i2c "github.com/d2r2/go-i2c"

	"github.com/jacobsa/go-serial/serial"

	"github.com/d2r2/go-bsbmp"
)

const ads1015Address = 0x49

const ox uint16 = 0b100  // in0/gnd
const red uint16 = 0b101 // in1/gnd
const nh3 uint16 = 0b110 // in2/gnd

var mutex = &sync.Mutex{}

func reset() {
	mutex.Lock()
	defer mutex.Unlock()
}

type Gas struct {
	OX  float32 `json:"oxidising"`
	RED float32 `json:"reducing"`
	NH3 float32 `json:"nh3"`
	ADC float32 `json:"adc"`
}

type Pollution struct {
	PM1_0     uint16 `json:"PM1_0"`
	PM2_5     uint16 `json:"PM2_5"`
	PM10      uint16 `json:"PM10"`
	PM1_0_ATM uint16 `json:"PM1_0_atm"`
	PM2_5_ATM uint16 `json:"PM2_5_atm"`
	PM10_ATM  uint16 `json:"PM10_atm"`
	GT0_3UM   uint16 `json:"gt0_3um"`
	GT0_5UM   uint16 `json:"gt0_5um"`
	GT1_0UM   uint16 `json:"gt1_0um"`
	GT2_5UM   uint16 `json:"gt2_5um"`
	GT5_0UM   uint16 `json:"gt5_0um"`
	GT10UM    uint16 `json:"gt10um"`
}

type Weather struct {
	Temperature 	float32
	Humidity	float32
	Pressure	float32
	Altitude	float32
}

func getGasSensorValue(channel uint16) uint16 {
	// Create new connection to I2C bus on 2 line with address 0x49
	i2c, err := i2c.NewI2C(ads1015Address, 1)
	if err != nil {
		log.Fatalf("Error creating i2c connection: %v", err)
	}
	// Free I2C connection on exit
	defer i2c.Close()

	// Here goes code specific for sending and reading data
	// to and from device connected via I2C bus, like:

	var config uint16 = 0

	// start mode
	config = config | 0b1

	// Set in0/gnd
	config = config<<3 | channel

	// set gain 6.144
	config = config<<3 | 0b000

	// set single mode
	config = config<<1 | 0b1

	// set data rate (1600)
	config = config<<3 | 0b100

	// set comparater mode
	config = config<<1 | 0b0

	// set comparator polarity
	config = config<<1 | 0b0

	// set latching comparator
	config = config<<1 | 0b0

	// set comparator queue
	config = config<<2 | 0b11

	// Write config
	_, err = i2c.WriteBytes([]byte{0x01, uint8(config >> 8), uint8(config & 0xff)})
	if err != nil {
		log.Fatalf("Error writing bytes to i2c %v", err)
	}

	for {
		//Read config, wait for until measurement is not active anymore
		buf := make([]byte, 2)
		_, _ = i2c.ReadBytes(buf)

		if (buf[0] >> 7) == 0b1 {
			break
		}
	}

	// Set to conversion register
	_, _ = i2c.WriteBytes([]byte{0x00})

	// Read conversion register
	buf := make([]byte, 2)
	_, _ = i2c.ReadBytes(buf)

	rawValue := binary.BigEndian.Uint16(buf) >> 4
	log.Printf("Raw value: %v\n", rawValue)

	return rawValue
}

func rawValueToGasResistance(rawValue uint16) float32 {
	value := (float32(rawValue) / 2048) * (6144) / 1000
	log.Printf("Voltage: %vV\n", value)
	valueF := float32(value)
	resistance := (valueF * 56000) / (3.3 - valueF)
	log.Printf("Resistance: %v\n", resistance)
	return resistance
}

func getGasSensorValues() Gas {
	values := Gas{}
	values.OX = rawValueToGasResistance(getGasSensorValue(ox))
	values.NH3 = rawValueToGasResistance(getGasSensorValue(nh3))
	values.RED = rawValueToGasResistance(getGasSensorValue(red))
	return values
}

func getPollutionSensorValues() Pollution {
	options := serial.OpenOptions{
		PortName:        "/dev/ttyAMA0",
		BaudRate:        9600,
		DataBits:        8,
		StopBits:        1,
		MinimumReadSize: 30,
	}

	port, err := serial.Open(options)
	if err != nil {
		log.Fatalf("serial.Open: %v", err)
	}

	defer port.Close()
	marker := []byte{0x42, 0x4d}
	frame := make([]byte, 30)
	buf := make([]byte, 1)

	i := 0
	for {
		n, err := port.Read(buf)
		if err != nil {
			if err != io.EOF {
				log.Fatalf("Error reading from serial port: %v", err)
			}
		} else {
			buf = buf[:n]
			log.Printf("Rx: %v", hex.EncodeToString(buf))
			if buf[0] == marker[i] {
				i = i + 1
				if i == 2 {
					break
				}
			} else {
				i = 0
			}
		}
	}

	pollution := Pollution{}
	n, err := port.Read(frame)
	if err != nil {
		if err != io.EOF {
			log.Printf("Error reading from serial port: %v", err)
		}
	} else {
		frame = frame[:n]
		log.Printf("Rx: %v", hex.EncodeToString(frame))

		length := binary.BigEndian.Uint16(frame[0:])
		checksum := binary.BigEndian.Uint16(frame[28:])
		log.Printf("Length: %v", length)
		log.Printf("Checksum: %v", checksum)
		pollution.PM1_0 = binary.BigEndian.Uint16(frame[2:])
		pollution.PM2_5 = binary.BigEndian.Uint16(frame[4:])
		pollution.PM10 = binary.BigEndian.Uint16(frame[6:])
		pollution.PM1_0_ATM = binary.BigEndian.Uint16(frame[8:])
		pollution.PM2_5_ATM = binary.BigEndian.Uint16(frame[10:])
		pollution.PM10_ATM = binary.BigEndian.Uint16(frame[12:])
		pollution.GT0_3UM = binary.BigEndian.Uint16(frame[14:])
		pollution.GT0_5UM = binary.BigEndian.Uint16(frame[16:])
		pollution.GT1_0UM = binary.BigEndian.Uint16(frame[18:])
		pollution.GT2_5UM = binary.BigEndian.Uint16(frame[20:])
		pollution.GT5_0UM = binary.BigEndian.Uint16(frame[22:])
		pollution.GT10UM = binary.BigEndian.Uint16(frame[24:])
	}
	return pollution
}

func getWeatherSensorValues() Weather {
	i2c, err := i2c.NewI2C(0x76, 1)
	if err != nil {
		log.Fatal(err)
	}
	defer i2c.Close()

	sensor, err := bsbmp.NewBMP(bsbmp.BME280, i2c)
	if err != nil {
		log.Fatal(err)
	}

	t, err := sensor.ReadTemperatureC(bsbmp.ACCURACY_STANDARD)
	if err != nil {
		log.Fatal(err)
	}
	log.Printf("Temperature = %v*C\n", t)
	
	_, h, err := sensor.ReadHumidityRH(bsbmp.ACCURACY_STANDARD)
	if err != nil {
                log.Fatal(err)
        }
        log.Printf("Humidity = %v%\n", h)

	p, err := sensor.ReadPressurePa(bsbmp.ACCURACY_STANDARD)
	if err != nil {
		log.Fatal(err)
	}
	log.Printf("Pressure = %v Pa\n", p)
	
	a, err := sensor.ReadAltitude(bsbmp.ACCURACY_STANDARD)
	if err != nil {
		log.Fatal(err)
	}
	log.Printf("Altitude = %v m\n", a)

	weather := Weather{}

	weather.Temperature = t
	weather.Humidity = h
	weather.Pressure = p
	weather.Altitude = a

	return weather
}

func gasSensorHandler(w http.ResponseWriter, r *http.Request) {
	defer reset()

	func() {
		mutex.Lock()

		json, err := json.Marshal(getGasSensorValues())
		if err != nil {
			log.Printf("Error marshaling gas sensor values %v", err)
		} else {
			log.Printf("Marshalled gas sensor values: %v", string(json))
			fmt.Fprintf(w, string(json))
		}

		mutex.Unlock()
	}()
}

func pollutionSensorHandler(w http.ResponseWriter, r *http.Request) {
	defer reset()

	func() {
		mutex.Lock()

		json, err := json.Marshal(getPollutionSensorValues())
		if err != nil {
			log.Printf("Error marshaling pollution sensor values %v", err)
		} else {
			log.Printf("Marshalled pollution sensor values: %v", string(json))
			fmt.Fprintf(w, string(json))
		}

		mutex.Unlock()
	}()
}

func weatherSensorHandler(w http.ResponseWriter, r *http.Request) {
	defer reset()

	func() {
	        mutex.Lock()

	        json, err := json.Marshal(getWeatherSensorValues())
	        if err != nil {
	                log.Printf("Error marshaling weather sensor values %v", err)
	        } else {
	                log.Printf("Marshalled weather sensor values: %v", string(json))
	                fmt.Fprintf(w, string(json))
	        }

        	mutex.Unlock()
	}()
}

func main() {
	http.HandleFunc("/gas", gasSensorHandler)
	http.HandleFunc("/pollution", pollutionSensorHandler)
	http.HandleFunc("/weather", weatherSensorHandler)

	log.Printf("Starting server at port 5050\n")
	if err := http.ListenAndServe(":5050", nil); err != nil {
		log.Fatal(err)
	}
}
