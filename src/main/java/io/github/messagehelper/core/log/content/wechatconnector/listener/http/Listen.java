package io.github.messagehelper.core.log.content.wechatconnector.listener.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.messagehelper.core.exception.LogContentInvalidException;
import io.github.messagehelper.core.log.content.Content;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public class Listen extends Content {
  private Integer port;

  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public Listen(String json) {
    JsonNode jsonNode;
    try {
      jsonNode = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new LogContentInvalidException(e);
    }
    JsonNode temp = jsonNode.get("port");
    if (temp != null && temp.isIntegralNumber() && temp.canConvertToInt()) {
      port = temp.asInt();
    } else {
      throw new LogContentInvalidException("content.port: required, int");
    }
  }
}
