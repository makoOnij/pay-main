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
package com.ruoyi.payment.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.dto.PayOrderDto;
import com.ruoyi.common.core.exception.BizException;
import com.ruoyi.payment.params.QueryPayOrderRQ;
import com.ruoyi.payment.params.QueryPayOrderRS;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * 商户查单controller
 */
@Slf4j
@RestController
public class QueryOrderController extends ApiController {


    /**
     * 查单接口
     **/
    @RequestMapping("/api/query")
    public R queryOrder() {

        //获取参数 & 验签
        QueryPayOrderRQ rq = getRQByWithMchSign(QueryPayOrderRQ.class);

        if (StringUtils.isAllEmpty(rq.getMchOrderNo(), rq.getPayOrderId())) {
            throw new BizException("mchOrderNo 和 payOrderId不能同时为空");
        }

        PayOrderDto payOrderDto = remoteOrderService.queryMchOrder(rq.getMchNo(), rq.getPayOrderId(), rq.getMchOrderNo());
        if (payOrderDto == null) {
            throw new BizException("订单不存在");
        }

        QueryPayOrderRS bizRes = QueryPayOrderRS.buildByPayOrder(payOrderDto);
        return R.ok(bizRes);
    }

}
