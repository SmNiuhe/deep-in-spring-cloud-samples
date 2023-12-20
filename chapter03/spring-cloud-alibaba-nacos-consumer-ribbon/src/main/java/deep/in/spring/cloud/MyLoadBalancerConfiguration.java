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

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;

/**
 * configuration属性表示配置类，配置类中返回的 Bean会替换RibbonClientConfiguration配置类中已经存在的Bean（前文提到SpringClientFactory内部维护着一个Map，
 * 这个Map用于保存各个服务的ApplicationContext。每个 ApplicationContext构造的时候都会加上RibbonClient-Configuration配置类）。
 * 比如RibbonClientConfiguration配置类中负载均衡策略为ZoneAvoidanceRule （一种基于可用区zone和过滤规则的负载均衡策略）：
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class MyLoadBalancerConfiguration {

    @Bean
    public IRule iRule() {
        System.out.println("random rule active");
        return new RandomRule();
    }

}
