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
package com.ruoyi.common.core.enums;

import lombok.Getter;

/*
* 接口返回码
*
*/
@Getter
public enum OrderStatus {

    WAIT(0, "已入库"),
    SETTLE(1, "代付中"),

    SUCCESS(2, "代付成功"),
    FAIL(3, "代付失败"),
    NOTIFYING(4, "已通知"),
    NOTIFYSUCCESS(5, "通知成功"),
    NOTIFYFAIL(6, "通知失败"),
    AUDIT(7, "等待审核"),
    AUDITSUCCESS(8, "审核成功"),
    AUDITFAIL(9, "审核失败"),
    ;



    private int code;

    private String msg;

    OrderStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
