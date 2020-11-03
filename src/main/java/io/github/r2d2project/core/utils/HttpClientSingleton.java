package io.github.r2d2project.core.utils;

import java.net.http.HttpClient;

public class HttpClientSingleton {
  private static final HttpClient instance = HttpClient.newHttpClient();

  public static HttpClient getInstance() {
    return instance;
  }

  private HttpClientSingleton() {}
}
