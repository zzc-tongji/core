package io.github.messagehelper.core.dto.api;

import io.github.messagehelper.core.utils.ConfigMapSingleton;

public class GetResponse {
  private final Boolean registered;
  private final String document;

  public Boolean getRegistered() {
    return registered;
  }

  public String getDocument() {
    return document;
  }

  public GetResponse() {
    ConfigMapSingleton x = ConfigMapSingleton.getInstance();
    String a = x.load("core.api-password-hash");
    registered =
        (ConfigMapSingleton.getInstance().load("core.api-password-hash").length() > 0
            && ConfigMapSingleton.getInstance().load("core.api-password-salt").length() > 0);
    document = ConfigMapSingleton.getInstance().load("core.api-document");
  }
}
