package io.github.messagehelper.core.utils;

import com.fasterxml.jackson.databind.JsonNode;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

public class IdGenerator {
  private static final IdGenerator instance = new IdGenerator();
  private final Random random;

  public static IdGenerator getInstance() {
    return instance;
  }

  private IdGenerator() {
    random = new Random();
  }

  public long generate() {
    try {
      String url = ConfigMapSingleton.getInstance().load("core.id-generator");
      HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();
      HttpResponse<String> response =
          HttpClientSingleton.getInstance().send(request, HttpResponse.BodyHandlers.ofString());
      int statusCode = response.statusCode();
      if (statusCode <= 199 || statusCode >= 300) {
        return generateNegative();
      }
      JsonNode jsonNode = ObjectMapperSingleton.getInstance().readTree(response.body());
      JsonNode temp = jsonNode.get("id");
      if (temp != null && temp.isIntegralNumber() && temp.canConvertToLong()) {
        long result = temp.asLong();
        return result > 0 ? result : generateNegative();
      } else {
        return generateNegative();
      }
    } catch (Exception e) {
      return generateNegative();
    }
  }

  public long generateNegative() {
    long result;
    do {
      result = random.nextLong();
    } while (result >= 0);
    return result;
  }
}
