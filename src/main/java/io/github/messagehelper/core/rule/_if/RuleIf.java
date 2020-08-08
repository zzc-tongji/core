package io.github.messagehelper.core.rule._if;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.messagehelper.core.exception.InvalidRuleIfException;
import io.github.messagehelper.core.log.Log;
import io.github.messagehelper.core.rule._if.wechatconnector.listener.wechat.Message;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public class RuleIf {
  private String instance;
  private String category;

  public static RuleIf parse(String json) {
    // general
    if (json.contains("\"wechat-connector.listener.wechat.message\"")) {
      return new Message(json);
    } else {
      // "wechat-connector.auto-start"
      // "wechat-connector.cache.remove-expired"
      // "wechat-connector.listener.wechat.login"
      // "wechat-connector.listener.wechat.logout"
      // "wechat-connector.listener.wechat.ready"
      // "wechat-connector.listener.wechat.start"
      // "wechat-connector.listener.wechat.stop"
      // "wechat-connector.report.not-login-after-start"
      // "wechat-connector.report.unexpected-logout"
      // "wechat-connector.requestor.wechat.sync-all"
      return new RuleIf(json);
    }
  }

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
