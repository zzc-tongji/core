package io.github.r2d2project.core.utils;

import org.springframework.http.HttpHeaders;

public class DisableCacheHeader {
  private static final HttpHeaders instance;

  static {
    instance = new HttpHeaders();
    instance.add("Content-Type", "application/json;charset=utf-8");
    instance.add("Cache-Control", "no-store, no-cache, must-revalidate, proxy-revalidate");
    instance.add("Pragma", "no-cache");
    instance.add("Expires", "no-store");
    instance.add("Surrogate-Control", "no-store");
  }

  public static HttpHeaders getInstance() {
    return instance;
  }

  private DisableCacheHeader() {}
}
