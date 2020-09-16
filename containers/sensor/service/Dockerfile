FROM quay.io/cloudwerkstatt/qiot-sensor-service-base:1-aarch64
RUN pip install flask
COPY sensor.py /sensor.py
CMD python /sensor.py
