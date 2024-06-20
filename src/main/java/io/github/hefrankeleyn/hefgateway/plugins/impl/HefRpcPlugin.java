package io.github.hefrankeleyn.hefgateway.plugins.impl;

import cn.hefrankeleyn.hefrpc.core.api.LoadBalance;
import cn.hefrankeleyn.hefrpc.core.api.RegistryCenter;
import cn.hefrankeleyn.hefrpc.core.api.Router;
import cn.hefrankeleyn.hefrpc.core.meta.InstanceMeta;
import cn.hefrankeleyn.hefrpc.core.meta.ServiceMeta;
import io.github.hefrankeleyn.hefgateway.plugins.AbstractGatewayPlugin;
import io.github.hefrankeleyn.hefgateway.plugins.GatewayPlugin;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Date 2024/6/20
 * @Author lifei
 */
@Component("hefRpcPlugin")
public class HefRpcPlugin extends AbstractGatewayPlugin {

    private static final Logger log = LoggerFactory.getLogger(HefRpcPlugin.class);

    private static final String NAME = "hefrpc";

    @Resource
    private RegistryCenter registryCenter;

    @Resource
    private Router<InstanceMeta> router;

    @Resource
    private LoadBalance<InstanceMeta> loadBalance;

    @Override
    public Mono<Void> doHandle(ServerWebExchange exchange) {
        log.debug("====> [HefRpcPlugin] doHandle....");
        // 1. 从路径中获取服务名
        String service = exchange.getRequest().getPath().value().substring(gatewayPrefix().length());
        // 2. 从注册中心拿到服务的列表
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .name(service)
                .app("app1")
                .env("dev")
                .namespace("public")
                .build();
        // 3. 负载均衡获取请求实例
        List<InstanceMeta> instanceMetaList = registryCenter.findAll(serviceMeta);
        InstanceMeta instanceMeta = loadBalance.choose(router.route(instanceMetaList));
        String url = instanceMeta.toUrl();
        // 4. 获取请求报文
        Flux<DataBuffer> requestBody = exchange.getRequest().getBody();
        // 5. 通过WebClient 发送post请求
        WebClient webClient = WebClient.create(url);
        Mono<ResponseEntity<String>> entity = webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody, DataBuffer.class).retrieve().toEntity(String.class);
        // 6. 获取响应报文
        Mono<String> responseBody = entity.map(ResponseEntity::getBody);
        exchange.getResponse().getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        exchange.getResponse().getHeaders().add("plugin.rpc.version", "v0.0.1");
        return responseBody.flatMap(res -> exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(res.getBytes()))));
    }

    @Override
    public String getName() {
        return NAME;
    }
}
