package io.github.hefrankeleyn.hefgateway.conf;

import cn.hefrankeleyn.hefrpc.core.api.LoadBalance;
import cn.hefrankeleyn.hefrpc.core.api.RegistryCenter;
import cn.hefrankeleyn.hefrpc.core.api.Router;
import cn.hefrankeleyn.hefrpc.core.cluster.RandomLoadBalance;
import cn.hefrankeleyn.hefrpc.core.meta.InstanceMeta;
import cn.hefrankeleyn.hefrpc.core.registry.hef.HefRegistryCenter;
import io.github.hefrankeleyn.hefgateway.handler.GatewayHandler;
import io.github.hefrankeleyn.hefgateway.handler.GatewayWebHandler;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.server.WebHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @Date 2024/6/11
 * @Author lifei
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RegistryCenter registryCenter() {
        return new HefRegistryCenter();
    }

    @Bean
    public Router<InstanceMeta> router() {
        return Router.DEFAULT;
    }

    @Bean
    public LoadBalance<InstanceMeta> loadBalance() {
        return new RandomLoadBalance<>();
    }


    @Bean
    public ApplicationRunner runner(ApplicationContext applicationContext) {
        return args->{
            SimpleUrlHandlerMapping simpleUrlHandlerMapping = applicationContext.getBean(SimpleUrlHandlerMapping.class);
            Properties mappings = new Properties();
            mappings.put("/gw/**", "gatewayWebHandler");
            mappings.put("/ga/**", "gatewayWebHandler");
            simpleUrlHandlerMapping.setMappings(mappings);
            simpleUrlHandlerMapping.initApplicationContext();
        };
    }

//    @Bean(name = "gatewaySimpleUrlHandlerMapping")
//    public SimpleUrlHandlerMapping gatewaySimpleUrlHandlerMapping(GatewayWebHandler gatewayWebHandler) {
//        SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
//        Map<String, Object> urlMap = new HashMap<>();
//        urlMap.put("/gw/**", gatewayWebHandler);
//        urlMap.put("/ga/**", gatewayWebHandler);
//        simpleUrlHandlerMapping.setUrlMap(urlMap);
//        // 必须添加这句话
//        simpleUrlHandlerMapping.setOrder(1);
//        return simpleUrlHandlerMapping;
//    }

}
