package com.ruoyi.common.tenant.interceptor;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.tenant.tenant.TenantContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * @author cutecloud
 * @date 2020/4/29
 * <p>
 * 传递 RestTemplate 请求的租户ID
 */
@Slf4j
public class TenantRequestInterceptor implements ClientHttpRequestInterceptor {

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {

		if (TenantContextHolder.getTenantId() != null) {

			request.getHeaders().set(Constants.TENANT_ID, String.valueOf(TenantContextHolder.getTenantId()));
		}

		return execution.execute(request, body);
	}

}
