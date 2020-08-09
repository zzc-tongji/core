package io.github.messagehelper.core.utils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.messagehelper.core.mysql.Constant;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorJsonGenerator {
  private static ErrorJsonGenerator instance = new ErrorJsonGenerator();

  public static ErrorJsonGenerator getInstance() {
    return instance;
  }

  private ErrorJsonGenerator() {}

  public String generate(String information, String detail, String verbose) {
    ObjectNode objectNode = ObjectMapperSingleton.getInstance().getNodeFactory().objectNode();
    objectNode.put("information", information);
    objectNode.put("detail", detail);
    objectNode.put("verbose", verbose);
    return objectNode.toString();
  }

  public String generate(Exception e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    //
    ObjectNode objectNode = ObjectMapperSingleton.getInstance().getNodeFactory().objectNode();
    objectNode.put("information", e.getMessage());
    objectNode.put("detail", e.toString());
    String verbose = sw.toString();
    objectNode.put("verbose", verbose);
    String result = objectNode.toString();
    //
    int delta = result.length() - Constant.LOG_CONTENT_LENGTH;
    if (delta > 0) {
      objectNode.put("verbose", verbose.substring(0, verbose.length() - delta));
    }
    return objectNode.toString();
  }
}
