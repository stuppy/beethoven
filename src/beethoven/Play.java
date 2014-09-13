package beethoven;

import java.io.File;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class Play extends UiAutomatorTestCase {

  private static final UiSelector VIEW = new UiSelector()
      .packageName("com.umonistudio.tile")
      .className(android.view.View.class);

  private static final int BLACK = Color.rgb(32, 32, 32);

  public void testWakeUp() throws Exception {
    if (!getUiDevice().isScreenOn()) {
      getUiDevice().wakeUp();
    }
    assertTrue(getUiDevice().isScreenOn());
  }

  public void testRandomClick() throws Exception {
//    getUiDevice().click(20, 700);
    assertTrue(new UiObject(VIEW).exists());

    for (int i = 0; i < 50; i++) {
      getUiDevice().click(nextX(), 650);
    }
  }

  private int nextX() {
    Bitmap bmp = getBitmap();
    StringBuilder colors = new StringBuilder();
    for (int x = 90; x <= 720; x += 180) {
      int pixel = bmp.getPixel(x, 650);
      colors.append(color(pixel)).append(";");
      if (pixel == BLACK) {
        return x;
      }
    }
    throw new AssertionError("No BLACK! " + colors);
  }

  @SuppressWarnings("deprecation")
  private Bitmap getBitmap() {
    File screenshot;
    try {
      screenshot = File.createTempFile("beethoven", ".png");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    screenshot.deleteOnExit();

    assertTrue(getUiDevice().takeScreenshot(screenshot, 1, 1));
    assertTrue(screenshot.exists());
    assertTrue(screenshot.isFile());

    return new BitmapDrawable(screenshot.getAbsolutePath()).getBitmap();
  }

  private static String color(int pixel) {
    return '#' + hex(Color.red(pixel)) + hex(Color.green(pixel)) + hex(Color.blue(pixel));
  }

  private static String hex(int color) {
    String s = Integer.toString(color, 16);
    if (s.length() == 1) {
      s = '0' + s;
    }
    return s;
  }
}
