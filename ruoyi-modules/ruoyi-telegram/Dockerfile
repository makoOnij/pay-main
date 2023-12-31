FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER Lion Li

RUN mkdir -p /ruoyi/telegram/logs \
    /ruoyi/telegram/temp \
    /ruoyi/skywalking/agent

WORKDIR /ruoyi/telegram

ENV SERVER_PORT=9209

EXPOSE ${SERVER_PORT}

ADD ./target/ruoyi-telegram.jar ./app.jar

ENTRYPOINT ["java", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-Dserver.port=${SERVER_PORT}", \
#            "-Dskywalking.agent.service_name=ruoyi-system", \
#            "-javaagent:/ruoyi/skywalking/agent/skywalking-agent.jar", \
            "-jar", "app.jar"]
