package io.github.messagehelper.core.utils;

import org.springframework.http.HttpHeaders;

public class DisableCacheHeader {
  private static HttpHeaders instance;

  static {
    instance = new HttpHeaders();
    instance.add("content-type", "application/json;charset=utf-8");
    instance.add("cache-control", "no-store, no-cache, must-revalidate, proxy-revalidate");
    instance.add("pragma", "no-cache");
    instance.add("expires", "no-store");
    instance.add("surrogate-control", "no-store");
  }

  public static HttpHeaders getInstance() {
    return instance;
  }

  private DisableCacheHeader() {}
}
