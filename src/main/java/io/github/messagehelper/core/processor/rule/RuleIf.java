package io.github.messagehelper.core.processor.rule;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.messagehelper.core.processor.log.Log;
import io.github.messagehelper.core.processor.rule.webhook_connector.receive.Receive;
import io.github.messagehelper.core.processor.rule.wechat_connector.listener.wechat.friendship.Friendship;
import io.github.messagehelper.core.processor.rule.wechat_connector.listener.wechat.message.Message;
import io.github.messagehelper.core.processor.rule.wechat_connector.listener.wechat.room_topic.RoomTopic;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public class RuleIf {
  public static RuleIf parse(String json, String category) {
    if (category.equals("webhook-connector.receive")) {
      return new Receive(json);
    } else if (category.equals("wechat-connector.listener.wechat.friendship")) {
      return new Friendship(json);
    } else if (category.equals("wechat-connector.listener.wechat.message")) {
      return new Message(json);
    } else if (category.equals("wechat-connector.listener.wechat.room-topic")) {
      return new RoomTopic(json);
    } else {
      return new RuleIf();
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

  public boolean satisfy(Log log) {
    return true;
  }
}
