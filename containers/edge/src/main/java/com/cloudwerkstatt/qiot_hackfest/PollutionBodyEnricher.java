package com.cloudwerkstatt.qiot_hackfest;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.cloudwerkstatt.qiot_hackfest.model.Pollution;
import com.cloudwerkstatt.qiot_hackfest.util.DateUtil;
import com.cloudwerkstatt.qiot_hackfest.util.DeviceUtil;

public class PollutionBodyEnricher implements Processor {

	public void process(Exchange exchange) throws Exception {
		Pollution pollution = exchange.getIn().getBody(Pollution.class);

		pollution.setStationId(DeviceUtil.getDeviceId());
		pollution.setInstant(DateUtil.getInstant());

		exchange.getIn().setBody(pollution);
	}

}
