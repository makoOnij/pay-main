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
package com.ruoyi.payment.service;


import cn.hutool.extra.spring.SpringUtil;
import com.ruoyi.common.core.domain.ChannelRetMsg;
import com.ruoyi.common.core.dto.PayOrderDto;
import com.ruoyi.common.core.dto.TenantDto;
import com.ruoyi.order.api.RemoteOrderService;
import com.ruoyi.payment.channel.IPayOrderQueryService;
import com.ruoyi.system.api.RemoteTenantService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
 * 查询上游订单， &  补单服务实现类
 *
 * @author terrfly
 * @site https://www.jeequan.com
 * @date 2021/6/8 17:40
 */

@Service
@Slf4j
public class ChannelOrderReissueService {


    @Autowired
    private PayOrderProcessService payOrderProcessService;
    @DubboReference
    private RemoteTenantService remoteTenantService;

    @DubboReference
    private RemoteOrderService remoteOrderService;


    /**
     * 处理订单
     **/
    public ChannelRetMsg processPayOrder(PayOrderDto payOrder) {

        try {

            String payOrderId = payOrder.getOrderNo();

            //查询支付接口是否存在
            IPayOrderQueryService queryService = SpringUtil.getBean(payOrder.getWayCode() + "PayOrderQueryService", IPayOrderQueryService.class);

            // 支付通道接口实现不存在
            if (queryService == null) {
                log.error("{} interface not exists!", payOrder.getWayCode());
                return null;
            }

            //查询出商户应用的配置信息
            TenantDto tenant = remoteTenantService.getTenant(payOrder.getTenantId());

            ChannelRetMsg channelRetMsg = queryService.query(payOrder, tenant);
            if (channelRetMsg == null) {
                log.error("channelRetMsg is null");
                return null;
            }

            log.info("补单[{}]查询结果为：{}", payOrderId, channelRetMsg);

            // 查询成功
            if (channelRetMsg.getChannelState() == ChannelRetMsg.ChannelState.CONFIRM_SUCCESS) {
                if (remoteOrderService.updateIng2Success(payOrderId)) {

                    //订单支付成功，其他业务逻辑
                    payOrderProcessService.confirmSuccess(payOrder, tenant);
                }
            } else if (channelRetMsg.getChannelState() == ChannelRetMsg.ChannelState.CONFIRM_FAIL) {  //确认失败

                //1. 更新支付订单表为失败状态
                remoteOrderService.updateIng2Fail(payOrderId, channelRetMsg.getChannelErrCode(), channelRetMsg.getChannelErrMsg());

            }

            return channelRetMsg;

        } catch (Exception e) {  //继续下一次迭代查询
            log.error("error payOrderId = {}", payOrder.getId(), e);
            return null;
        }

    }


}
