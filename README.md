beethoven
=========

Don't Tap The White Tile/Piano Tiles Automator

@see http://developer.android.com/tools/testing/testing_ui.html for more instructions on UiAutomator.

To get started:
# Download and install [Don't Tap The White Tile](https://play.google.com/store/apps/details?id=com.umonistudio.tile)
# Launch app to Classic > Pro
# Run test!

From project dir:
```
> android create uitest-project -n beethoven -t 5 -p .
> ant build
> adb push ./bin/beethoven.jar /data/local/tmp/
> adb shell uiautomator runtest beethoven.jar -c beethoven.Play
```