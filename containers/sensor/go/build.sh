#!/bin/sh
CGO_ENABLED=0 go build sensors.go
sudo podman build . -t quay.io/cloudwerkstatt/qiot-sensor-service-go:1-aarch64
