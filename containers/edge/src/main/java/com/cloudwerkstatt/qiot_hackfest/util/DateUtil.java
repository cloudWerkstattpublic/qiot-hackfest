package com.cloudwerkstatt.qiot_hackfest.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	public static final DateFormat ISO_8601_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	
	public static String getInstant() {
		return ISO_8601_DATE_FORMAT.format(new Date()) + "Z";
	}
}
