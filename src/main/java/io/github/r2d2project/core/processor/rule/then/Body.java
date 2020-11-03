package io.github.r2d2project.core.processor.rule.then;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import io.github.r2d2project.core.processor.log.Log;
import io.github.r2d2project.core.processor.log.content.Unit;

import java.lang.reflect.Field;
import java.util.Map;

public class Body {
  private static final StringBuilder BUILDER = new StringBuilder();
  private static final String REGEX_PREFIX = "\\(\\(";
  private static final String REGEX_POSTFIX = "\\)\\)?";

  public static String fill(String input, Log log, boolean jsonEscape) {
    String output = input;
    // log
    for (Field field : Log.class.getDeclaredFields()) {
      if (!field.getName().equals("content")) {
        field.setAccessible(true);
        try {
          output =
              replaceHelper(
                  output, generateRegex(field.getName()), field.get(log).toString(), jsonEscape);
        } catch (IllegalAccessException ignored) {
        } finally {
          field.setAccessible(false);
        }
      }
    }
    // log.content
    Map<String, Unit> content = log.getContent();
    for (Unit unit : content.values()) {
      output =
          replaceHelper(output, generateRegex(unit.getPath()), unit.valueToString(), jsonEscape);
    }
    return output;
  }

  private static String generateRegex(String memberName) {
    BUILDER.delete(0, BUILDER.length());
    return BUILDER.append(REGEX_PREFIX).append(memberName).append(REGEX_POSTFIX).toString();
  }

  private static String replaceHelper(
      String input, String regex, String replacement, boolean jsonEscape) {
    StringBuilder stringBuilder = new StringBuilder();
    String temp;
    // https://www.cnblogs.com/iyangyuan/p/4809582.html
    if (jsonEscape) {
      JsonStringEncoder.getInstance().quoteAsString(replacement, stringBuilder);
      temp = stringBuilder.toString().replace("\\", "\\\\");
    } else {
      temp = replacement.replace("\\", "\\\\");
    }
    temp = temp.replace("$", "\\$");
    return input.replaceAll(regex, temp);
  }
}
