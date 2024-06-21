package io.github.hefrankeleyn.hefgateway.plugins;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

/**
 * @Date 2024/6/20
 * @Author lifei
 */
public class DefaultGatewayPluginChain implements GatewayPluginChain{

    private List<GatewayPlugin> pluginList;
    private int i;

    public DefaultGatewayPluginChain(List<GatewayPlugin> pluginList) {
        this.pluginList = pluginList;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        return Mono.defer(()->{
            if (Objects.nonNull(pluginList) && i<pluginList.size()) {
                return pluginList.get(i++).handle(exchange, this);
            }
            return Mono.empty();
        });
    }
}
