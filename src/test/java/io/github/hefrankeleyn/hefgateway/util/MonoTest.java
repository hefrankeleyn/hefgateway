package io.github.hefrankeleyn.hefgateway.util;

import org.junit.Test;
import reactor.core.publisher.Mono;

/**
 * @Date 2024/6/20
 * @Author lifei
 */
public class MonoTest {

    @Test
    public void test01() {
        Mono<String> mono = Mono.just(getDate());
        Mono<String> deferMono = Mono.defer(() -> Mono.just(getDate()));
        mono.subscribe(System.out::println);
        deferMono.subscribe(System.out::println);

    }

    public String getDate() {
        System.out.println("Fetch data....");
        return "hello demo";
    }
}
