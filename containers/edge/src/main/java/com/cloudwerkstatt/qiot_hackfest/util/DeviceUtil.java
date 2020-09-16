package com.cloudwerkstatt.qiot_hackfest.util;

import com.cloudwerkstatt.qiot_hackfest.TimerRoute;

public class DeviceUtil {
	public static final int getDeviceId() {
		return Integer.getInteger(TimerRoute.DEVICE_ID, -1);
	}
}
