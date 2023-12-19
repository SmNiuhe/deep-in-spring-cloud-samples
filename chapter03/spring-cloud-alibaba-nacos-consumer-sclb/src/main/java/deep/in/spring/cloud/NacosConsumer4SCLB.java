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
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
@EnableDiscoveryClient(autoRegister = true)
//@LoadBalancerClients(defaultConfiguration = MyLoadBalancerConfiguration.class)
public class NacosConsumer4SCLB {

    public static void main(String[] args) {
        SpringApplication.run(NacosConsumer4SCLB.class, args);
    }


    @Configuration
    @LoadBalancerClient(name = "nacos-provider-lb", configuration = MyLoadBalancerConfiguration.class)
    class LoadBalanceConfiguration {

    }

    // LoadBalancerAutoConfiguration 配置类处理
    // 1 获取ApplicationContext中所有被@LoadBalanced注释修饰的RestTemplate
    // 2. List<RestTemplateCustomizer> 是ApplicationContext 存在的RestTemplateCustomizer Bean的集合。
    // 3. 遍历代码1出得到的 RestTemplate集合，并使用 RestTemplateCustomizer集合给每个RestTemplate定制

    // LoadBalancerClient继承ServiceInstanceChooser，会根据ServiceInstance和Request请求信息执行最终的结果
    // 1.BlockingLoadBalancerClient SpringCloud LoadBalancer默认的实现，不过功能点没有Ribbon的完善
    // 2. RibbonLoadBalancerClient netflix.ribbon 实现了改接口，Ribbon 在时机工作中还是相对于SpringCloud的默认实现还是用的更广泛
    // 3. ·reconstructURI方法。这个方法用于重新构造URI。比如，要访问nacos-provider-lb服务下的“/”路径，
    // 这个URI为http：//nacos-provider-lb/。nacos-provider-lb 服务在注册中心有10个服务实例，
    // 某个服务实例ServiceInstance的IP为192.168.1.100，端口为8080。那么重新构造的真正URI为http：//192.168.1.100：8080/。

    // LoadBalancerRequest 表示一次负载均衡请求，会被LoadBalancerRequestFactory 构造。
    // 构造出的负载均衡请求实现类是ServiceRequestWrapper（内部基于服务实例和请求信息构造出真正的 URI）。
    // 然后根据 LoadBalancerRequestTransformer 做二次加工。LoadBalancerRequest 接口定义如下：
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RestTemplate normalRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RandomServiceInstanceChooser randomServiceInstanceChooser(DiscoveryClient discoveryClient) {
        return new RandomServiceInstanceChooser(discoveryClient);
    }

    @RestController
    class HelloController {

        @Autowired
        private RestTemplate restTemplate;

        @Autowired
        private RestTemplate normalRestTemplate;

        @Autowired
        private RandomServiceInstanceChooser randomServiceInstanceChooser;

        private String serviceName = "nacos-provider-lb";

        @GetMapping("/echo")
        public String echo() {
            return restTemplate.getForObject("http://" + serviceName + "/", String.class);
        }

        @GetMapping("/customChooser")
        public String customChooser() {
            ServiceInstance serviceInstance = randomServiceInstanceChooser.choose(serviceName);
            return normalRestTemplate.getForObject(
                "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/", String.class);
        }

    }

}
