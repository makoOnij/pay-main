FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER Lion Li

RUN mkdir -p /ruoyi/boss/logs \
    /ruoyi/boss/temp \
    /ruoyi/skywalking/agent

WORKDIR /ruoyi/boss

ENV SERVER_PORT=9206

EXPOSE ${SERVER_PORT}

ADD ./target/ruoyi-boss.jar ./app.jar

ENTRYPOINT ["java", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-Dserver.port=${SERVER_PORT}", \
           "-Dskywalking.agent.service_name=ruoyi-boss", \
            "-javaagent:/ruoyi/skywalking/agent/skywalking-agent.jar", \
            "-jar", "app.jar"]
