package io.github.messagehelper.core.processor.log.content.wechatconnector.requestor.wechat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.messagehelper.core.exception.LogContentInvalidException;
import io.github.messagehelper.core.processor.log.content.Content;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public class Reply extends Content {
  private String messageText;
  private String receiverId;
  private String receiverType;
  private String receiverName;

  public String getMessageText() {
    return messageText;
  }

  public void setMessageText(String messageText) {
    this.messageText = messageText;
  }

  public String getReceiverId() {
    return receiverId;
  }

  public void setReceiverId(String receiverId) {
    this.receiverId = receiverId;
  }

  public String getReceiverType() {
    return receiverType;
  }

  public void setReceiverType(String receiverType) {
    this.receiverType = receiverType;
  }

  public String getReceiverName() {
    return receiverName;
  }

  public void setReceiverName(String receiverName) {
    this.receiverName = receiverName;
  }

  public Reply(String json) {
    JsonNode jsonNode;
    try {
      jsonNode = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new LogContentInvalidException(e);
    }
    JsonNode temp = jsonNode.get("messageText");
    if (temp != null && temp.isTextual()) {
      messageText = temp.asText();
    } else {
      throw new LogContentInvalidException("content.messageText: required, string");
    }
    temp = jsonNode.get("receiverId");
    if (temp != null && temp.isTextual()) {
      receiverId = temp.asText();
    } else {
      throw new LogContentInvalidException("content.receiverId: required, string");
    }
    temp = jsonNode.get("receiverType");
    if (temp != null && temp.isTextual()) {
      receiverType = temp.asText();
    } else {
      throw new LogContentInvalidException("content.receiverType: required, string");
    }
    temp = jsonNode.get("receiverName");
    if (temp != null && temp.isTextual()) {
      receiverName = temp.asText();
    } else {
      throw new LogContentInvalidException("content.receiverName: required, string");
    }
  }
}
