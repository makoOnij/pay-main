FROM rabbitmq:3.12-management

MAINTAINER Lion Li

# 添加插件到指定目录 可按照此方式自行扩展其他插件
ADD ./rabbitmq_delayed_message_exchange-3.12.0.ez /opt/rabbitmq/plugins/

# 开启插件
RUN rabbitmq-plugins enable rabbitmq_delayed_message_exchange
ENTRYPOINT ["rabbitmq-server"]


