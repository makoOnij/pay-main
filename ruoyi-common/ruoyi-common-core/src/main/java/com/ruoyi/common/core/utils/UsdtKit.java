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
package com.ruoyi.common.core.utils;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;

/*
 * USDT 工具类
 *
 */
public class UsdtKit {


    public static BigDecimal getCurrentRate() {
        String result = HttpUtil.get("https://www.okx.com/priapi/v3/b2c/deposit/quotedPrice?t=1701689962099&side=buy&quoteCurrency=CNY&baseCurrency=USDT");
        JSONObject parse = JSONObject.parseObject(result);
        return parse.getJSONArray("data").getJSONObject(0).getBigDecimal("price");
    }

}

