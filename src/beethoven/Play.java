package beethoven;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class Play extends UiAutomatorTestCase {

  private static final int BLACK = Color.rgb(32, 32, 32);
  private static final int Y = 700;

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
        colors.append(color(pixel)).append(";");
      }
      throw new AssertionError("No BLACK @ y=" + y + "! " + colors);
    }

    public void remove() {
      throw new UnsupportedOperationException("Not supported!");
    }
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
    assertTrue(getUiDevice().takeScreenshot(screenshot, 1, 0));
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
