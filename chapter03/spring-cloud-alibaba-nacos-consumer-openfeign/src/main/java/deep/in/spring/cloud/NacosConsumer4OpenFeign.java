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

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OpenFeign对JAX-RS的支持
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@SpringBootApplication
@EnableDiscoveryClient(autoRegister = false)
@EnableFeignClients
public class NacosConsumer4OpenFeign {

    public static void main(String[] args) {
        SpringApplication.run(NacosConsumer4OpenFeign.class, args);
    }

    @FeignClient(name = "nacos-provider-lb", configuration = MyOpenFeignConfiguration.class, contextId = "jaxrsFeign")
    interface EchoServiceJAXRS {

        @GET
        @Path("/")
        String echo();

    }

    @FeignClient(name = "nacos-provider-lb", contextId = "springmvcFeign")
    interface EchoServiceSpringMVC {

        @GetMapping("/")
        String echo();

    }

    @RestController
    class HelloController {

        @Autowired
        EchoServiceJAXRS echoServiceJAXRS;

        @Autowired
        EchoServiceSpringMVC echoServiceSpringMVC;

        @GetMapping("/jaxrs")
        public String jaxrs() {
            return echoServiceJAXRS.echo();
        }

        @GetMapping("/springmvc")
        public String springmvc() {
            return echoServiceSpringMVC.echo();
        }

    }

}
