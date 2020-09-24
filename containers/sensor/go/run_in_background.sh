#!/bin/bash
sudo podman run --detach --privileged -p 5050:5050 quay.io/cloudwerkstatt/qiot-sensor-service-go:1-aarch64
