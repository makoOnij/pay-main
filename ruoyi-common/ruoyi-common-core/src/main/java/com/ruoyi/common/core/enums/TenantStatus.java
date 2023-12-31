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
public enum TenantStatus {

    CLOSE(1, "停用"), //请求成功

    OPEN(0, "开启");


    private int code;

    private String msg;

    TenantStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
