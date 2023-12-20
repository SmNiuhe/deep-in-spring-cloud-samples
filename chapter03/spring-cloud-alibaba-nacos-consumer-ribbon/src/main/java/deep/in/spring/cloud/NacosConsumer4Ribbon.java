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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Netflix Ribbon是 Netflix开源的客户端负载均衡组件，在 Spring Cloud LoadBalancer出现之前，它是Spring Cloud生态里唯一的负载均衡组件。
 * 目前市场上绝大多数的Spring Cloud应用还是使用Ribbon作为其负载均衡组件。
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 *
 * @RibbonClient ，自定配置都可以替換，本身 SpringFactory 对应没个服务实例都有单独的配置上下文
 * 提示：@RibbonClients 注解的 defaultConfiguration 属性表示默认的配置类，所有的RibbonLoadBalancerClient都会使用这些配置类里的配置。
 *
 * nacos-discovery 包下默认的负载均衡器是 netflix-ribbon，RibbonLoadBalancerClient
 */
@SpringBootApplication
@EnableDiscoveryClient(autoRegister = false)
//@RibbonClient(name = "nacos-provider-lb", configuration = MyLoadBalancerConfiguration.class)
public class NacosConsumer4Ribbon {

    public static void main(String[] args) {
        SpringApplication.run(NacosConsumer4Ribbon.class, args);
    }

    // 1. SpringClientFactory 是一个与 LoadBalancerClientFactory 作用类似的工厂类，其内部维护着一个 Map，
    // 这个 Map用于保存各个服务的ApplicationContext（Map的 key表示服务名）。每个ApplicationContext内部维护对应服务的一些配置和Bean。

    // 2SpringClientFactory在RibbonAutoConfiguration自动化配置类中被构造，可以通过构造器注入的方式注入。

    // 3. reconstructURI方法与Spring Cloud LoadBalancer中的BlockingLoadBalancerClient实现完全不一样。BlockingLoadBalancerClient
    // 直接委托给 LoadBalancerUriTools＃reconstructURI 方法实现，其内部使用ServiceInstance 进行相应的属性替换；而 RibbonLoadBalancerClient
    // 内部基于新的类 com.netflix.loadbalancer.Server（表示一个服务器实例，内部有 host、port、schema、zone等属性）来实现。
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RestTemplate normalRestTemplate() {
        return new RestTemplate();
    }

    @RestController
    class HelloController {

        @Autowired
        private RestTemplate restTemplate;

        @Autowired
        private RestTemplate normalRestTemplate;

        private String serviceName = "nacos-provider-lb";

        @GetMapping("/echo")
        public String echo() {
            return restTemplate.getForObject("http://" + serviceName + "/", String.class);
        }

    }

}
