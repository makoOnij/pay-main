FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER Lion Li

RUN mkdir -p /ruoyi/mq/logs \
    /ruoyi/mq/temp \
    /ruoyi/skywalking/agent

WORKDIR /ruoyi/mq

ENV SERVER_PORT=9402

EXPOSE ${SERVER_PORT}

ADD ./target/ruoyi-mq.jar ./app.jar

ENTRYPOINT ["java", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-Dserver.port=${SERVER_PORT}", \
            "-Dskywalking.agent.service_name=ruoyi-mq", \
            "-javaagent:/ruoyi/skywalking/agent/skywalking-agent.jar", \
            "-jar", "app.jar"]
