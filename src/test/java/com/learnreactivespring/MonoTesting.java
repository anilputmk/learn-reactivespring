package com.learnreactivespring;

import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;

public class MonoTesting {

    @Test
    public void testScheduler() {
        Mono<String> callableMethod1 = callableMethod();
//        callableMethod1.block();

        StepVerifier.create(callableMethod1)
                .expectSubscription()
                .expectNext("Success")
                .verifyComplete();
//        Mono<String> callableMethod2 = callableMethod();
//        callableMethod2.block();
    }

    private Mono<String> callableMethod() {
        return Mono.fromCallable(() -> {
            Thread.sleep(80000);
            return "Success";
        })
                .subscribeOn(Schedulers.elastic())
//                .timeout(Duration.ofMillis(100))
                .onErrorResume(throwable -> Mono.just("Timeout"));
    }
}
