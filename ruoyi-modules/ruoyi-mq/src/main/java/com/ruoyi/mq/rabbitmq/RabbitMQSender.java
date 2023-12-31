/*
 * Copyright (c) 2021-2031, 河北计全科技有限公司 (https://www.jeequan.com & jeequan@126.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ruoyi.mq.rabbitmq;


import com.ruoyi.mq.IMQSender;
import com.ruoyi.mq.api.constant.MQSendTypeEnum;
import com.ruoyi.mq.api.constant.MQVenderCS;
import com.ruoyi.mq.api.model.AbstractMQ;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * rabbitMQ 消息发送器的实现
 *
 * @author terrfly
 * @site https://www.jeequan.com
 * @date 2021/7/23 16:52
 */
@Component
@ConditionalOnProperty(name = MQVenderCS.YML_VENDER_KEY, havingValue = MQVenderCS.RABBIT_MQ)
public class RabbitMQSender implements IMQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void send(AbstractMQ mqModel) {

        if (mqModel.getMQType() == MQSendTypeEnum.QUEUE) {

            rabbitTemplate.convertAndSend(mqModel.getMQRouteLey(), mqModel.toMessage());
        } else {

            // fanout模式 的 routeKEY 没意义。
            this.rabbitTemplate.convertAndSend(RabbitMQConfig.FANOUT_EXCHANGE_NAME_PREFIX + mqModel.getMQName(), null, mqModel.toMessage());
        }
    }

    @Override
    public void send(AbstractMQ mqModel, int delay) {


        if (mqModel.getMQType() == MQSendTypeEnum.QUEUE) {

            rabbitTemplate.convertAndSend(RabbitMQConfig.DELAYED_EXCHANGE_NAME, mqModel.getMQName(), mqModel.toMessage(), messagePostProcessor -> {
                messagePostProcessor.getMessageProperties().setDelay(Math.toIntExact(delay * 1000));
                return messagePostProcessor;
            });
        } else {

            // fanout模式 的 routeKEY 没意义。  没有延迟属性
            this.rabbitTemplate.convertAndSend(RabbitMQConfig.FANOUT_EXCHANGE_NAME_PREFIX + mqModel.getMQName(), null, mqModel.toMessage());
        }
    }

}
