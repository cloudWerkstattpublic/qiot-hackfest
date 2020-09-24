#!/bin/bash
podman run -e URL_SENSOR_GAS=http://raspyEnviro:5000/gas -e URL_SENSOR_POLLUTION=http://raspyEnviro:5000/pollution -it --rm --name qiotedgeservice quay.io/cloudwerkstatt/qiot-edge-service:1-aarch64
