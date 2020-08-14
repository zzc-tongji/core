package io.github.messagehelper.core.rule.then;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.messagehelper.core.exception.InvalidRuleThenException;
import io.github.messagehelper.core.utils.Delegate;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public class RuleThen implements Delegate {
  private String instance;
  private String path;
  private String bodyTemplate;

  @Override
  public String getInstance() {
    return instance;
  }

  public void setInstance(String instance) {
    this.instance = instance;
  }

  @Override
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
