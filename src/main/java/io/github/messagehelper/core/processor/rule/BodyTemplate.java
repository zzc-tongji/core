package io.github.messagehelper.core.processor.rule;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import io.github.messagehelper.core.processor.log.Log;
import io.github.messagehelper.core.processor.log.content.Content;

import java.lang.reflect.Field;

public class BodyTemplate {
  private static final StringBuilder BUILDER = new StringBuilder();
  private static final String REGEX_PREFIX = "\\(\\(";
  private static final String REGEX_CONTENT = "content\\.";
  private static final String REGEX_POSTFIX = "\\)\\)?";

  public static String fill(String input, Log log) {
    String output = input;
    // log
    for (Field field : Log.class.getDeclaredFields()) {
      if (!field.getName().equals("content")) {
        field.setAccessible(true);
        try {
          output = output.replaceAll(generateLogRegex(field.getName()), field.get(log).toString());
        } catch (IllegalAccessException ignored) {
        } finally {
          field.setAccessible(false);
        }
      }
    }
    // log.content
    Content content = log.getContent();
    for (Field field : content.getClass().getDeclaredFields()) {
      field.setAccessible(true);
      try {
        output =
            replaceHelper(
                output, generateContentRegex(field.getName()), field.get(content).toString());
      } catch (IllegalAccessException ignored) {
      } finally {
        field.setAccessible(false);
      }
    }
    return output;
  }

  private static String generateLogRegex(String memberName) {
    BUILDER.delete(0, BUILDER.length());
    return BUILDER.append(REGEX_PREFIX).append(memberName).append(REGEX_POSTFIX).toString();
  }

  private static String generateContentRegex(String memberName) {
    BUILDER.delete(0, BUILDER.length());
    return BUILDER
        .append(REGEX_PREFIX)
        .append(REGEX_CONTENT)
        .append(memberName)
        .append(REGEX_POSTFIX)
        .toString();
  }

  private static String escapeHelper(String text) {
    StringBuilder e1 = new StringBuilder();
    JsonStringEncoder.getInstance().quoteAsString(text, e1);
    return e1.toString().replace("\\", "\\\\");
  }

  private static String replaceHelper(String input, String regex, String replacement) {
    StringBuilder stringBuilder = new StringBuilder();
    JsonStringEncoder.getInstance().quoteAsString(replacement, stringBuilder);
    // https://www.cnblogs.com/iyangyuan/p/4809582.html
    return input.replaceAll(regex, stringBuilder.toString().replace("\\", "\\\\"));
  }
}
