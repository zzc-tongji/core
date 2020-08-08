package io.github.messagehelper.core.utils;

import com.fasterxml.jackson.databind.node.ObjectNode;

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
    ObjectNode objectNode = ObjectMapperSingleton.getInstance().getNodeFactory().objectNode();
    objectNode.put("information", e.getMessage());
    objectNode.put("detail", e.toString());
    objectNode.put("verbose", sw.toString());
    return objectNode.toString();
  }
}
