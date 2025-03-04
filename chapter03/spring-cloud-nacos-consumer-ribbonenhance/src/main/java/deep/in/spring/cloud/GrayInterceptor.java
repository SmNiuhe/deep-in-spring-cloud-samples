/*
 * Copyright (C) 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package deep.in.spring.cloud;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

/**
 *
 * 编写流量识别能力的代码。定义 GrayInterceptor，用于拦截请求，并基于请求的特征判断是否是一次灰度调用
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class GrayInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {
        // Ribbon LoadBalancer
        // 应用流量控制需要以下两个能力，流量识别能力和实例打标能力
        if (request.getHeaders().containsKey("Gray")) {
            String value = request.getHeaders().getFirst("Gray");
            if (value.equals("true")) {
                RibbonRequestContextHolder.getCurrentContext().put("Gray", Boolean.TRUE.toString());
                ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
                attributes.setAttribute("Gray", Boolean.TRUE.toString(), 0);
            }
        }
        return execution.execute(request, body);
    }

}
