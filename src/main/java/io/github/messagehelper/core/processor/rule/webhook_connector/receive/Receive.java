package io.github.messagehelper.core.processor.rule.webhook_connector.receive;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.messagehelper.core.exception.InvalidRuleIfException;
import io.github.messagehelper.core.processor.log.Log;
import io.github.messagehelper.core.processor.rule.RuleIf;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public class Receive extends RuleIf {
  private String value1Regex;
  private String value2Regex;
  private String value3Regex;

  public String getValue1Regex() {
    return value1Regex;
  }

  public void setValue1Regex(String value1Regex) {
    this.value1Regex = value1Regex;
  }

  public String getValue2Regex() {
    return value2Regex;
  }

  public void setValue2Regex(String value2Regex) {
    this.value2Regex = value2Regex;
  }

  public String getValue3Regex() {
    return value3Regex;
  }

  public void setValue3Regex(String value3Regex) {
    this.value3Regex = value3Regex;
  }

  @Override
  public boolean satisfy(Log log) {
    io.github.messagehelper.core.processor.log.content.Content content = log.getContent();
    if (!(content instanceof io.github.messagehelper.core.processor.log.content.core.Webhook)) {
      return false;
    }
    io.github.messagehelper.core.processor.log.content.core.Webhook webhook =
        (io.github.messagehelper.core.processor.log.content.core.Webhook) content;
    if (!webhook.getValue1().matches(value1Regex)) {
      return false;
    }
    if (!webhook.getValue2().matches(value2Regex)) {
      return false;
    }
    return webhook.getValue3().matches(value3Regex);
  }

  public Receive(String json) {
    JsonNode jsonNode;
    try {
      jsonNode = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new InvalidRuleIfException(e);
    }
    JsonNode temp = jsonNode.get("value1Regex");
    if (temp != null && temp.isTextual()) {
      value1Regex = temp.asText();
    } else {
      throw new InvalidRuleIfException("value1Regex: required, string");
    }
    temp = jsonNode.get("value2Regex");
    if (temp != null && temp.isTextual()) {
      value2Regex = temp.asText();
    } else {
      throw new InvalidRuleIfException("value2Regex: required, string");
    }
    temp = jsonNode.get("value3Regex");
    if (temp != null && temp.isTextual()) {
      value3Regex = temp.asText();
    } else {
      throw new InvalidRuleIfException("value3Regex: required, string");
    }
  }
}
