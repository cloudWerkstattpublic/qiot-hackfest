package com.cloudwerkstatt.qiot_hackfest;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.cloudwerkstatt.qiot_hackfest.model.Gas;
import com.cloudwerkstatt.qiot_hackfest.util.DateUtil;
import com.cloudwerkstatt.qiot_hackfest.util.DeviceUtil;

public class GasBodyEnricher implements Processor {

	public void process(Exchange exchange) throws Exception {
		Gas gas = exchange.getIn().getBody(Gas.class);

		gas.setStationId(DeviceUtil.getDeviceId());
		gas.setInstant(DateUtil.getInstant());

		exchange.getIn().setBody(gas);
	}

}
