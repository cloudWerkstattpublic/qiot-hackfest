package com.cloudwerkstatt.qiot_hackfest;

public class Properties {
	public static String REGISTRATION_BASE_URL = "http://qiot-registration-qiot.apps.cluster-emeaiot-d864.emeaiot-d864.example.opentlc.com/v1";

	public static String TELEMETRY_BASE_HOST = "qiot-broker-mqtts-0-svc-rte-qiot.apps.cluster-emeaiot-d864.emeaiot-d864.example.opentlc.com";
	public static String TELEMETRY_BASE_PORT = "443";
	
	public static String SENSOR_BASE_URL = "http://raspyEnviro:5000";

	public static String GAS_SENSOR_URL = SENSOR_BASE_URL + "/gas";
	public static String POLLUTION_SENSOR_URL = SENSOR_BASE_URL + "/pollution";
}
