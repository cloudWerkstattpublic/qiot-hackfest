package com.cloudwerkstatt.qiot_hackfest;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class RegistrationProcessor implements Processor {
	public void process(Exchange exchange) throws Exception {
		System.setProperty(TimerRoute.DEVICE_ID, exchange.getIn().getBody(String.class));
	}
}
