package io.github.hefrankeleyn.hefgateway.plugins;

import com.google.common.base.Strings;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Date 2024/6/20
 * @Author lifei
 */
public abstract class AbstractGatewayPlugin implements GatewayPlugin {

    @Override
    public String gatewayPrefix() {
        return Strings.lenientFormat("%s/%s/", GATEWAY_PREFIX, this.getName());
    }

    @Override
    public boolean support(ServerWebExchange exchange) {
        return exchange.getRequest().getPath().value().startsWith(this.gatewayPrefix());
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, GatewayPluginChain chain) {
        boolean isSupport = support(exchange);
        System.out.println(Strings.lenientFormat("====> plugin [%s], support = %s", this.getName(), isSupport));
        return isSupport? doHandle(exchange, chain): chain.handle(exchange);
    }

    public abstract Mono<Void> doHandle(ServerWebExchange exchange, GatewayPluginChain chain);
}
