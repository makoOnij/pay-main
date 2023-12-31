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

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/*
* 序列号生成 工具类
*
* @author terrfly
* @author jmdhappy
* @site https://www.jeequan.com
* @date 2021/6/8 16:56
*/
public class SeqKit {

    private static final AtomicLong PAY_MCHCODE_SEQ = new AtomicLong(0L);
	private static final AtomicLong PAY_ORDER_SEQ = new AtomicLong(0L);
	private static final AtomicLong REFUND_ORDER_SEQ = new AtomicLong(0L);
	private static final AtomicLong MHO_ORDER_SEQ = new AtomicLong(0L);
	private static final AtomicLong TRANSFER_ID_SEQ = new AtomicLong(0L);
	private static final AtomicLong DIVISION_BATCH_ID_SEQ = new AtomicLong(0L);

    private static final AtomicLong MERCHANT_CHARGE_ID_SEQ = new AtomicLong(0L);

	private static final String PAY_ORDER_SEQ_PREFIX = "P";
	private static final String REFUND_ORDER_SEQ_PREFIX = "R";
	private static final String MHO_ORDER_SEQ_PREFIX = "M";
	private static final String TRANSFER_ID_SEQ_PREFIX = "T";
	private static final String DIVISION_BATCH_ID_SEQ_PREFIX = "D";

    private static final String MERCHANT_CHARGE_ID_SEQ_PREFIX = "C";

	/** 是否使用MybatisPlus生成分布式ID **/
	private static final boolean IS_USE_MP_ID = true;

	/** 生成支付订单号 **/
	public static String genPayOrderId() {
		if(IS_USE_MP_ID) {
			return PAY_ORDER_SEQ_PREFIX + IdUtil.getSnowflake().nextIdStr();
		}
		return String.format("%s%s%04d",PAY_ORDER_SEQ_PREFIX,
				DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN),
				(int) PAY_ORDER_SEQ.getAndIncrement() % 10000);
	}

    public static String genWithdrawOrderId() {
        return "W" + IdUtil.getSnowflake().nextIdStr();
    }

    public static String genFlowOrderId() {
        return "F" + IdUtil.getSnowflake().nextIdStr();
    }

	/** 生成退款订单号 **/
	public static String genRefundOrderId() {
		if(IS_USE_MP_ID) {
			return REFUND_ORDER_SEQ_PREFIX + IdUtil.getSnowflake().nextIdStr();
		}
		return String.format("%s%s%04d",REFUND_ORDER_SEQ_PREFIX,
				DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN),
				(int) REFUND_ORDER_SEQ.getAndIncrement() % 10000);
	}


	/** 模拟生成商户订单号 **/
	public static String genMhoOrderId() {
		if(IS_USE_MP_ID) {
			return MHO_ORDER_SEQ_PREFIX + IdUtil.getSnowflake().nextIdStr();
		}
		return String.format("%s%s%04d", MHO_ORDER_SEQ_PREFIX,
				DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN),
				(int) MHO_ORDER_SEQ.getAndIncrement() % 10000);
	}

	/** 模拟生成商户订单号 **/
	public static String genTransferId() {
		if(IS_USE_MP_ID) {
			return TRANSFER_ID_SEQ_PREFIX + IdUtil.getSnowflake().nextIdStr();
		}
		return String.format("%s%s%04d", TRANSFER_ID_SEQ_PREFIX,
				DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN),
				(int) TRANSFER_ID_SEQ.getAndIncrement() % 10000);
	}

	/** 模拟生成分账批次号 **/
	public static String genDivisionBatchId() {
		if(IS_USE_MP_ID) {
			return DIVISION_BATCH_ID_SEQ_PREFIX + IdUtil.getSnowflake().nextIdStr();
		}
		return String.format("%s%s%04d", DIVISION_BATCH_ID_SEQ_PREFIX,
				DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN),
				(int) DIVISION_BATCH_ID_SEQ.getAndIncrement() % 10000);
	}
    public static String genMerchantChargeId() {
        if(IS_USE_MP_ID) {
            return MERCHANT_CHARGE_ID_SEQ_PREFIX + IdUtil.getSnowflake().nextIdStr();
        }
        return String.format("%s%s%04d", MERCHANT_CHARGE_ID_SEQ_PREFIX,
            DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN),
            (int) MERCHANT_CHARGE_ID_SEQ.getAndIncrement() % 10000);
    }

    public static String genMerchantSecret() {
		return IdUtil.nanoId(10).toUpperCase();
    }

    public static String genMerchantCode() {
        return String.format("%s%04d",
                DateUtil.format(new Date(), DatePattern.PURE_DATE_FORMATTER),
                (int) PAY_MCHCODE_SEQ.getAndIncrement() % 10000);
    }

}

