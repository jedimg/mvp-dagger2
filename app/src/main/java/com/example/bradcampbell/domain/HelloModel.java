package com.example.bradcampbell.domain;

import com.example.bradcampbell.data.HelloDiskCache;
import com.example.bradcampbell.data.HelloService;
import rx.Observable;
import rx.Subscription;
import rx.subjects.ReplaySubject;

public class HelloModel {
  private static final long STALE_MS = 5 * 1000;

  private final SchedulerProvider schedulerProvider;
  private final HelloDiskCache diskCache;
  private final HelloService networkService;
  private final Clock clock;

  private HelloEntity memoryCache;
  private ReplaySubject<HelloEntity> helloSubject;
  private Subscription helloSubscription;

  public HelloModel(SchedulerProvider schedulerProvider, HelloDiskCache diskCache, HelloService networkService,
      Clock clock) {
    this.schedulerProvider = schedulerProvider;
    this.diskCache = diskCache;
    this.networkService = networkService;
    this.clock = clock;
  }

  public Observable<HelloEntity> value() {
    if (helloSubscription == null || helloSubscription.isUnsubscribed()) {
      helloSubject = ReplaySubject.create();

      helloSubscription = Observable.concat(memory(), disk(), network())
          .first(entity -> entity != null && isUpToDate(entity))
          .subscribe(helloSubject);
    }

    return helloSubject.asObservable();
  }

  public void clearMemoryCache() {
    memoryCache = null;
  }

  public void clearMemoryAndDiskCache() {
    diskCache.clear();
    clearMemoryCache();
  }

  private Observable<HelloEntity> network() {
    return networkService.getValue()
        .map(data -> HelloEntity.create(data, clock.millis()))
        .doOnNext(entity -> memoryCache = entity)
        .flatMap(entity -> diskCache.saveEntity(entity).map(__ -> entity))
        .compose(schedulerProvider.applySchedulers());
  }

  private Observable<HelloEntity> disk() {
    return diskCache.getEntity()
        .doOnNext(entity -> memoryCache = entity)
        .compose(schedulerProvider.applySchedulers());
  }

  private Observable<HelloEntity> memory() {
    return Observable.just(memoryCache);
  }

  private boolean isUpToDate(HelloEntity entity) {
    return clock.millis() - entity.timestamp() < STALE_MS;
  }
}
