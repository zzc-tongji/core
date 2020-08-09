package io.github.messagehelper.core.log.content.wechatconnector.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.messagehelper.core.exception.LogContentInvalidException;
import io.github.messagehelper.core.log.content.Content;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public class Set extends Content {
  private Long key;

  public Long getKey() {
    return key;
  }

  public void setKey(Long key) {
    this.key = key;
  }

  public Set(String json) {
    JsonNode jsonNode;
    try {
      jsonNode = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new LogContentInvalidException(e);
    }
    JsonNode temp = jsonNode.get("key");
    if (temp != null && temp.isIntegralNumber() && temp.canConvertToLong()) {
      key = temp.asLong();
    } else {
      throw new LogContentInvalidException("content.key: required, long");
    }
  }
}
