package io.github.messagehelper.core.processor.rule._if.wechatconnector.listener.wechat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.messagehelper.core.exception.InvalidRuleIfException;
import io.github.messagehelper.core.processor.log.Log;
import io.github.messagehelper.core.processor.log.content.Content;
import io.github.messagehelper.core.processor.rule._if.RuleIf;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public class Friendship extends RuleIf {
  private String friendshipTypeRegex;
  private String requestMessageRegex;
  private String requesterNameRegex;

  public String getFriendshipTypeRegex() {
    return friendshipTypeRegex;
  }

  public void setFriendshipTypeRegex(String friendshipTypeRegex) {
    this.friendshipTypeRegex = friendshipTypeRegex;
  }

  public String getRequestMessageRegex() {
    return requestMessageRegex;
  }

  public void setRequestMessageRegex(String requestMessageRegex) {
    this.requestMessageRegex = requestMessageRegex;
  }

  public String getRequesterNameRegex() {
    return requesterNameRegex;
  }

  public void setRequesterNameRegex(String requesterNameRegex) {
    this.requesterNameRegex = requesterNameRegex;
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
            .Friendship)) {
      return false;
    }
    io.github.messagehelper.core.processor.log.content.wechatconnector.listener.wechat.Friendship
        friendship =
            (io.github.messagehelper.core.processor.log.content.wechatconnector.listener.wechat
                    .Friendship)
                content;
    if (!friendship.getFriendshipType().matches(friendshipTypeRegex)) {
      return false;
    }
    if (!friendship.getRequestMessage().matches(requestMessageRegex)) {
      return false;
    }
    return friendship.getRequesterName().matches(requesterNameRegex);
  }

  public Friendship(String json) {
    super(json);
    JsonNode jsonNode;
    try {
      jsonNode = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new InvalidRuleIfException(e);
    }
    JsonNode temp = jsonNode.get("friendshipTypeRegex");
    if (temp != null && temp.isTextual()) {
      friendshipTypeRegex = temp.asText();
    } else {
      throw new InvalidRuleIfException("friendshipTypeRegex: required, string");
    }
    temp = jsonNode.get("requestMessageRegex");
    if (temp != null && temp.isTextual()) {
      requestMessageRegex = temp.asText();
    } else {
      throw new InvalidRuleIfException("requestMessageRegex: required, string");
    }
    temp = jsonNode.get("requesterNameRegex");
    if (temp != null && temp.isTextual()) {
      requesterNameRegex = temp.asText();
    } else {
      throw new InvalidRuleIfException("requesterNameRegex: required, string");
    }
  }
}
