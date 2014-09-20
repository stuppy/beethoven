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
   * Takes a screenshot in ~ 16ms (basically, one frame).
   *
   * <p>The basic call chain is:
   * UiDevice.getInstance().getAutomatorBridge().mUiAutomation.takeScreenshot()
   *
   * <p>This is a super hack that uses private/hidden methods to avoid creating a file, which is
   * slow (300+ ms).
   */
  private Bitmap takeScreenshot() {
    return new SuperObject(UiDevice.getInstance())
        .call("getAutomatorBridge")
        .get("mUiAutomation")
        .call("takeScreenshot")
        .to(Bitmap.class);
  }

  private static class SuperObject {
    private final Object object;

    private SuperObject(Object object) {
      this.object = object;
    }

    private <T> T to(Class<T> clazz) {
      return clazz.cast(object);
    }

    private SuperObject call(String method, Object... args) {
      Method m;
      Class<?> c = object.getClass();
      do {
        try {
          m = c.getDeclaredMethod(method);
          break;
        } catch (NoSuchMethodException e) {
          if (c.getSuperclass() != null) {
            c = c.getSuperclass();
          } else {
            throw new RuntimeException(e);
          }
        }
      } while(true);
      m.setAccessible(true);
      try {
        return new SuperObject(m.invoke(object, args));
      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    private SuperObject get(String name) {
      Field f;
      Class<?> c = object.getClass();
      do {
        try {
          f = c.getDeclaredField(name);
          break;
        } catch (NoSuchFieldException e) {
          if (c.getSuperclass() != null) {
            c = c.getSuperclass();
          } else {
            throw new RuntimeException(e);
          }
        }
      } while(true);
      f.setAccessible(true);
      try {
        return new SuperObject(f.get(object));
      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}
