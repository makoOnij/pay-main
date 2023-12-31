FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER Lion Li

RUN mkdir -p /ruoyi/payment/logs \
    /ruoyi/payment/temp \
    /ruoyi/skywalking/agent

WORKDIR /ruoyi/system

ENV SERVER_PORT=9209

EXPOSE ${SERVER_PORT}

ADD ./target/ruoyi-payment.jar ./app.jar

ENTRYPOINT ["java", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-Dserver.port=${SERVER_PORT}", \
            "-Dskywalking.agent.service_name=ruoyi-payment", \
            "-javaagent:/ruoyi/skywalking/agent/skywalking-agent.jar", \
            "-jar", "app.jar"]
