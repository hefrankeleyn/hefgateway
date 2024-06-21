package io.github.hefrankeleyn.hefgateway.web.handler;

import io.github.hefrankeleyn.hefgateway.plugins.DefaultGatewayPluginChain;
import io.github.hefrankeleyn.hefgateway.plugins.GatewayPlugin;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

/**
 * @Date 2024/6/18
 * @Author lifei
 */
@Component(value = "gatewayWebHandler")
public class GatewayWebHandler implements WebHandler {

    private static final Logger log = LoggerFactory.getLogger(GatewayWebHandler.class);

    @Resource
    private List<GatewayPlugin> pluginList;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        log.debug("====> [gatewayWebHandler] handling request");
        if (Objects.isNull(pluginList) || pluginList.isEmpty()) {
            // {"result": "none plugins"}
            return createOtherMonoResult("{\"result\": \"none plugins\"}", exchange);
        }
//        for (GatewayPlugin plugin : pluginList) {
//            if (plugin.support(exchange)) {
//                return plugin.handle(exchange);
//            }
//        }
        return new DefaultGatewayPluginChain(pluginList).handle(exchange);
//        return createOtherMonoResult("{\"result\": \"none support plugins\"}", exchange);
    }

    private static Mono<Void> createOtherMonoResult(String result, ServerWebExchange exchange) {
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(result.getBytes())));
    }
}
