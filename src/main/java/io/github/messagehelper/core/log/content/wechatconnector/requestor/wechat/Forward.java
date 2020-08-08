package io.github.messagehelper.core.log.content.wechatconnector.requestor.wechat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.messagehelper.core.exception.InvalidContentException;
import io.github.messagehelper.core.log.content.Content;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public class Forward extends Content {
  private String messageId;
  private String messageType;
  private String messageText;
  private String receiverId;
  private String receiverType;
  private String receiverName;

  public String getMessageId() {
    return messageId;
  }

  public void setMessageId(String messageId) {
    this.messageId = messageId;
  }

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

  public Forward(String json) {
    JsonNode jsonNode;
    try {
      jsonNode = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new InvalidContentException(e);
    }
    JsonNode temp = jsonNode.get("messageId");
    if (temp != null && temp.isTextual()) {
      messageId = temp.asText();
    } else {
      throw new InvalidContentException("content.messageId: required, string");
    }
    temp = jsonNode.get("messageType");
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
    temp = jsonNode.get("receiverId");
    if (temp != null && temp.isTextual()) {
      receiverId = temp.asText();
    } else {
      throw new InvalidContentException("content.receiverId: required, string");
    }
    temp = jsonNode.get("receiverType");
    if (temp != null && temp.isTextual()) {
      receiverType = temp.asText();
    } else {
      throw new InvalidContentException("content.receiverType: required, string");
    }
    temp = jsonNode.get("receiverName");
    if (temp != null && temp.isTextual()) {
      receiverName = temp.asText();
    } else {
      throw new InvalidContentException("content.receiverName: required, string");
    }
  }
}
