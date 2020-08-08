package io.github.messagehelper.core.log.content.wechatconnector.listener.wechat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.messagehelper.core.exception.InvalidContentException;
import io.github.messagehelper.core.log.content.Content;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public class Message extends Content {
  private String messageType;
  private String messageText;
  private String messageFile;
  private String messageFileName;
  private Long messageTimestampMs;
  private Long messageAgeMs;
  private String oneId;
  private String oneName;
  private String oneAlias;
  private String oneAliasInGroup;
  private Boolean oneIsFriend;
  private String groupId;
  private String groupName;

  public String getMessageType() {
    return messageType;
  }

  public void setMessageType(String messageType) {
    this.messageType = messageType;
  }

  public String getMessageText() {
    return messageText;
  }

  public void setMessageText(String messageText) {
    this.messageText = messageText;
  }

  public String getMessageFile() {
    return messageFile;
  }

  public void setMessageFile(String messageFile) {
    this.messageFile = messageFile;
  }

  public String getMessageFileName() {
    return messageFileName;
  }

  public void setMessageFileName(String messageFileName) {
    this.messageFileName = messageFileName;
  }

  public Long getMessageTimestampMs() {
    return messageTimestampMs;
  }

  public void setMessageTimestampMs(Long messageTimestampMs) {
    this.messageTimestampMs = messageTimestampMs;
  }

  public Long getMessageAgeMs() {
    return messageAgeMs;
  }

  public void setMessageAgeMs(Long messageAgeMs) {
    this.messageAgeMs = messageAgeMs;
  }

  public String getOneId() {
    return oneId;
  }

  public void setOneId(String oneId) {
    this.oneId = oneId;
  }

  public String getOneName() {
    return oneName;
  }

  public void setOneName(String oneName) {
    this.oneName = oneName;
  }

  public String getOneAlias() {
    return oneAlias;
  }

  public void setOneAlias(String oneAlias) {
    this.oneAlias = oneAlias;
  }

  public String getOneAliasInGroup() {
    return oneAliasInGroup;
  }

  public void setOneAliasInGroup(String oneAliasInGroup) {
    this.oneAliasInGroup = oneAliasInGroup;
  }

  public Boolean getOneIsFriend() {
    return oneIsFriend;
  }

  public void setOneIsFriend(Boolean oneIsFriend) {
    this.oneIsFriend = oneIsFriend;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public Message(String json) {
    JsonNode jsonNode;
    try {
      jsonNode = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new InvalidContentException(e);
    }
    JsonNode temp = jsonNode.get("messageType");
    if (temp != null && temp.isTextual()) {
      messageType = temp.asText();
    } else {
      throw new InvalidContentException("content.messageType: required, string");
    }
    temp = jsonNode.get("messageText");
    if (temp != null && temp.isTextual()) {
      messageText = temp.asText();
    } else {
      throw new InvalidContentException("content.messageText: required, string");
    }
    temp = jsonNode.get("messageFile");
    if (temp != null && temp.isTextual()) {
      messageFile = temp.asText();
    } else {
      throw new InvalidContentException("content.messageFile: required, string");
    }
    temp = jsonNode.get("messageFileName");
    if (temp != null && temp.isTextual()) {
      messageFileName = temp.asText();
    } else {
      throw new InvalidContentException("content.messageFileName: required, string");
    }
    temp = jsonNode.get("messageTimestampMs");
    if (temp != null && temp.isIntegralNumber() && temp.canConvertToLong()) {
      messageTimestampMs = temp.asLong();
    } else {
      throw new InvalidContentException("content.messageTimestampMs: required, int");
    }
    temp = jsonNode.get("messageAgeMs");
    if (temp != null && temp.isIntegralNumber() && temp.canConvertToLong()) {
      messageAgeMs = temp.asLong();
    } else {
      throw new InvalidContentException("content.messageAgeMs: required, int");
    }
    temp = jsonNode.get("oneId");
    if (temp != null && temp.isTextual()) {
      oneId = temp.asText();
    } else {
      throw new InvalidContentException("content.oneId: required, string");
    }
    temp = jsonNode.get("oneName");
    if (temp != null && temp.isTextual()) {
      oneName = temp.asText();
    } else {
      throw new InvalidContentException("content.oneName: required, string");
    }
    temp = jsonNode.get("oneAlias");
    if (temp != null && temp.isTextual()) {
      oneAlias = temp.asText();
    } else {
      throw new InvalidContentException("content.oneAlias: required, string");
    }
    temp = jsonNode.get("oneAliasInGroup");
    if (temp != null && temp.isTextual()) {
      oneAliasInGroup = temp.asText();
    } else {
      throw new InvalidContentException("content.oneAliasInGroup: required, string");
    }
    temp = jsonNode.get("oneIsFriend");
    if (temp != null && temp.isBoolean()) {
      oneIsFriend = temp.asBoolean();
    } else {
      throw new InvalidContentException("content.oneIsFriend: required, boolean");
    }
    temp = jsonNode.get("groupId");
    if (temp != null && temp.isTextual()) {
      groupId = temp.asText();
    } else {
      throw new InvalidContentException("content.groupId: required, string");
    }
    temp = jsonNode.get("groupName");
    if (temp != null && temp.isTextual()) {
      groupName = temp.asText();
    } else {
      throw new InvalidContentException("content.groupName: required, string");
    }
  }
}
