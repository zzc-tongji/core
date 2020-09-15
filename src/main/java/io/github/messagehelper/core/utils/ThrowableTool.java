package io.github.messagehelper.core.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class ThrowableTool {
  private static final String TRIM_SUFFIX = " (...)";
  private static ThrowableTool instance = new ThrowableTool();

  public static ThrowableTool getInstance() {
    return instance;
  }

  public String convertToString(Throwable throwable) {
    return convertToString(throwable, 1, Integer.MAX_VALUE);
  }

  public String convertToString(Throwable throwable, int levelOfDetail) {
    return convertToString(throwable, levelOfDetail, Integer.MAX_VALUE);
  }

  public String convertToString(Throwable throwable, int levelOfDetail, int maxLength) {
    String temp;
    // acquire
    if (levelOfDetail <= 1) {
      // level 1
      temp = throwable.getMessage();
    } else if (levelOfDetail == 2) {
      // level 2
      temp = throwable.toString();
    } else { // levelOfDetail >= 3
      // level 3
      Writer writer = new StringWriter();
      throwable.printStackTrace(new PrintWriter(writer));
      temp = writer.toString();
    }
    // trim
    if (temp.length() <= maxLength) {
      return temp;
    }
    return temp.substring(0, maxLength - TRIM_SUFFIX.length()) + TRIM_SUFFIX;
  }
}
