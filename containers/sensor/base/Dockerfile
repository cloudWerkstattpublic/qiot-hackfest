FROM fedora:31
RUN dnf -y update && dnf -y install libgpiod-utils i2c-tools libi2c-devel gcc python3-devel python3-pip && pip3 install enviroplus && dnf -y install python3-numpy python3-i2c-tools python3-pillow python3-setuptools python3-libgpiod python3-RPi.GPIO python3-cffi && python3 -m pip install sounddevice && dnf -y install portaudio && dnf clean all
