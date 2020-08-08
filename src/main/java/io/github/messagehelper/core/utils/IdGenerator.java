package io.github.messagehelper.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

public class IdGenerator {
  private static IdGenerator instance = new IdGenerator();
  private Random random;

  public static IdGenerator getInstance() {
    return instance;
  }

  private IdGenerator() {
    random = new Random();
    WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
  }

  public long generate() {
    String url = ConfigMapSingleton.getInstance().load("common.id-generator");
    HttpRequest request;
    try {
      request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();
    } catch (URISyntaxException e) {
      return generateNegative();
    }
    HttpResponse<String> response;
    try {
      response =
          HttpClientSingleton.getInstance().send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      return generateNegative();
    }
    int statusCode = response.statusCode();
    if (statusCode <= 199 || statusCode >= 300) {
      return generateNegative();
    }
    JsonNode jsonNode;
    try {
      jsonNode = ObjectMapperSingleton.getInstance().readTree(response.body());
    } catch (JsonProcessingException e) {
      return generateNegative();
    }
    JsonNode temp = jsonNode.get("id");
    if (temp != null && temp.isIntegralNumber() && temp.canConvertToLong()) {
      return temp.asLong();
    } else {
      return generateNegative();
    }
  }

  private long generateNegative() {
    long result;
    do {
      result = random.nextLong();
    } while (result >= 0);
    return result;
  }
}
