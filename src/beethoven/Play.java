package beethoven;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Looper;

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
   * Test!
   *
   * <p>This one is basically mashing buttons.
   */
  public void ignore_testClassicPro() throws Exception {
    UiObject tile = new UiObject(
        new UiSelector()
            .packageName("com.umonistudio.tile")
            .className(android.view.View.class));
    assertTrue(tile.exists());

    Looper.prepare();
    // Only one async task is executed in the background at a time, apparently?
    new AsyncTask<Void, Void, Void>() {
      @Override
      protected Void doInBackground(Void... args) {
        for (int i = 0; i < 100; i++) {
          getUiDevice().click(185, 970);
        }
        return null;
      }
    }.execute();
    for (int i = 0; i < 100; i++) {
      getUiDevice().click(535, 970);
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
        bmp = getBitmap();
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

  @SuppressWarnings("deprecation")  // BitmapDrawable contructor; Resources isn't available.
  private Bitmap getBitmap() {
    File screenshot;
    try {
      screenshot = File.createTempFile("beethoven", ".png");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    screenshot.deleteOnExit();
    assertTrue(getUiDevice().takeScreenshot(screenshot, 1, 0));
    return new BitmapDrawable(screenshot.getAbsolutePath()).getBitmap();
  }
}
