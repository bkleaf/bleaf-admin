FROM adoptopenjdk/openjdk11:latest

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y \
git \
curl \
wget \
maven \
openssh-server \
tzdata \
unzip && \
apt-get clean

RUN apt-get remove -y --purge netcat-openbsd iputils-ping telnet ftp \
    && rm -rf /var/lib/apt/lists/*

ENV TZ Asia/Seoul

ENV JAVA_TOOL_OPTIONS "-Dfile.encoding=UTF8"

RUN java -version
RUN $JAVA_HOME/bin/javac -version

ENV APP_HOME /bleaf/src/bleaf-comix-admin

WORKDIR $APP_HOME
COPY . $APP_HOME

RUN ["mvn", "clean", "package", "-DskipTests=true"]

EXPOSE 8080

#ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-Djava.security.egd=file:/dev/./urandom -Dsun.net.inetaddr.ttl=0", "-jar", "target/allnew-poi-node.jar"]
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom -Dsun.net.inetaddr.ttl=0", "-jar", "target/bleaf-comix-admin.jar"]