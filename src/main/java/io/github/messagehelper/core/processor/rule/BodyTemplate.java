package io.github.messagehelper.core.processor.rule;

import io.github.messagehelper.core.processor.log.Log;
import io.github.messagehelper.core.processor.log.content.Content;

import java.lang.reflect.Field;

public class BodyTemplate {
  private static final StringBuilder BUILDER = new StringBuilder();
  private static final String REGEX_PREFIX = "\\(\\(";
  private static final String REGEX_CONTENT = "content\\.";
  private static final String REGEX_POSTFIX = "\\)\\)?";
  private static final boolean DEBUG = false;

  public static String fill(String input, Log log) {
    String output = input;
    if (DEBUG) {
      System.out.printf("output = \"%s\"\n", output);
    }
    // log
    for (Field field : Log.class.getDeclaredFields()) {
      if (!field.getName().equals("content")) {
        field.setAccessible(true);
        try {
          output = output.replaceAll(generateLogRegex(field.getName()), field.get(log).toString());
          if (DEBUG) {
            System.out.printf(
                "output.replaceAll(\"%s\", \"%s\")\n",
                generateLogRegex(field.getName()), field.get(log).toString());
          }
        } catch (IllegalAccessException e) {
          if (DEBUG) {
            e.printStackTrace();
          }
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
            output.replaceAll(generateContentRegex(field.getName()), field.get(content).toString());
        if (DEBUG) {
          System.out.printf(
              "output.replaceAll(\"%s\", \"%s\")\n",
              generateContentRegex(field.getName()).replaceAll("\"", "\\\""),
              field.get(content).toString().replaceAll("\"", "\\\""));
        }
      } catch (IllegalAccessException e) {
        if (DEBUG) {
          e.printStackTrace();
        }
      } finally {
        field.setAccessible(false);
      }
    }
    if (DEBUG) {
      System.out.printf("output = \"%s\"\n", output);
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
}
