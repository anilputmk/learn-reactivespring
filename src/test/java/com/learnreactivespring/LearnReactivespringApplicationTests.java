package com.learnreactivespring;

import lombok.SneakyThrows;
import org.junit.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.net.HttpRetryException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static java.lang.Thread.sleep;
import static reactor.core.scheduler.Schedulers.parallel;

public class LearnReactivespringApplicationTests {

	// Factory methods.
	@SneakyThrows
	@Test
	public void testFluxWithoutError() {
		Flux<String> fluxStrings = Flux.just("Flux","Merge")
				.concatWith(Flux.error(new RuntimeException()));

		fluxStrings.subscribe(System.out::println, e-> System.err.println(e), () -> System.out.println("Completed"));

		StepVerifier.create(fluxStrings)
				.expectNext("Flux", "Merge")
				.expectError(RuntimeException.class)
				.verify();
	}

	@Test
	public void testMonoWithoutError() {
		Mono<String> mono = Mono.just("Hello");

		StepVerifier.create(mono).expectNext("Hello").verifyComplete();
	}

	@Test
	public void fluxUsingIterable() {
		List<String> names = Arrays.asList("adab","asdasd","dfdgs");
		Flux<String> iterables = Flux.fromIterable(names);

		StepVerifier.create(iterables)
				.expectNext("adab","asdasd","dfdgs")
				.verifyComplete();

		String[] names1 = new String[] {"adab","asdasd","dfdgs"};
		Flux<String> arrayNames = Flux.fromArray(names1);

		StepVerifier.create(arrayNames)
				.expectNext("adab","asdasd","dfdgs")
				.verifyComplete();

		Flux<String> namesStream = Flux.fromStream(names.stream());

		StepVerifier.create(namesStream)
				.expectNext("adab","asdasd","dfdgs")
				.verifyComplete();

		Flux<Integer> intFlux = Flux.range(1,5).log();

		StepVerifier.create(intFlux)
				.expectNext(1,2,3,4,5)
				.verifyComplete();
	}

	@Test
	public void MonoTesting() {
		Mono<String> mono = Mono.justOrEmpty(null);

		StepVerifier.create(mono)
				.verifyComplete();

		Supplier<String> stringSupplier = () -> "Hello";
		Mono<String> monoFromSupplier = Mono.fromSupplier(stringSupplier);

		StepVerifier.create(monoFromSupplier)
				.expectNext("Hello")
				.verifyComplete();
	}

	@Test
	public void flatMapParallel() {
		Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("AA","BB","CC","DD","FF"))
				.window(2)
				.flatMap( s -> s.map(this::returnList).subscribeOn(parallel()))
				.flatMap(s -> Flux.fromIterable(s))
				.log();

//		StepVerifier.create(stringFlux)
//				.expectNextCount(10)
//				.verifyComplete();

		Flux<String> stringFlux2 = Flux.fromIterable(Arrays.asList("AA","BB","CC","DD","FF"))
				.window(2)
				.flatMapSequential( s -> s.map(this::returnList).subscribeOn(parallel()))
				.flatMap(s -> Flux.fromIterable(s))
				.log();

