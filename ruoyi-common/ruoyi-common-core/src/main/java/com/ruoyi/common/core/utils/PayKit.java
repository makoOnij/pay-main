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

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Set;

/*
 * jeepay工具类
 *
 * @author terrfly
 * @site https://www.jeequan.com
 * @date 2021/6/8 16:50
 */
@Slf4j
public class PayKit {

    public static byte[] AES_KEY = "4ChT08phkz59hquD795X7w==".getBytes();

    /**
     * 加密
     **/
    public static String aesEncode(String str) {
        return SecureUtil.aes(PayKit.AES_KEY).encryptHex(str);
    }

    public static String aesDecode(String str) {
        return SecureUtil.aes(PayKit.AES_KEY).decryptStr(str);
    }


    private static String encodingCharset = "UTF-8";

    /**
     * <p><b>Description: </b>计算签名摘要
     * <p>2018年9月30日 上午11:32:46
     *
     * @param map 参数Map
     * @param key 商户秘钥
     * @return
     */
    public static String getSign(Map<String, Object> map, String key) {

        String result = MapUtil.sortJoin(map, "&", "=", true);
        result += "&key=" + key;
        log.info("signStr:{}", result);

        result = SecureUtil.md5(result);
        log.info("sign:{}", result);
        return result;
    }

    public static Boolean verifySign(Map<String, Object> map, String key, String sign) {

        String result = MapUtil.sortJoin(map, "&", "=", true);
        result += "&key=" + key;
        log.info("signStr:{}", result);

        result = SecureUtil.md5(result);
        log.info("sign:{}", result);

        return StrUtil.equals(sign, result, true);
    }


    /**
     * <p><b>Description: </b>MD5
     * <p>2018年9月30日 上午11:33:19
     *
     * @param value
     * @param charset
     * @return
     */
    public static String md5(String value, String charset) {
        MessageDigest md = null;
        try {
            byte[] data = value.getBytes(charset);
            md = MessageDigest.getInstance("MD5");
            byte[] digestData = md.digest(data);
            return toHex(digestData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String toHex(byte input[]) {
        if (input == null) {
            return null;
        }
        StringBuffer output = new StringBuffer(input.length * 2);
        for (int i = 0; i < input.length; i++) {
            int current = input[i] & 0xff;
            if (current < 16) {
                output.append("0");
            }
            output.append(Integer.toString(current, 16));
        }

        return output.toString();
    }

    /**
     * map 转换为  url参数
     **/
    public static String genUrlParams(Map<String, Object> paraMap) {
        if (paraMap == null || paraMap.isEmpty()) {
            return "";
        }
        StringBuffer urlParam = new StringBuffer();
        Set<String> keySet = paraMap.keySet();
        int i = 0;
        for (String key : keySet) {
            urlParam.append(key).append("=").append(paraMap.get(key) == null ? "" : doEncode(paraMap.get(key).toString()));
            if (++i == keySet.size()) {
                break;
            }
            urlParam.append("&");
        }
        return urlParam.toString();
    }

    static String doEncode(String str) {
        if (str.contains("+")) {
            return URLEncoder.encode(str);
        }
        return str;
    }

}
