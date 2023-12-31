package com.ruoyi.system.api;

import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.exception.user.UserException;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.system.api.model.LoginUser;
import com.ruoyi.system.api.model.XcxLoginUser;

/**
 * 用户服务
 *
 * @author Lion Li
 */
public interface RemoteConfigService {

    /**
     * 查询系统配置
     * @param key
     * @return
     */
    String getConfigValue(String key);
}
