package io.github.messagehelper.core.log.content.wechatconnector.listener.wechat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.messagehelper.core.exception.InvalidContentException;
import io.github.messagehelper.core.log.content.Content;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public class Error extends Content {
  private String name;
  private String message;
  private String stack;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getStack() {
    return stack;
  }

  public void setStack(String stack) {
    this.stack = stack;
  }

  public Error(String json) {
    JsonNode jsonNode;
    try {
      jsonNode = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new InvalidContentException(e);
    }
    JsonNode temp = jsonNode.get("name");
    if (temp != null && temp.isTextual()) {
      name = temp.asText();
    } else {
      throw new InvalidContentException("content.name: required, string");
    }
    temp = jsonNode.get("message");
    if (temp != null && temp.isTextual()) {
      message = temp.asText();
    } else {
      throw new InvalidContentException("content.message: required, string");
    }
    temp = jsonNode.get("stack");
    if (temp != null && temp.isTextual()) {
      stack = temp.asText();
    } else {
      throw new InvalidContentException("content.stack: required, string");
    }
  }
}
