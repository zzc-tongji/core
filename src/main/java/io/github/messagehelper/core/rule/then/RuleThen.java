package io.github.messagehelper.core.rule.then;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.messagehelper.core.exception.InvalidRuleThenException;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

import javax.validation.constraints.NotNull;

public class RuleThen {
  @NotNull(message = "payload.instance: required, string")
  private String instance;

  @NotNull(message = "payload.path: required, string")
  private String path;

  @JsonProperty("body")
  @NotNull(message = "payload.bodyTemplate: required, string")
  private String bodyTemplate;

  public String getInstance() {
    return instance;
  }

  public void setInstance(String instance) {
    this.instance = instance;
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

  public RuleThen(String json) {
    JsonNode jsonNode;
    try {
      jsonNode = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new InvalidRuleThenException(e);
    }
    JsonNode temp = jsonNode.get("instance");
    if (temp != null && temp.isTextual()) {
      instance = temp.asText();
    } else {
      throw new InvalidRuleThenException("instance: required, string");
    }
    temp = jsonNode.get("path");
    if (temp != null && temp.isTextual()) {
      path = temp.asText();
    } else {
      throw new InvalidRuleThenException("path: required, string");
    }
    temp = jsonNode.get("bodyTemplate");
    if (temp != null && temp.isTextual()) {
      bodyTemplate = temp.asText();
    } else {
      throw new InvalidRuleThenException("bodyTemplate: required, string");
    }
  }
}
