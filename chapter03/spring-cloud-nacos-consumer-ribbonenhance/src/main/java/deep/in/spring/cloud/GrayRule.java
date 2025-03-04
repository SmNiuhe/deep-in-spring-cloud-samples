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

import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.Server;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @RibbonClients(defaultConfiguration = {GrayRule.class})
 * 通过@RibbonClients 配置默认的Configuration，优先级最高影响所有的 @RibbonClient
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class GrayRule extends AbstractLoadBalancerRule {

    private Random random = new Random();

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {

    }

    // 重写IRule#choose方法，进行灰度调用控制
    // 进行灰度发布的服务，通过元数据直接筛选出原来后进行随机访问
    @Override
    public Server choose(Object key) {
        boolean grayInvocation = false;
        try {
            String grayTag = RibbonRequestContextHolder.getCurrentContext().get("Gray");
            if(!StringUtils.isEmpty(grayTag) && grayTag.equals(Boolean.TRUE.toString())) {
                grayInvocation = true;
            }

            List<Server> serverList = this.getLoadBalancer().getReachableServers();
            List<Server> grayServerList = new ArrayList<>();
            List<Server> normalServerList = new ArrayList<>();
            for(Server server : serverList) {
                NacosServer nacosServer = (NacosServer) server;
                if(nacosServer.getMetadata().containsKey("gray") && nacosServer.getMetadata().get("gray").equals("true")) {
                    grayServerList.add(server);
                } else {
                    normalServerList.add(server);
                }
            }

            if(grayInvocation) {
                return grayServerList.get(random.nextInt(grayServerList.size()));
            } else {
                return normalServerList.get(random.nextInt(normalServerList.size()));
            }
        } finally {
            RibbonRequestContextHolder.clearContext();
        }
    }

}
