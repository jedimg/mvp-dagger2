package com.example.bradcampbell;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.squareup.spoon.Spoon.screenshot;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static rx.Observable.just;
import static rx.android.schedulers.AndroidSchedulers.mainThread;

import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.example.bradcampbell.domain.HelloEntity;
import com.example.bradcampbell.domain.HelloModel;
import com.example.bradcampbell.ui.HelloFragment;
import com.example.bradcampbell.ui.MainActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.TestScheduler;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HelloFragmentTests {
  @Rule public final ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class, false, false);

  @Rule public final TestAppComponentRule componentRule = new TestAppComponentRule();

  private HelloModel mockHelloModel;

  @Before public void setup() throws Throwable {
    // Set up application module
    mockHelloModel = mock(HelloModel.class);
    MockAppModule mockAppModule = componentRule.getMockAppModule();
    mockAppModule.setOverrideHelloModel(mockHelloModel);
    mockAppModule.setOverrideLayoutInflaterFactory((parent, name, context, attrs) -> {
      View result = null;
      // ProgressBar animates forever which keeps the main thread from being idle
      // and hence blocks Espresso from working. This is true even if you disable
      // animations on the device (testing on Android L and above it seems). I have
      // added a hook to replace any ProgressBar with a plain old View instance
      // to avoid the animation issues.
      if ("ProgressBar".equals(name)) {
        result = new View(context, attrs);
      }
      // We have overridden the Activity Delegate LayoutFactory, so we need to call
      // back into it
      if (result == null && context instanceof AppCompatActivity) {
        AppCompatActivity activity = (AppCompatActivity) context;
        AppCompatDelegate delegate = activity.getDelegate();
        result = delegate.createView(parent, name, context, attrs);
      }
      return result;
    });

    // Launch main activity
    main.launchActivity(MainActivity.getStartIntent(getTargetContext(), false));
  }

  @Test public void testLoadingDisplaysCorrectly() throws Throwable {
    TestScheduler testScheduler = new TestScheduler();
    setupMockHelloModelResult(testScheduler, 0, 0L);
    setupFragment();

    // Check loading is showing
    onView(withId(R.id.loading)).check(matches(isDisplayed()));
    onView(withId(R.id.text_view)).check(matches(not(isDisplayed())));

    screenshot(main.getActivity(), "loading");

    // Trigger onNext/onCompleted
    testScheduler.triggerActions();

    // Check loading is hidden
    onView(withId(R.id.loading)).check(matches(not(isDisplayed())));
    onView(withId(R.id.text_view)).check(matches(isDisplayed()));

    screenshot(main.getActivity(), "result");
  }

  private void setupMockHelloModelResult(Scheduler scheduler, int value, long timestamp) {
    Observable<HelloEntity> result = just(HelloEntity.create(value, timestamp))
        .subscribeOn(scheduler)
        .observeOn(mainThread());

    when(mockHelloModel.value()).thenReturn(result);
  }

  private void setupFragment() {
    FragmentManager fragmentManager = main.getActivity().getSupportFragmentManager();
    fragmentManager.beginTransaction()
        .replace(R.id.root, new HelloFragment())
        .commit();

    // Wait for the fragment to be committed
    Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
    instrumentation.waitForIdleSync();
  }
}
