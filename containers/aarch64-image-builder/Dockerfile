FROM fedora:31
RUN groupadd -r quarkus -g 1001 && useradd -u 1001 -r -g 1001 -m -d /project -s /sbin/nologin -c "Quarkus user" quarkus && cd /opt && curl https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-20.2.0/graalvm-ce-java11-linux-aarch64-20.2.0.tar.gz -o graalvm-ce-java11-linux-aarch64-20.2.0.tar.gz -L && tar xvvzf graalvm-ce-java11-linux-aarch64-20.2.0.tar.gz && dnf -y install tar gzip gcc glibc-devel zlib-devel shadow-utils unzip gcc-c++ glibc-langpack-en && dnf clean all
ENV JAVA_HOME /opt/graalvm-ce-java11-20.2.0
ENV PATH "$PATH:$JAVA_HOME/bin"
RUN gu install native-image
WORKDIR /project
VOLUME ["/project"]
USER 1001
ENTRYPOINT ["native-image"]