		StepVerifier.create(stringFlux2)
				.expectNextCount(10)
				.verifyComplete();
	}

	@SneakyThrows
	private List<String> returnList(String s) {
		sleep(1000);
		return Arrays.asList(s, "hello");
	}

	@Test
	public void testFluxMerge() {
		Flux<String> flux1 = Flux.just("A","B","C");
		Flux<String> flux2 = Flux.just("D","E","F");

		Flux<String> merge = Flux.merge(flux1, flux2);

		StepVerifier.create(merge)
				.expectSubscription()
				.expectNext("A","B","C","D","E","F")
				.verifyComplete();

		flux1 = Flux.just("A","B","C").delayElements(Duration.ofSeconds(1));
		flux2 = Flux.just("D","E","F").delayElements(Duration.ofSeconds(1));;

		merge = Flux.merge(flux1, flux2);

		StepVerifier.create(merge)
				.expectSubscription()
				.expectNextCount(6)
				.verifyComplete();
	}

	@Test
	public void testFluxConcat() {
		Flux<String> flux1 = Flux.just("A","B","C").delayElements(Duration.ofSeconds(1));
		Flux<String> flux2 = Flux.just("D","E","F").delayElements(Duration.ofSeconds(1));

		Flux<String> merge = Flux.concat(flux1, flux2);

		StepVerifier.create(merge)
				.expectSubscription()
				.expectNext("A","B","C","D","E","F")
				.verifyComplete();

		Flux<String> flux3 = Flux.error(new RuntimeException("Error"));

		merge = Flux.concat(flux1, flux3, flux2);

		StepVerifier.create(merge.log())
				.expectSubscription()
				.expectNext("A","B","C","D","E","F")
				.expectError(RuntimeException.class)
				.verify();
	}

	@Test
	public void errorHandling_withResume() {
		Flux<String> errorFlux = Flux.just("A","B","C")
				.concatWith(Flux.error(new RuntimeException("Error")))
				.concatWith(Flux.just("D"));

		StepVerifier.create(errorFlux.log())
				.expectSubscription()
				.expectNext("A","B","C")
				.expectError(RuntimeException.class)
				.verify();

		errorFlux = Flux.just("A","B","C")
				.concatWith(Flux.error(new RuntimeException("Error")))
				.concatWith(Flux.just("D"))
				.onErrorResume((e) -> {
					System.out.println("Error");
					return Flux.just("default");
				});

		StepVerifier.create(errorFlux.log())
				.expectSubscription()
				.expectNext("A","B","C")
				.expectNext("default")
				.verifyComplete();
	}

	@Test
	public void errorHandling_withReturn() {
		Flux<String> errorFlux = Flux.just("A","B","C")
				.concatWith(Flux.error(new RuntimeException("Error")))
				.concatWith(Flux.just("D"))
				.onErrorReturn("default");

		StepVerifier.create(errorFlux.log())
				.expectSubscription()
				.expectNext("A","B","C")
				.expectNext("default")
				.verifyComplete();
	}

	@Test
	public void errorHandling_Map() {
		Flux<String> errorFlux = Flux.just("A","B","C")
				.concatWith(Flux.error(new RuntimeException("Error")))
				.concatWith(Flux.just("D"))
				.onErrorMap(e -> new HttpRetryException("", 0));

		StepVerifier.create(errorFlux.log())
				.expectSubscription()
				.expectNext("A","B","C")
				.expectError(HttpRetryException.class)
				.verify();

		errorFlux = Flux.just("A","B","C")
				.concatWith(Flux.error(new RuntimeException("Error")))
				.concatWith(Flux.just("D"))
				.onErrorMap(e -> new HttpRetryException("", 0))
				.retry(2);

		StepVerifier.create(errorFlux.log())
				.expectSubscription()
				.expectNext("A","B","C")
				.expectNext("A","B","C")
				.expectNext("A","B","C")
				.expectError(HttpRetryException.class)
				.verify();
	}

	@Test
	public void infiniteFlux() throws InterruptedException {
		Flux<Long> infinite = Flux.interval(Duration.ofMillis(200))
				.log();

		infinite.subscribe(element -> System.out.println("value = "+element));
		Thread.sleep(3000);
	}

	@Test
	public void finiteFLux() {
		Flux<Long> finite = Flux.interval(Duration.ofMillis(200))
				.take(3)
				.log();

		StepVerifier.create(finite)
				.expectSubscription()
				.expectNext(0L, 1L, 2L)
				.verifyComplete();

		Flux<Integer> finiteInt = Flux.interval(Duration.ofMillis(200))
				.delayElements(Duration.ofMillis(200))
				.map(element -> new Integer(element.intValue()))
				.take(3)
				.log();

		StepVerifier.create(finiteInt)
				.expectSubscription()
				.expectNext(0,1,2)
				.verifyComplete();
	}

	@Test
	public void backPressureTest() {
		Flux<Integer> finiteFlux = Flux.range(1,10).log();

		StepVerifier.create(finiteFlux)
				.expectSubscription()
				.thenRequest(1)
				.expectNext(1)
				.thenRequest(2)
				.expectNext(2)
				.thenCancel().verify();

		finiteFlux.subscribe(element -> System.out.println("Element is "+element),
				(e) -> System.err.println(e),
				() -> System.out.println("Completed"),
				(subscription) -> subscription.request(2));

		finiteFlux.subscribe(element -> System.out.println("Element is "+element),
				(e) -> System.err.println(e),
				() -> System.out.println("Completed"),
				(subscription) -> subscription.cancel());

		finiteFlux.subscribe(new BaseSubscriber<Integer>() {
			@Override
			protected void hookOnNext(Integer value) {
				request(1);
				System.out.println("value is "+value);
				if(value==4) {
					cancel();
				}
			}
		});
	}

	@SneakyThrows
	@Test
	public void coldPublisherTest() {
		Flux<String> coldPublisher = Flux.just("A","B","C","D","E","F")
				.delayElements(Duration.ofSeconds(1));

		coldPublisher.subscribe(element -> System.out.println("Sub 1 value:" + element));
		Thread.sleep(2000);

		coldPublisher.subscribe(element -> System.out.println("Sub 2 value:" + element));
		Thread.sleep(4000);
	}

	@Test
	@SneakyThrows
	public void hotPubTest() {
		Flux<String> stringFlux = Flux.just("A","B","C","D","E","F")
				.delayElements(Duration.ofSeconds(1));

		ConnectableFlux<String> publish = stringFlux.publish();
		publish.connect();

		publish.subscribe(element -> System.out.println("Sub 1 value:" + element));
		Thread.sleep(2000);

		publish.subscribe(element -> System.out.println("Sub 2 value:" + element));
		Thread.sleep(4000);
	}

	@Test
	public void virtualTimeTest() {
		VirtualTimeScheduler.getOrSet();

		Flux<Long> stringFlux = Flux.interval(Duration.ofSeconds(1))
				.take(3);

		StepVerifier.withVirtualTime(() -> stringFlux.log())
				.expectSubscription()
				.thenAwait(Duration.ofSeconds(3))
				.expectNext(0l, 1l, 2l)
				.verifyComplete();
	}
}
