package io.github.messagehelper.core.processor.log.content.wechatconnector.requestor.wechat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.messagehelper.core.exception.LogContentInvalidException;
import io.github.messagehelper.core.processor.log.content.Content;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public class Error extends Content {
  private String reason;
  private String contextType;
  private String contextRequest;

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public String getContextType() {
    return contextType;
  }

  public void setContextType(String contextType) {
    this.contextType = contextType;
  }

  public String getContextRequest() {
    return contextRequest;
  }

  public void setContextRequest(String contextRequest) {
    this.contextRequest = contextRequest;
  }

  public Error(String json) {
    JsonNode jsonNode;
    try {
      jsonNode = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new LogContentInvalidException(e);
    }
    JsonNode temp = jsonNode.get("reason");
    if (temp != null && temp.isTextual()) {
      reason = temp.asText();
    } else {
      throw new LogContentInvalidException("content.reason: required, string");
    }
    temp = jsonNode.get("contextType");
    if (temp != null && temp.isTextual()) {
      contextType = temp.asText();
    } else {
      throw new LogContentInvalidException("content.contextType: required, string");
    }
    temp = jsonNode.get("contextRequest");
    if (temp != null && temp.isTextual()) {
      contextRequest = temp.asText();
    } else {
      throw new LogContentInvalidException("content.contextRequest: required, string");
    }
  }
}
