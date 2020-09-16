#!/bin/bash
sudo podman run --detach --rm --privileged -p 5000:5000  --name qiotsensorservice quay.io/cloudwerkstatt/qiot-sensor-service:1-aarch64
