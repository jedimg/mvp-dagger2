package com.example.bradcampbell;

import static android.support.test.InstrumentationRegistry.getTargetContext;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Test rule that provides a {@link MockAppModule} before each test, and clears the
 * {@link App} component after each test automatically
 */
public class TestAppComponentRule implements TestRule {
  private MockAppModule mockAppModule;

  @Override public Statement apply(Statement base, Description description) {
    return new Statement() {
      @Override public void evaluate() throws Throwable {
        TestApp app = (TestApp) getTargetContext().getApplicationContext();
        try {
          mockAppModule = new MockAppModule(app);
          app.setOverrideModule(mockAppModule);
          base.evaluate();
        } finally {
          App.clearAppComponent(app);
        }
      }
    };
  }

  public MockAppModule getMockAppModule() {
    return mockAppModule;
  }
}
