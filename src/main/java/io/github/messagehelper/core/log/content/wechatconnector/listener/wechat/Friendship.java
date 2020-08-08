package io.github.messagehelper.core.log.content.wechatconnector.listener.wechat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.messagehelper.core.exception.InvalidContentException;
import io.github.messagehelper.core.log.content.Content;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public class Friendship extends Content {
  private String friendshipType;
  private String requestMessage;
  private String requesterName;

  public String getFriendshipType() {
    return friendshipType;
  }

  public void setFriendshipType(String friendshipType) {
    this.friendshipType = friendshipType;
  }

  public String getRequestMessage() {
    return requestMessage;
  }

  public void setRequestMessage(String requestMessage) {
    this.requestMessage = requestMessage;
  }

  public String getRequesterName() {
    return requesterName;
  }

  public void setRequesterName(String requesterName) {
    this.requesterName = requesterName;
  }

  public Friendship(String json) {
    JsonNode jsonNode;
    try {
      jsonNode = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new InvalidContentException(e);
    }
    JsonNode temp = jsonNode.get("friendshipType");
    if (temp != null && temp.isTextual()) {
      friendshipType = temp.asText();
    } else {
      throw new InvalidContentException("content.friendshipType: required, string");
    }
    temp = jsonNode.get("requestMessage");
    if (temp != null && temp.isTextual()) {
      requestMessage = temp.asText();
    } else {
      throw new InvalidContentException("content.requestMessage: required, string");
    }
    temp = jsonNode.get("requesterName");
    if (temp != null && temp.isTextual()) {
      requesterName = temp.asText();
    } else {
      throw new InvalidContentException("content.requesterName: required, string");
    }
  }
}