package com.ruoyi.common.tenant.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.List;

/**
 * 多租户配置
 *
 * @author oathsign
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "tenant")
public class TenantProperties {

    private Boolean enable;
	/**
	 * 维护租户列名称
	 */
	private String column;

	/**
	 * 多租户的数据表集合
	 */
	private List<String> tables;

}
