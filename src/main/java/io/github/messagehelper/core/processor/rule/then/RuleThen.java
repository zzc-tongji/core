package io.github.messagehelper.core.processor.rule.then;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.messagehelper.core.exception.InvalidRuleThenException;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public class RuleThen {
  private String instance;
  private String method;
  private String path;
  private String bodyTemplate;

  public String getInstance() {
    return instance;
  }

  public void setInstance(String instance) {
    this.instance = instance;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getBodyTemplate() {
    return bodyTemplate;
  }

  public void setBodyTemplate(String bodyTemplate) {
    this.bodyTemplate = bodyTemplate;
  }

  public static RuleThen parse(String json) {
    RuleThen ruleThen = new RuleThen();
    JsonNode jsonNode;
    try {
      jsonNode = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new InvalidRuleThenException(e);
    }
    JsonNode temp = jsonNode.get("instance");
    if (temp != null && temp.isTextual()) {
      ruleThen.setInstance(temp.asText());
    } else {
      throw new InvalidRuleThenException("thenContent.instance: required, string");
    }
    temp = jsonNode.get("method");
    if (temp != null && temp.isTextual()) {
      switch (temp.asText().toUpperCase()) {
        case "GET":
          ruleThen.setMethod("GET");
          break;
        case "POST":
          ruleThen.setMethod("POST");
          break;
        default:
          throw new InvalidRuleThenException("thenContent.method: required, \"GET\" or \"POST\"");
      }
    } else {
      throw new InvalidRuleThenException("thenContent.method: required, \"GET\" or \"POST\"");
    }
    temp = jsonNode.get("path");
    if (temp != null && temp.isTextual()) {
      ruleThen.setPath(temp.asText());
    } else {
      throw new InvalidRuleThenException("thenContent.path: required, string");
    }
    temp = jsonNode.get("bodyTemplate");
    if (temp != null && temp.isTextual()) {
      ruleThen.setBodyTemplate(temp.asText());
    } else {
      throw new InvalidRuleThenException("thenContent.bodyTemplate: required, string");
    }
    return ruleThen;
  }

  @Override
  public String toString() {
    try {
      return ObjectMapperSingleton.getInstance().writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new RuntimeException();
    }
  }

  private RuleThen() {}
}
