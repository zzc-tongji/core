package io.github.messagehelper.core.log.content.wechatconnector.requestor.wechat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.messagehelper.core.exception.LogContentInvalidException;
import io.github.messagehelper.core.log.content.Content;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public class Sync extends Content {
  private String objectType;
  private String objectName;

  public String getObjectType() {
    return objectType;
  }

  public void setObjectType(String objectType) {
    this.objectType = objectType;
  }

  public String getObjectName() {
    return objectName;
  }

  public void setObjectName(String objectName) {
    this.objectName = objectName;
  }

  public Sync(String json) {
    JsonNode jsonNode;
    try {
      jsonNode = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new LogContentInvalidException(e);
    }
    JsonNode temp = jsonNode.get("objectType");
    if (temp != null && temp.isTextual()) {
      objectType = temp.asText();
    } else {
      throw new LogContentInvalidException("content.objectType: required, string");
    }
    temp = jsonNode.get("objectName");
    if (temp != null && temp.isTextual()) {
      objectName = temp.asText();
    } else {
      throw new LogContentInvalidException("content.objectName: required, string");
    }
  }
}
