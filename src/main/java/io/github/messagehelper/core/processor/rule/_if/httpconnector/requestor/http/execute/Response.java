package io.github.messagehelper.core.processor.rule._if.httpconnector.requestor.http.execute;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.messagehelper.core.exception.InvalidRuleIfException;
import io.github.messagehelper.core.processor.log.Log;
import io.github.messagehelper.core.processor.log.content.Content;
import io.github.messagehelper.core.processor.rule._if.RuleIf;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public class Response extends RuleIf {
  private String bodyStringRegex;
  private String contentTypeRegex;
  private String statusStringRegex;
  private String urlRegex;

  public String getBodyStringRegex() {
    return bodyStringRegex;
  }

  public void setBodyStringRegex(String bodyStringRegex) {
    this.bodyStringRegex = bodyStringRegex;
  }

  public String getContentTypeRegex() {
    return contentTypeRegex;
  }

  public void setContentTypeRegex(String contentTypeRegex) {
    this.contentTypeRegex = contentTypeRegex;
  }

  public String getStatusStringRegex() {
    return statusStringRegex;
  }

  public void setStatusStringRegex(String statusStringRegex) {
    this.statusStringRegex = statusStringRegex;
  }

  public String getUrlRegex() {
    return urlRegex;
  }

  public void setUrlRegex(String urlRegex) {
    this.urlRegex = urlRegex;
  }

  @Override
  public boolean satisfy(Log log) {
    if (!super.satisfy(log)) {
      return false;
    }
    Content content = log.getContent();
    if (!(content
        instanceof
        io.github.messagehelper.core.processor.log.content.httpconnector.requestor.http.execute
            .Response)) {
      return false;
    }
    io.github.messagehelper.core.processor.log.content.httpconnector.requestor.http.execute.Response
        response =
            (io.github.messagehelper.core.processor.log.content.httpconnector.requestor.http.execute
                    .Response)
                content;
    if (!response.getBodyString().matches(bodyStringRegex)) {
      return false;
    }
    if (!response.getContentType().matches(contentTypeRegex)) {
      return false;
    }
    if (!response.getStatusString().matches(statusStringRegex)) {
      return false;
    }
    return response.getUrl().matches(urlRegex);
  }

  public Response(String json) {
    super(json);
    JsonNode jsonNode;
    try {
      jsonNode = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new InvalidRuleIfException(e);
    }
    JsonNode temp = jsonNode.get("bodyStringRegex");
    if (temp != null && temp.isTextual()) {
      bodyStringRegex = temp.asText();
    } else {
      throw new InvalidRuleIfException("bodyStringRegex: required, string");
    }
    temp = jsonNode.get("contentTypeRegex");
    if (temp != null && temp.isTextual()) {
      contentTypeRegex = temp.asText();
    } else {
      throw new InvalidRuleIfException("contentTypeRegex: required, string");
    }
    temp = jsonNode.get("statusStringRegex");
    if (temp != null && temp.isTextual()) {
      statusStringRegex = temp.asText();
    } else {
      throw new InvalidRuleIfException("statusStringRegex: required, string");
    }
    temp = jsonNode.get("urlRegex");
    if (temp != null && temp.isTextual()) {
      urlRegex = temp.asText();
    } else {
      throw new InvalidRuleIfException("urlRegex: required, string");
    }
  }
}
