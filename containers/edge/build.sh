#!/bin/bash
mvn clean package -Pnative -Dquarkus.native.container-runtime=podman -Dquarkus.native.builder-image=quay.io/cloudwerkstatt/quarkus-native-image-builder-aarch64:20.2.0-java11
podman build -t quay.io/cloudwerkstatt/qiot-edge-service:1-aarch64 .
