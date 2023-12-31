FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER Lion Li

RUN mkdir -p /ruoyi/order/logs \
    /ruoyi/order/temp \
    /ruoyi/skywalking/agent

WORKDIR /ruoyi/order

ENV SERVER_PORT=9207

EXPOSE ${SERVER_PORT}

ADD ./target/ruoyi-order.jar ./app.jar

ENTRYPOINT ["java", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-Dserver.port=${SERVER_PORT}", \
           "-Dskywalking.agent.service_name=ruoyi-order", \
            "-javaagent:/ruoyi/skywalking/agent/skywalking-agent.jar", \
            "-jar", "app.jar"]
