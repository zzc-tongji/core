package io.github.messagehelper.core.processor.rule.wechat_connector.listener.wechat.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.messagehelper.core.exception.InvalidRuleIfException;
import io.github.messagehelper.core.processor.log.Log;
import io.github.messagehelper.core.processor.rule.RuleIf;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public class Message extends RuleIf {
  private String messageTypeRegex;
  private String messageTextRegex;
  private String messageFileNameRegex;
  private String oneNameRegex;
  private String oneAliasRegex;
  private String oneAliasInGroupRegex;
  private Integer oneIsFriendJudge;
  private String groupNameRegex;

  public String getMessageTypeRegex() {
    return messageTypeRegex;
  }

  public void setMessageTypeRegex(String messageTypeRegex) {
    this.messageTypeRegex = messageTypeRegex;
  }

  public String getMessageTextRegex() {
    return messageTextRegex;
  }

  public void setMessageTextRegex(String messageTextRegex) {
    this.messageTextRegex = messageTextRegex;
  }

  public String getMessageFileNameRegex() {
    return messageFileNameRegex;
  }

  public void setMessageFileNameRegex(String messageFileNameRegex) {
    this.messageFileNameRegex = messageFileNameRegex;
  }

  public String getOneNameRegex() {
    return oneNameRegex;
  }

  public void setOneNameRegex(String oneNameRegex) {
    this.oneNameRegex = oneNameRegex;
  }

  public String getOneAliasRegex() {
    return oneAliasRegex;
  }

  public void setOneAliasRegex(String oneAliasRegex) {
    this.oneAliasRegex = oneAliasRegex;
  }

  public String getOneAliasInGroupRegex() {
    return oneAliasInGroupRegex;
  }

  public void setOneAliasInGroupRegex(String oneAliasInGroupRegex) {
    this.oneAliasInGroupRegex = oneAliasInGroupRegex;
  }

  public Integer getOneIsFriendJudge() {
    return oneIsFriendJudge;
  }

  public void setOneIsFriendJudge(Integer oneIsFriendJudge) {
    this.oneIsFriendJudge = oneIsFriendJudge;
  }

  public String getGroupNameRegex() {
    return groupNameRegex;
  }

  public void setGroupNameRegex(String groupNameRegex) {
    this.groupNameRegex = groupNameRegex;
  }

  @Override
  public boolean satisfy(Log log) {
    io.github.messagehelper.core.processor.log.content.Content content = log.getContent();
    if (!(content
        instanceof
        io.github.messagehelper.core.processor.log.content.wechatconnector.listener.wechat
            .Message)) {
      return false;
    }
    io.github.messagehelper.core.processor.log.content.wechatconnector.listener.wechat.Message
        message =
            (io.github.messagehelper.core.processor.log.content.wechatconnector.listener.wechat
                    .Message)
                content;
    if (!message.getMessageType().matches(messageTypeRegex)) {
      return false;
    }
    if (!message.getMessageText().matches(messageTextRegex)) {
      return false;
    }
    if (!message.getMessageFileName().matches(messageFileNameRegex)) {
      return false;
    }
    if (!message.getOneName().matches(oneNameRegex)) {
      return false;
    }
    if (!message.getOneAlias().matches(oneAliasRegex)) {
      return false;
    }
    if (!message.getOneAliasInGroup().matches(oneAliasInGroupRegex)) {
      return false;
    }
    if (message.getOneIsFriend() && oneIsFriendJudge < 0) {
      return false;
    }
    if (!message.getOneIsFriend() && oneIsFriendJudge > 0) {
      return false;
    }
    return message.getGroupName().matches(groupNameRegex);
  }

  public Message(String json) {
    JsonNode jsonNode;
    try {
      jsonNode = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new InvalidRuleIfException(e);
    }
    JsonNode temp = jsonNode.get("messageTypeRegex");
    if (temp != null && temp.isTextual()) {
      messageTypeRegex = temp.asText();
    } else {
      throw new InvalidRuleIfException("messageTypeRegex: required, string");
    }
    temp = jsonNode.get("messageTextRegex");
    if (temp != null && temp.isTextual()) {
      messageTextRegex = temp.asText();
    } else {
      throw new InvalidRuleIfException("messageTextRegex: required, string");
    }
    temp = jsonNode.get("messageFileNameRegex");
    if (temp != null && temp.isTextual()) {
      messageFileNameRegex = temp.asText();
    } else {
      throw new InvalidRuleIfException("messageFileNameRegex: required, string");
    }
    temp = jsonNode.get("oneNameRegex");
    if (temp != null && temp.isTextual()) {
      oneNameRegex = temp.asText();
    } else {
      throw new InvalidRuleIfException("oneNameRegex: required, string");
    }
    temp = jsonNode.get("oneAliasRegex");
    if (temp != null && temp.isTextual()) {
      oneAliasRegex = temp.asText();
    } else {
      throw new InvalidRuleIfException("oneAliasRegex: required, string");
    }
    temp = jsonNode.get("oneAliasInGroupRegex");
    if (temp != null && temp.isTextual()) {
      oneAliasInGroupRegex = temp.asText();
    } else {
      throw new InvalidRuleIfException("oneAliasInGroupRegex: required, string");
    }
    temp = jsonNode.get("oneIsFriendJudge");
    if (temp != null && temp.isIntegralNumber() && temp.canConvertToInt()) {
      oneIsFriendJudge = temp.asInt();
    } else {
      throw new InvalidRuleIfException("oneIsFriendJudge: required, int");
    }
    temp = jsonNode.get("groupNameRegex");
    if (temp != null && temp.isTextual()) {
      groupNameRegex = temp.asText();
    } else {
      throw new InvalidRuleIfException("groupNameRegex: required, string");
    }
  }
}
