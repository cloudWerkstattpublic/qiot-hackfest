package com.cloudwerkstatt.qiot_hackfest;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class PrepareRegistrationProcessor implements Processor {

	public void process(Exchange exchange) throws Exception {
		// Default values
		String name = "raspyEnviro";
		String serial = "0000000066495444";
		String longitude = "48.766744";
		String latitude = "11.403468";
		
		File f = new File("/proc/device-tree/serial-number");
		if(f.exists() && f.canRead()) {
			List<String> lines = Files.readAllLines(f.toPath());
			if(lines.size() > 0) {
				serial = lines.get(0).trim();
			}
		}
		
		exchange.getIn().setHeader("name", name);
		exchange.getIn().setHeader("serial", serial);
		exchange.getIn().setHeader("longitude", longitude);
		exchange.getIn().setHeader("latitude", latitude);
	}
	
}