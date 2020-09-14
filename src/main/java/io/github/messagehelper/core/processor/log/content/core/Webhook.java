package io.github.messagehelper.core.processor.log.content.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.messagehelper.core.processor.log.content.Content;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public class Webhook extends Content {
  private String value1;
  private String value2;
  private String value3;

  public String getValue1() {
    return value1;
  }

  public void setValue1(String value1) {
    this.value1 = value1;
  }

  public String getValue2() {
    return value2;
  }

  public void setValue2(String value2) {
    this.value2 = value2;
  }

  public String getValue3() {
    return value3;
  }

  public void setValue3(String value3) {
    this.value3 = value3;
  }

  public Webhook(String json) {
    JsonNode jsonNode;
    try {
      jsonNode = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    JsonNode temp = jsonNode.get("value1");
    value1 = temp.asText();
    temp = jsonNode.get("value2");
    value2 = temp.asText();
    temp = jsonNode.get("value3");
    value3 = temp.asText();
  }
}
