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


import com.ruoyi.common.core.dto.PayOrderDto;
import lombok.Data;

import java.math.BigDecimal;

/*
 *  查询订单 响应参数
 *
 * @author terrfly
 * @site https://www.jeequan.com
 * @date 2021/6/8 17:40
 */
@Data
public class QueryPayOrderRS extends AbstractRS {

    /**
     * 支付订单号
     */
    private Long payOrderId;

    /**
     * 商户号
     */
    private String mchNo;

    /**
     * 商户应用ID
     */
    private String appId;

    /**
     * 商户订单号
     */
    private String mchOrderNo;



    /**
     * 支付方式代码
     */
    private String wayCode;

    /**
     * 支付金额,单位分
     */
    private BigDecimal amount;

    /**
     * 三位货币代码,人民币:cny
     */
    private String currency;

    /**
     * 支付状态: 0-订单生成, 1-支付中, 2-支付成功, 3-支付失败, 4-已撤销, 5-已退款, 6-订单关闭
     */
    private Integer status;

    /**
     * 渠道支付错误码
     */
    private String errCode;

    /**
     * 渠道支付错误描述
     */
    private String errMsg;
    /**
     * 订单支付成功时间
     */
    private Long successTime;

    /**
     * 创建时间
     */
    private Long createdAt;


    public static QueryPayOrderRS buildByPayOrder(PayOrderDto payOrderDto) {

        if (payOrderDto == null) {
            return null;
        }

        QueryPayOrderRS result = new QueryPayOrderRS();
        result.setPayOrderId(payOrderDto.getId());
        result.setAmount(payOrderDto.getAmount());
        result.setCurrency(payOrderDto.getCurrency());
        result.setMchNo(payOrderDto.getTenantCode());
        result.setMchOrderNo(payOrderDto.getAppOrderNo());
        result.setErrCode(payOrderDto.getErrorCode());
        result.setErrMsg(payOrderDto.getErrorMessage());
        result.setStatus(payOrderDto.getStatus());
        result.setWayCode(payOrderDto.getWayCode());
        result.setSuccessTime(payOrderDto.getSuccessTime() == null ? null : payOrderDto.getSuccessTime().getTime());
        result.setCreatedAt(payOrderDto.getCreateTime() == null ? null : payOrderDto.getCreateTime().getTime());

        return result;
    }


}
