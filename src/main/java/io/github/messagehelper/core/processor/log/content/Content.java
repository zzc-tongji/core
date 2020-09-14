package io.github.messagehelper.core.processor.log.content;

public class Content {
  public static Content parse(String category, String json) {
    switch (category) {
      case "core.webhook":
        return new io.github.messagehelper.core.processor.log.content.core.Webhook(json);
      case "http-connector.listener.http.receive":
        return new io.github.messagehelper.core.processor.log.content.httpconnector.listener.http
            .Receive(json);
      case "http-connector.requestor.http.execute.response":
        return new io.github.messagehelper.core.processor.log.content.httpconnector.requestor.http
            .execute.Response(json);
      case "wechat-connector.listener.wechat.friendship":
        return new io.github.messagehelper.core.processor.log.content.wechatconnector.listener
            .wechat.Friendship(json);
      case "wechat-connector.listener.wechat.message":
        return new io.github.messagehelper.core.processor.log.content.wechatconnector.listener
            .wechat.Message(json);
      case "wechat-connector.listener.wechat.room-topic":
        return new io.github.messagehelper.core.processor.log.content.wechatconnector.listener
            .wechat.RoomTopic(json);
      default:
        return new Content();
    }
  }

  protected Content() {}
}
