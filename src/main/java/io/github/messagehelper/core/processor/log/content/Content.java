package io.github.messagehelper.core.processor.log.content;

public class Content {
  public static Content parse(String category, String json) {
    // general
    switch (category) {
      case "wechat-connector.cache.get":
        return new io.github.messagehelper.core.processor.log.content.wechatconnector.cache.Get(
            json);
      case "wechat-connector.cache.set":
        return new io.github.messagehelper.core.processor.log.content.wechatconnector.cache.Set(
            json);
      case "wechat-connector.listener.http.listen":
        return new io.github.messagehelper.core.processor.log.content.wechatconnector.listener.http
            .Listen(json);
      case "wechat-connector.listener.wechat.error":
        return new io.github.messagehelper.core.processor.log.content.wechatconnector.listener
            .wechat.Error(json);
      case "wechat-connector.listener.wechat.friendship":
        return new io.github.messagehelper.core.processor.log.content.wechatconnector.listener
            .wechat.Friendship(json);
      case "wechat-connector.listener.wechat.message":
        return new io.github.messagehelper.core.processor.log.content.wechatconnector.listener
            .wechat.Message(json);
      case "wechat-connector.listener.wechat.room-invite":
        return new io.github.messagehelper.core.processor.log.content.wechatconnector.listener
            .wechat.RoomInvite(json);
      case "wechat-connector.listener.wechat.room-join":
        return new io.github.messagehelper.core.processor.log.content.wechatconnector.listener
            .wechat.RoomJoin(json);
      case "wechat-connector.listener.wechat.room-leave":
        return new io.github.messagehelper.core.processor.log.content.wechatconnector.listener
            .wechat.RoomLeave(json);
      case "wechat-connector.listener.wechat.room-topic":
        return new io.github.messagehelper.core.processor.log.content.wechatconnector.listener
            .wechat.RoomTopic(json);
      case "wechat-connector.requestor.wechat.error":
        return new io.github.messagehelper.core.processor.log.content.wechatconnector.requestor
            .wechat.Error(json);
      case "wechat-connector.requestor.wechat.forward":
        return new io.github.messagehelper.core.processor.log.content.wechatconnector.requestor
            .wechat.Forward(json);
      case "wechat-connector.requestor.wechat.reply":
        return new io.github.messagehelper.core.processor.log.content.wechatconnector.requestor
            .wechat.Reply(json);
      case "wechat-connector.requestor.wechat.send":
        return new io.github.messagehelper.core.processor.log.content.wechatconnector.requestor
            .wechat.Send(json);
      case "wechat-connector.requestor.wechat.sync":
        return new io.github.messagehelper.core.processor.log.content.wechatconnector.requestor
            .wechat.Sync(json);
      default:
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
        return new Content();
    }
  }
}
