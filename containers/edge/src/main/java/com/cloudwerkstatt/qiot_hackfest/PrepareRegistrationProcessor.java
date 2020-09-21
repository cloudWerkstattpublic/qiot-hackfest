package com.cloudwerkstatt.qiot_hackfest;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Named("PrepareRegistrationBean")
public class PrepareRegistrationProcessor implements Processor {
	@ConfigProperty(name = "name", defaultValue="raspyEnviro")
	String name;

	@ConfigProperty(name = "longitude", defaultValue="11.403468")
	String longitude;

	@ConfigProperty(name = "latitude", defaultValue="48.766744")
	String latitude;

	public void process(Exchange exchange) throws Exception {
		// Default values
		String serial = "0000000066495444";
		
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