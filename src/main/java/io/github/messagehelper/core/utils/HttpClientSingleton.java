package io.github.messagehelper.core.utils;

import java.net.http.HttpClient;

public class HttpClientSingleton {
  private static final HttpClient instance = HttpClient.newHttpClient();

  public static HttpClient getInstance() {
    return instance;
  }

  private HttpClientSingleton() {}
}
