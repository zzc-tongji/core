package io.github.messagehelper.core.processor.log.content.httpconnector.listener.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.messagehelper.core.exception.LogContentInvalidException;
import io.github.messagehelper.core.processor.log.content.Content;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public class Receive extends Content {
  private String contentType;
  private String bodyString;

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getBodyString() {
    return bodyString;
  }

  public void setBodyString(String bodyString) {
    this.bodyString = bodyString;
  }

  public Receive(String json) {
    JsonNode jsonNode;
    try {
      jsonNode = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new LogContentInvalidException(e);
    }
    JsonNode temp = jsonNode.get("contentType");
    if (temp != null && temp.isTextual()) {
      contentType = temp.asText();
    } else {
      throw new LogContentInvalidException("contentType: required, string");
    }
    temp = jsonNode.get("bodyString");
    if (temp != null && temp.isTextual()) {
      bodyString = temp.asText();
    } else {
      throw new LogContentInvalidException("bodyString: required, string");
    }
  }
}
