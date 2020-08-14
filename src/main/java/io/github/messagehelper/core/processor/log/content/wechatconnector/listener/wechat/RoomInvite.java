package io.github.messagehelper.core.processor.log.content.wechatconnector.listener.wechat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.messagehelper.core.exception.LogContentInvalidException;
import io.github.messagehelper.core.processor.log.content.Content;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public class RoomInvite extends Content {
  private String groupName;

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public RoomInvite(String json) {
    JsonNode jsonNode;
    try {
      jsonNode = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new LogContentInvalidException(e);
    }
    JsonNode temp = jsonNode.get("groupName");
    if (temp != null && temp.isTextual()) {
      groupName = temp.asText();
    } else {
      throw new LogContentInvalidException("content.groupName: required, string");
    }
  }
}
