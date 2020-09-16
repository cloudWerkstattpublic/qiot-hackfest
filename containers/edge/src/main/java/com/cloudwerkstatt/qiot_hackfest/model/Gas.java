package com.cloudwerkstatt.qiot_hackfest.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Gas {
	public int stationId;
	private String instant;
	private double adc;
	private double nh3;
	private double oxidising;
	private double reducing;

	public int getStationId() {
		return stationId;
	}

	public void setStationId(int stationId) {
		this.stationId = stationId;
	}

	public String getInstant() {
		return instant;
	}

	public void setInstant(String instant) {
		this.instant = instant;
	}

	public double getAdc() {
		return adc;
	}

	public void setAdc(double adc) {
		this.adc = adc;
	}

	public double getNh3() {
		return nh3;
	}

	public void setNh3(double nh3) {
		this.nh3 = nh3;
	}

	public double getOxidising() {
		return oxidising;
	}

	public void setOxidising(double oxidising) {
		this.oxidising = oxidising;
	}

	public double getReducing() {
		return reducing;
	}

	public void setReducing(double reducing) {
		this.reducing = reducing;
	}

	@Override
	public String toString() {
		return "Gas [stationId=" + stationId + ", instant=" + instant + ", adc=" + adc + ", nh3=" + nh3 + ", oxidising="
				+ oxidising + ", reducing=" + reducing + "]";
	}
}