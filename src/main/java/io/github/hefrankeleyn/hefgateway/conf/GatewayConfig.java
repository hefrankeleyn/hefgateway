package io.github.hefrankeleyn.hefgateway.conf;

import cn.hefrankeleyn.hefrpc.core.api.LoadBalance;
import cn.hefrankeleyn.hefrpc.core.api.RegistryCenter;
import cn.hefrankeleyn.hefrpc.core.api.Router;
import cn.hefrankeleyn.hefrpc.core.cluster.RandomLoadBalance;
import cn.hefrankeleyn.hefrpc.core.meta.InstanceMeta;
import cn.hefrankeleyn.hefrpc.core.registry.hef.HefRegistryCenter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
