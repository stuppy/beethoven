package beethoven;

import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class Play extends UiAutomatorTestCase {

  public void testWakeUp() throws Exception {
    if (!getUiDevice().isScreenOn()) {
      getUiDevice().wakeUp();
    }
    assertTrue(getUiDevice().isScreenOn());
  }
}
