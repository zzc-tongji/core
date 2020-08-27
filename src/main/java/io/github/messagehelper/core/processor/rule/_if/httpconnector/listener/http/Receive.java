package io.github.messagehelper.core.processor.rule._if.httpconnector.listener.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.messagehelper.core.exception.InvalidRuleIfException;
import io.github.messagehelper.core.processor.log.Log;
import io.github.messagehelper.core.processor.log.content.Content;
import io.github.messagehelper.core.processor.rule._if.RuleIf;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public class Receive extends RuleIf {
  private String contentTypeRegex;
  private String bodyStringRegex;

  public String getContentTypeRegex() {
    return contentTypeRegex;
  }

  public void setContentTypeRegex(String contentTypeRegex) {
    this.contentTypeRegex = contentTypeRegex;
  }

  public String getBodyStringRegex() {
    return bodyStringRegex;
  }

  public void setBodyStringRegex(String bodyStringRegex) {
    this.bodyStringRegex = bodyStringRegex;
  }

  @Override
  public boolean satisfy(Log log) {
    if (!super.satisfy(log)) {
      return false;
    }
    Content content = log.getContent();
    if (!(content
        instanceof
        io.github.messagehelper.core.processor.log.content.httpconnector.listener.http.Receive)) {
      return false;
    }
    io.github.messagehelper.core.processor.log.content.httpconnector.listener.http.Receive receive =
        (io.github.messagehelper.core.processor.log.content.httpconnector.listener.http.Receive)
            content;
    if (!receive.getContentType().matches(contentTypeRegex)) {
      return false;
    }
    return receive.getBodyString().matches(bodyStringRegex);
  }

  public Receive(String json) {
    super(json);
    JsonNode jsonNode;
    try {
      jsonNode = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new InvalidRuleIfException(e);
    }
    JsonNode temp = jsonNode.get("contentTypeRegex");
    if (temp != null && temp.isTextual()) {
      contentTypeRegex = temp.asText();
    } else {
      throw new InvalidRuleIfException("contentTypeRegex: required, string");
    }
    temp = jsonNode.get("bodyStringRegex");
    if (temp != null && temp.isTextual()) {
      bodyStringRegex = temp.asText();
    } else {
      throw new InvalidRuleIfException("bodyStringRegex: required, string");
    }
  }
}
