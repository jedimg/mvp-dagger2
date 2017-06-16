package com.example.bradcampbell.presentation;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rx.Observable.just;

import com.example.bradcampbell.BuildConfig;
import com.example.bradcampbell.TestApp;
import com.example.bradcampbell.domain.HelloEntity;
import com.example.bradcampbell.domain.HelloModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import rx.Observable;
import rx.schedulers.TestScheduler;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        application = TestApp.class,
        sdk = 21)
public class HelloPresenterTests {
  private HelloModel mockModel;
  private HelloView mockView;

  @Before public void setup() {
    mockModel = mock(HelloModel.class);
    mockView = mock(HelloView.class);
  }

  @Test public void testLoadingIsCalledCorrectly() {
    TestScheduler testScheduler = new TestScheduler();
    Observable<HelloEntity> result = just(HelloEntity.create(0, 0)).subscribeOn(testScheduler);
    when(mockModel.value()).thenReturn(result);

    HelloPresenter presenter = new HelloPresenter(mockModel);
    presenter.setView(mockView);

    verify(mockView, times(1)).showLoading();
    verify(mockView, never()).hideLoading();
    verify(mockView, never()).display(anyString());

    testScheduler.triggerActions();

    verify(mockView, times(1)).display("0");
    verify(mockView, times(1)).hideLoading();
  }
}
