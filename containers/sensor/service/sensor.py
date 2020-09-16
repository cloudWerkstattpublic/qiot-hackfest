#!/usr/bin/env python3
from flask import Flask
from flask import jsonify

import time
from enviroplus import gas
from pms5003 import PMS5003, ReadTimeoutError
import logging

app = Flask(__name__)

pms5003 = PMS5003()
time.sleep(1.0)

@app.route("/pollution")
def readPollution():
    global pms5003
    try:
        sensor_values = pms5003.read()
        output = {
            "PM1_0": sensor_values.pm_ug_per_m3(1.0, False),
            "PM2_5": sensor_values.pm_ug_per_m3(2.5, False),
            "PM10": sensor_values.pm_ug_per_m3(10, False),
            "PM1_0_atm": sensor_values.pm_ug_per_m3(1.0, True),
            "PM2_5_atm": sensor_values.pm_ug_per_m3(2.5, True),
            "PM10_atm": sensor_values.pm_ug_per_m3(None, True),
            "gt0_3um": sensor_values.pm_per_1l_air(0.3),
            "gt0_5um": sensor_values.pm_per_1l_air(0.5),
            "gt1_0um": sensor_values.pm_per_1l_air(1.0),
            "gt2_5um": sensor_values.pm_per_1l_air(2.5),
            "gt5_0um": sensor_values.pm_per_1l_air(5.0),
            "gt10um": sensor_values.pm_per_1l_air(10)
        }
        return jsonify(output)
    except ReadTimeoutError:
        pms5003 = PMS5003()

@app.route("/gas")
def readGas():
    sensor_values = gas.read_all();

    output = {
        "adc": sensor_values.adc,
        "nh3": sensor_values.nh3,
        "oxidising": sensor_values.oxidising,
        "reducing": sensor_values.reducing
    }
    return jsonify(output)

app.run(host= '0.0.0.0')
