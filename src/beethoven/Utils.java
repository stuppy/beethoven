package beethoven;

import android.graphics.Color;

/**
 * Non-essential utilities.
 */
class Utils {

  /**
   * Convert {@link Color} pixel to a hex value (ex. #ABCDEF).
   */
  static String color(int pixel) {
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
