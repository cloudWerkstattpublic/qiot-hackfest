#!/bin/bash
keytool -importcert -keystore security/quarkus.truststore -file server.crt -alias server -storepass changeit -noprompt
