package io.github.hefrankeleyn.hefgateway.handler;

import cn.hefrankeleyn.hefrpc.core.api.LoadBalance;
import cn.hefrankeleyn.hefrpc.core.api.RegistryCenter;
import cn.hefrankeleyn.hefrpc.core.api.Router;
import cn.hefrankeleyn.hefrpc.core.meta.InstanceMeta;
import cn.hefrankeleyn.hefrpc.core.meta.ServiceMeta;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Date 2024/6/18
 * @Author lifei
 */
@Component(value = "gatewayWebHandler")
public class GatewayWebHandler implements WebHandler {

    private static final Logger log = LoggerFactory.getLogger(GatewayWebHandler.class);

    @Resource
    private RegistryCenter registryCenter;

    @Resource
    private Router<InstanceMeta> router;

    @Resource
    private LoadBalance<InstanceMeta> loadBalance;


    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        log.debug("====> Hef Gateway web handler....");
        // 获取路径
        String subUrl = exchange.getRequest().getPath().value().substring(1);
        if (subUrl.indexOf("/")>0) {
            // 1. 获取服务名
            String service = subUrl.split("/")[1];
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
            exchange.getResponse().getHeaders().add("hef.gw.version", "v0.0.1");
            return responseBody.flatMap(res->exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(res.getBytes()))));
        } else {
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory()
                    .wrap("hello gateway web handler".getBytes())));
        }
    }
}
