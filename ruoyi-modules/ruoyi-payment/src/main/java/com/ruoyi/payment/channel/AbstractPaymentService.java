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
package com.ruoyi.payment.channel;

import com.ruoyi.boss.api.RemoteBossService;
import com.ruoyi.common.core.dto.PayOrderDto;
import com.ruoyi.common.core.utils.SeqKit;
import com.ruoyi.payment.params.UnifiedOrderRQ;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class AbstractPaymentService implements IPaymentService {


    @Override
    public String customPayOrderId(UnifiedOrderRQ bizRQ, PayOrderDto payOrderDto) {
        return SeqKit.genPayOrderId(); //使用系统默认支付订单号
    }


    public abstract String getIfCode();

    public String customPayOrderId(PayOrderDto payOrderDto) {
        return null; //使用系统默认支付订单号
    }


    protected String getNotifyUrl() {
        return "/payment/api/notify/" + getIfCode();
    }

    protected String getNotifyUrl(String payOrderId) {
        return "/payment/api/notify/" + getIfCode() + "/" + payOrderId;
    }

    protected String getReturnUrl() {
        return "/way/return/" + getIfCode();
    }

    protected String getReturnUrl(String payOrderId) {
        return "/way/return/" + getIfCode() + "/" + payOrderId;
    }


}
