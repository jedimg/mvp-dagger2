package com.example.bradcampbell.data;

import rx.Observable;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * This is faking a Retrofit service returning an observable
 */
public class HelloService {
  public Observable<Integer> getValue() {
    return Observable.defer(() -> {
      Random rand = new Random();
      return Observable.just(rand.nextInt(50));
    }).delay(3, TimeUnit.SECONDS);
  }
}
