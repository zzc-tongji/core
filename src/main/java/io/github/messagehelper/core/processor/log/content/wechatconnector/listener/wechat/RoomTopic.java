package io.github.messagehelper.core.processor.log.content.wechatconnector.listener.wechat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.messagehelper.core.exception.LogContentInvalidException;
import io.github.messagehelper.core.processor.log.content.Content;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public class RoomTopic extends Content {
  private String oldGroupName;
  private String newGroupName;

  public String getOldGroupName() {
    return oldGroupName;
  }

  public void setOldGroupName(String oldGroupName) {
    this.oldGroupName = oldGroupName;
  }

  public String getNewGroupName() {
    return newGroupName;
  }

  public void setNewGroupName(String newGroupName) {
    this.newGroupName = newGroupName;
  }

  public RoomTopic(String json) {
    JsonNode jsonNode;
    try {
      jsonNode = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new LogContentInvalidException(e);
    }
    JsonNode temp = jsonNode.get("oldGroupName");
    if (temp != null && temp.isTextual()) {
      oldGroupName = temp.asText();
    } else {
      throw new LogContentInvalidException("content.oldGroupName: required, string");
    }
    temp = jsonNode.get("newGroupName");
    if (temp != null && temp.isTextual()) {
      newGroupName = temp.asText();
    } else {
      throw new LogContentInvalidException("content.newGroupName: required, string");
    }
  }
}
