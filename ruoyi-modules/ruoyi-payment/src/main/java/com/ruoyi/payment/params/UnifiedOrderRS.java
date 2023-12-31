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
package com.ruoyi.payment.params;

import com.alibaba.fastjson.annotation.JSONField;
import com.ruoyi.common.core.domain.ChannelRetMsg;
import lombok.Data;

/*
 * 创建订单(统一订单) 响应参数
 *
 * @author terrfly
 * @site https://www.jeequan.com
 * @date 2021/6/8 17:34
 */
@Data
public class UnifiedOrderRS extends AbstractRS {

    /**
     * 支付订单号
     **/
    private String orderNo;

    /**
     * 商户订单号
     **/
    private String mchOrderNo;

    /**
     * 订单状态
     **/
    private Integer orderState;

    /**
     * 支付参数
     **/
    private String message;

    /**
     * 上游渠道返回数据包 (无需JSON序列化)
     **/
    @JSONField(serialize = false)
    private ChannelRetMsg channelRetMsg;


}
