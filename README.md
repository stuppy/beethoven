beethoven
=========

Piano Tiles Automator

@see http://developer.android.com/tools/testing/testing_ui.html for more instructions.

From project dir:
> android create uitest-project -n beethoven -t 5 -p .
> ant build
> adb push ./bin/beethoven.jar /data/local/tmp/
> adb shell uiautomator runtest beethoven.jar -c beethoven.Play