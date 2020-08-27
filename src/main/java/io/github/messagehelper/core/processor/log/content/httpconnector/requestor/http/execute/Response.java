package io.github.messagehelper.core.processor.log.content.httpconnector.requestor.http.execute;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.messagehelper.core.exception.LogContentInvalidException;
import io.github.messagehelper.core.processor.log.content.Content;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public class Response extends Content {
  private String bodyString;
  private String contentType;
  private String statusString;
  private String url;

  public String getBodyString() {
    return bodyString;
  }

  public void setBodyString(String bodyString) {
    this.bodyString = bodyString;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getStatusString() {
    return statusString;
  }

  public void setStatusString(String statusString) {
    this.statusString = statusString;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Response(String json) {
    JsonNode jsonNode;
    try {
      jsonNode = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new LogContentInvalidException(e);
    }
    JsonNode temp = jsonNode.get("bodyString");
    if (temp != null && temp.isTextual()) {
      bodyString = temp.asText();
    } else {
      throw new LogContentInvalidException("bodyString: required, string");
    }
    temp = jsonNode.get("contentType");
    if (temp != null && temp.isTextual()) {
      contentType = temp.asText();
    } else {
      throw new LogContentInvalidException("contentType: required, string");
    }
    temp = jsonNode.get("statusString");
    if (temp != null && temp.isTextual()) {
      statusString = temp.asText();
    } else {
      throw new LogContentInvalidException("statusString: required, string");
    }
    temp = jsonNode.get("url");
    if (temp != null && temp.isTextual()) {
      url = temp.asText();
    } else {
      throw new LogContentInvalidException("url: required, string");
    }
  }
}
