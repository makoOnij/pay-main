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

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.domain.ChannelRetMsg;
import com.ruoyi.common.core.dto.PayOrderDto;
import com.ruoyi.common.core.dto.TenantDto;
import com.ruoyi.payment.service.RequestKitBean;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * 通道回调抽象类
 */
public abstract class AbstractChannelNoticeService implements IChannelNoticeService {
    @Autowired
    private RequestKitBean requestKitBean;

    protected JSONObject getReqParamJSON() {
        return requestKitBean.getReqParamJSON();
    }

    /**
     * 文本类型的响应数据
     **/
    protected ResponseEntity textResp(String text) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.TEXT_HTML);
        return new ResponseEntity(text, httpHeaders, HttpStatus.OK);
    }

    /**
     * json类型的响应数据
     **/
    protected ResponseEntity jsonResp(Object body) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity(body, httpHeaders, HttpStatus.OK);
    }

    public abstract String getIfCode();

    /**
     * 转换参数
     *
     * @param request
     * @param urlOrderId
     * @return
     */
    public abstract MutablePair<String, Object> parseParams(HttpServletRequest request, java.lang.String urlOrderId, NoticeTypeEnum noticeTypeEnum);

    public abstract ChannelRetMsg doNotice(HttpServletRequest request,
                                           Object params, PayOrderDto payOrderDto, TenantDto mchAppConfigContext, NoticeTypeEnum noticeTypeEnum);

    @Override
    public ResponseEntity doNotifyOrderNotExists(HttpServletRequest request) {
        return textResp("order not exists");
    }

    @Override
    public ResponseEntity doNotifyOrderStateUpdateFail(HttpServletRequest request) {
        return textResp("update status error");
    }


}
