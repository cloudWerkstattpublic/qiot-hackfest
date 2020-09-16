package com.cloudwerkstatt.qiot_hackfest;

import java.net.URL;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import com.cloudwerkstatt.qiot_hackfest.model.Gas;
import com.cloudwerkstatt.qiot_hackfest.model.Pollution;

public class TimerRoute extends RouteBuilder {
	public static final String DEVICE_ID = "com.cloudwerkstatt.qiot_hackfest.DEVICE_ID";

	@Override
	public void configure() throws Exception {
		URL trustStoreURL = TimerRoute.class.getResource("/security/quarkus.truststore");
		if(trustStoreURL != null) {
			// Not necessary for native build, as truststore is baked in
			// This is the fallback for mvn quarkus:dev
			System.setProperty("javax.net.ssl.trustStore", trustStoreURL.getFile());
		}
		
		from("direct:gasTelemetry")
			.routeId("gasTelemetry")
		
			.log("Read sensor values")
			.toD(Properties.GAS_SENSOR_URL + "?httpMethod=GET")
			.convertBodyTo(String.class)
			.log("Read sensor values: ${body}")
	
			.log("Unmarshal sensor values")
			.unmarshal().json(JsonLibrary.Jackson, Gas.class)
			.log("Unmarshalled sensor values: ${body}")
			
			.log("Enrich sensor data")
			.process(new GasBodyEnricher())
			.log("Enriched sensor data: ${body}")
			
			.log("Prepared JSON")
			.marshal().json()
			.log("Prepared JSON: ${body}")
			
			.log("Sending data to topic")
			.convertBodyTo(String.class)
			.to("paho:gas")
			.log("Sent data to topic")
			;
		
		from("direct:pollutionTelemetry")
			.routeId("pollutionTelemetry")

			.log("Read sensor values")
			.toD(Properties.POLLUTION_SENSOR_URL + "?httpMethod=GET")
			.convertBodyTo(String.class)
			.log("Read sensor values: ${body}")
	
			.log("Unmarshal sensor values")
			.unmarshal().json(JsonLibrary.Jackson, Pollution.class)
			.log("Unmarshalled sensor values: ${body}")
			
			.log("Enrich sensor data")
			.process(new PollutionBodyEnricher())
			.log("Enriched sensor data: ${body}")
			
			.log("Prepared JSON")
			.marshal().json()
			.log("Prepared JSON: ${body}")
			
			.log("Sending data to topic")
			.convertBodyTo(String.class)
			.to("paho:pollution")
			.log("Sent data to topic")
			;
		
		from("timer:sensorTimer?period=5000")
			.routeId("sensorRoute")
		
//			Uncomment to disable registration on first call (i.e. for tests)
//			.setBody(simple("8"))
//			.process(new RegistrationProcessor())

			.choice()
				.when(systemProperty(DEVICE_ID).isNull())
				.log("Fetching id")
				.process(new PrepareRegistrationProcessor())
				.toD(Properties.REGISTRATION_BASE_URL + "/register/serial/${header.serial}/name/${header.name}/longitude/${header.longitude}/latitude/${header.latitude}?httpMethod=PUT")
				.process(new RegistrationProcessor())
				.end()
			.log("Device ID: ${sys."+DEVICE_ID+"}")
			.multicast()
				.to("direct:gasTelemetry")
				.to("direct:pollutionTelemetry")
			;
	}
	
}
