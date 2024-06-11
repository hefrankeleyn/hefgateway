package io.github.hefrankeleyn.hefgateway;

import cn.hefrankeleyn.hefrpc.core.api.LoadBalance;
import cn.hefrankeleyn.hefrpc.core.api.RegistryCenter;
import cn.hefrankeleyn.hefrpc.core.api.Router;
import cn.hefrankeleyn.hefrpc.core.meta.InstanceMeta;
import cn.hefrankeleyn.hefrpc.core.meta.ServiceMeta;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Date 2024/6/11
 * @Author lifei
 */
@Component
public class GatewayHandler {

    @Resource
    private RegistryCenter registryCenter;

    @Resource
    private Router<InstanceMeta> router;

    @Resource
    private LoadBalance<InstanceMeta> loadBalance;

    public Mono<ServerResponse> handle(ServerRequest request) {
        // 获取路径
        String subUrl = request.path().substring(1);
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
            Mono<String> requestBodyMono = request.bodyToMono(String.class);
            return requestBodyMono.flatMap(x-> invokeFromRegistry(x, url));
        } else {
            return ServerResponse.ok().body(Mono.just("Hello, hef gateway!"), String.class);
        }
    }

    private static Mono<ServerResponse> invokeFromRegistry(String x, String url) {
        // 5. 通过WebClient 发送post请求
        WebClient webClient = WebClient.create(url);
        Mono<ResponseEntity<String>> entity = webClient.post()
                .header("Content-Type", "application/json")
                .bodyValue(x).retrieve().toEntity(String.class);
        // 6. 获取响应报文
        Mono<String> responseBody = entity.map(ResponseEntity::getBody);
        // 7. 组装响应报文
        return ServerResponse.ok()
                .header("Content-Type", "application/json")
                .header("hef.gw.version", "0.0.1")
                .body(responseBody, String.class);
    }
}
