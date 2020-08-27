package io.github.messagehelper.core.processor.rule._if.wechatconnector.listener.wechat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.messagehelper.core.exception.InvalidRuleIfException;
import io.github.messagehelper.core.processor.log.Log;
import io.github.messagehelper.core.processor.log.content.Content;
import io.github.messagehelper.core.processor.rule._if.RuleIf;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public class RoomTopic extends RuleIf {
  private String oldGroupNameRegex;
  private String newGroupNameRegex;

  public String getOldGroupNameRegex() {
    return oldGroupNameRegex;
  }

  public void setOldGroupNameRegex(String oldGroupNameRegex) {
    this.oldGroupNameRegex = oldGroupNameRegex;
  }

  public String getNewGroupNameRegex() {
    return newGroupNameRegex;
  }

  public void setNewGroupNameRegex(String newGroupNameRegex) {
    this.newGroupNameRegex = newGroupNameRegex;
  }

  @Override
  public boolean satisfy(Log log) {
    if (!super.satisfy(log)) {
      return false;
    }
    Content content = log.getContent();
    if (!(content
        instanceof
        io.github.messagehelper.core.processor.log.content.wechatconnector.listener.wechat
            .RoomTopic)) {
      return false;
    }
    io.github.messagehelper.core.processor.log.content.wechatconnector.listener.wechat.RoomTopic
        roomTopic =
            (io.github.messagehelper.core.processor.log.content.wechatconnector.listener.wechat
                    .RoomTopic)
                content;
    if (!roomTopic.getOldGroupName().matches(oldGroupNameRegex)) {
      return false;
    }
    return roomTopic.getNewGroupName().matches(newGroupNameRegex);
  }

  public RoomTopic(String json) {
    super(json);
    JsonNode jsonNode;
    try {
      jsonNode = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new InvalidRuleIfException(e);
    }
    JsonNode temp = jsonNode.get("oldGroupNameRegex");
    if (temp != null && temp.isTextual()) {
      oldGroupNameRegex = temp.asText();
    } else {
      throw new InvalidRuleIfException("oldGroupNameRegex: required, string");
    }
    temp = jsonNode.get("newGroupNameRegex");
    if (temp != null && temp.isTextual()) {
      newGroupNameRegex = temp.asText();
    } else {
      throw new InvalidRuleIfException("newGroupNameRegex: required, string");
    }
  }
}
