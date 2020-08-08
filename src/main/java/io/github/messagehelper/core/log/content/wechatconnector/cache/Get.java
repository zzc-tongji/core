package io.github.messagehelper.core.log.content.wechatconnector.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.messagehelper.core.exception.InvalidContentException;
import io.github.messagehelper.core.log.content.Content;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public class Get extends Content {
  private Long key;
  private Boolean success;

  public Long getKey() {
    return key;
  }

  public void setKey(Long key) {
    this.key = key;
  }

  public Boolean getSuccess() {
    return success;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }

  public Get(String json) {
    JsonNode jsonNode;
    try {
      jsonNode = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new InvalidContentException(e);
    }
    JsonNode temp = jsonNode.get("key");
    if (temp != null && temp.isIntegralNumber() && temp.canConvertToLong()) {
      key = temp.asLong();
    } else {
      throw new InvalidContentException("content.key: required, long");
    }
    temp = jsonNode.get("success");
    if (temp != null && temp.isBoolean()) {
      success = temp.asBoolean();
    } else {
      throw new InvalidContentException("content.message: required, boolean");
    }
  }
}
