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

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.ServiceInstanceChooser;

import java.util.List;
import java.util.Random;

/**
 * 自定义一个 ServiceInstanceChooser 的实现类来完成负债均衡操作
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class RandomServiceInstanceChooser implements ServiceInstanceChooser {

    private final DiscoveryClient discoveryClient;

    private final Random random;

    public RandomServiceInstanceChooser(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
        random = new Random();
    }

    /**
     * Spring Cloud 编程模型的抽象，直接定义ServiceInstance服务实例接口 nacos和eureka 都实现了对应接口 com.alibaba.cloud.nacos.NacosServiceInstance
     * org.springframework.cloud.netflix.eureka.EurekaServiceInstance
     *
     * @param serviceId The service ID to look up the LoadBalancer.
     * @return
     */
    @Override
    public ServiceInstance choose(String serviceId) {
        List<ServiceInstance> serviceInstanceList =
            discoveryClient.getInstances(serviceId);
        return serviceInstanceList.get(random.nextInt(serviceInstanceList.size()));
    }
}
