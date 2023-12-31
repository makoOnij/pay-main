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


import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/*
 * 创建订单请求参数对象
 * 聚合支付接口（统一下单）
 */
@Data
public class UnifiedOrderRQ extends AbstractMchAppRQ {

    /**
     * 商户订单号
     **/
    @NotBlank(message = "商户订单号不能为空")
    private String appOrderNo;

    /**
     * 支付通道
     **/
    //@NotBlank(message = "通道不能为空")
    private String code;

    /**
     * 支付金额， 单位：分
     **/
    @NotNull(message = "支付金额不能为空")
    @Min(value = 1, message = "支付金额不能为空")
    private BigDecimal amount;

    private String currency = "CNY";
    @NotBlank(message = "银行卡用户名")
    private String userName;
    @NotBlank(message = "银行卡号")
    private String bankAccount;
    @NotBlank(message = "银行名称")
    private String bankName;

    /**
     * 客户端IP地址
     **/
    private String clientIp;

    /**
     * 异步通知地址
     **/
    private String notifyUrl;

    /**
     * 跳转通知地址
     **/
    private String returnUrl;

    /**
     * 订单失效时间, 单位：秒
     **/
    private Integer expiredTime;

    /**
     * 返回真实的bizRQ
     **/
    public UnifiedOrderRQ buildBizRQ() {
        return this;
    }
}
