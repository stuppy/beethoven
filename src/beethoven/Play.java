package beethoven;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class Play extends UiAutomatorTestCase {

  private static final String TAG = Play.class.getSimpleName();

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
      assertTrue(getUiDevice().click(x.next(), Y));
    }
  }

  private class X implements Iterator<Integer> {

    private final int length;
    private final ConcurrentMap<Integer, File> screenshots;
    private final ConcurrentMap<Integer, AsyncTask<?, ?, File>> tasks;

    private int count = 0;

    private X(int length) {
      Looper.prepare();

      this.length = length;
      this.screenshots = new ConcurrentHashMap<Integer, File>();
      this.tasks = new ConcurrentHashMap<Integer, AsyncTask<?,?,File>>();
    }

    public boolean hasNext() {
      return count < length;
    }

    public Integer next() {
      count++;

      takeScreenshot(count);

      Bitmap bmp = null;
      int y = -1;
      if (count == 1) {
        bmp = getBitmap(1);
        y = Y;
      } else {
        for (int s = count; s >= 1 && s >= count - 2; s--) {
          if (screenshots.containsKey(s)) {
            bmp = getBitmap(s);
            y = Y - (count - s) * 300;
            break;
          } else {
            Log.w(TAG, "screenshots does not contain " + s + " (" + screenshots.keySet() + ")");
          }
        }
        if (bmp == null) {
          bmp = getBitmap(count - 1);
          y = Y - 1 * 300;
        }
      }
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

    private void takeScreenshot(final int at) {
      // onPostExecute was not working correctly, so just managing with Concurrent Map instead!
      new AsyncTask<Void, Void, File>() {
        @Override
        protected void onPreExecute() {
          Log.i(TAG, "Taking screenshot at " + at);
          tasks.put(at, this);
        }

        @Override
        protected File doInBackground(Void... args) {
          File screenshot = new File("/data/local/tmp/beethoven-" + at + ".png");
          screenshot.deleteOnExit();
          assertTrue(getUiDevice().takeScreenshot(screenshot, 1, 0));

          screenshots.put(at, screenshot);

          return screenshot;
        }
      }.execute();
    }

    @SuppressWarnings("deprecation")
    private Bitmap getBitmap(int at) {
      Log.i(TAG, "Getting bitmap at " + at);
      return new BitmapDrawable(getScreenshot(at).getAbsolutePath()).getBitmap();
    }

    private File getScreenshot(int at) {
      if (screenshots.containsKey(at)) {
        return screenshots.get(at);
      } else if (tasks.containsKey(at)) {
        try {
          return tasks.get(at).get();
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        } catch (ExecutionException e) {
          throw new RuntimeException(e);
        }
      } else {
        throw new AssertionError("No screenshot for " + at);
      }
    }

    public void remove() {
      throw new UnsupportedOperationException("Not supported!");
    }
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
