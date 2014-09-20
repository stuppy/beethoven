package beethoven;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

/**
 * Play test case that "beats" the Classic Pro level.
 */
public class Play extends UiAutomatorTestCase {

  /**
   * For the purposes of white vs. black tile, this is "black."
   */
  private static final int BLACK = Color.rgb(32, 32, 32);

  /**
   * Click row.
   */
  private static final int Y = 700;

  /**
   * Test!
   *
   * <p>This one uses screenshots to pinpoint the location of the next tile!
   */
  public void testClassicPro() throws Exception {
    UiObject tile = new UiObject(
        new UiSelector()
            .packageName("com.umonistudio.tile")
            .className(android.view.View.class));
    assertTrue(tile.exists());

    X x = new X(50);
    while (x.hasNext()) {
      getUiDevice().click(x.next(), Y);
    }
  }

  /**
   * Iterates through the X values to click on at the {@link #Y} row.
   */
  private class X implements Iterator<Integer> {
    private final int length;

    private int count = 0;
    private Bitmap bmp;

    private X(int length) {
      this.length = length;
    }

    public boolean hasNext() {
      return count < length;
    }

    public Integer next() {
      int mod = count % 3;
      if (mod == 0) {
        bmp = takeScreenshot();
      }
      int y = Y - mod * 300;
      count++;
      for (int x = 90; x <= 720; x += 180) {
        int pixel = bmp.getPixel(x, y);
        if (pixel == BLACK) {
          return x;
        }
      }
      // Delay until needed.
      StringBuilder colors = new StringBuilder();
      for (int x = 90; x <= 720; x += 180) {
        int pixel = bmp.getPixel(x, y);
        colors.append(Utils.color(pixel)).append(";");
      }
      throw new AssertionError("No BLACK @ y=" + y + "! " + colors);
    }

    public void remove() {
      throw new UnsupportedOperationException("Not supported!");
    }
  }

  /**
   * Take a screenshot.
   *
   * <p>The basic call chain is:
   * UiDevice.getInstance().getAutomatorBridge().mUiAutomation.takeScreenshot()
   *
   * <p>This is a super hack that uses private/hidden methods to avoid creating a file, which is
   * slow.
   */
  private Bitmap takeScreenshot() {
    try {
      UiDevice device = UiDevice.getInstance();

      Method getAutomatorBridge = device.getClass().getDeclaredMethod("getAutomatorBridge");
      getAutomatorBridge.setAccessible(true);
      Object automatorBridge = getAutomatorBridge.invoke(device);

      // UiAutomatorBridge is abstract, so use the super class (UiAutomatorBridge).
      Field mUiAutomation =
          automatorBridge.getClass().getSuperclass().getDeclaredField("mUiAutomation");
      mUiAutomation.setAccessible(true);
      Object uiAutomation = mUiAutomation.get(automatorBridge);

      Method takeScreenshot = uiAutomation.getClass().getDeclaredMethod("takeScreenshot");
      takeScreenshot.setAccessible(true);
      return (Bitmap) takeScreenshot.invoke(uiAutomation);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
