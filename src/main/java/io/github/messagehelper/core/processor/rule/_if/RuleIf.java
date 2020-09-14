package io.github.messagehelper.core.processor.rule._if;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.messagehelper.core.exception.InvalidRuleIfException;
import io.github.messagehelper.core.processor.log.Log;
import io.github.messagehelper.core.processor.rule._if.core.Webhook;
import io.github.messagehelper.core.processor.rule._if.httpconnector.listener.http.Receive;
import io.github.messagehelper.core.processor.rule._if.httpconnector.requestor.http.execute.Response;
import io.github.messagehelper.core.processor.rule._if.wechatconnector.listener.wechat.Friendship;
import io.github.messagehelper.core.processor.rule._if.wechatconnector.listener.wechat.Message;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public class RuleIf {
  private String instance;
  private String category;

  public String getInstance() {
    return instance;
  }

  public void setInstance(String instance) {
    this.instance = instance;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public static RuleIf parse(String json) {
    if (json.contains("\"core.webhook\"")) {
      return new Webhook(json);
    } else if (json.contains("\"http-connector.listener.http.receive\"")) {
      return new Receive(json);
    } else if (json.contains("\"http-connector.requestor.http.execute.response\"")) {
      return new Response(json);
    } else if (json.contains("\"wechat-connector.listener.wechat.friendship\"")) {
      return new Friendship(json);
    } else if (json.contains("\"wechat-connector.listener.wechat.message\"")) {
      return new Message(json);
    } else if (json.contains("\"wechat-connector.listener.wechat.room-topic\"")) {
      return new Response(json);
    } else {
      return new RuleIf(json);
    }
  }

  @Override
  public String toString() {
    try {
      return ObjectMapperSingleton.getInstance().writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new RuntimeException();
    }
  }

  public RuleIf(String json) {
    JsonNode jsonNode;
    try {
      jsonNode = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new InvalidRuleIfException(e);
    }
    JsonNode temp = jsonNode.get("instance");
    if (temp != null && temp.isTextual()) {
      instance = temp.asText();
    } else {
      throw new InvalidRuleIfException("instance: required, string");
    }
    temp = jsonNode.get("category");
    if (temp != null && temp.isTextual()) {
      category = temp.asText();
    } else {
      throw new InvalidRuleIfException("category: required, string");
    }
  }

  public boolean satisfy(Log log) {
    if (!log.getInstance().equals(instance)) {
      return false;
    }
    return log.getCategory().equals(category);
  }
}
