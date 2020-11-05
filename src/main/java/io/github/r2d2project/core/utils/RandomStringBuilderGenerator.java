package io.github.r2d2project.core.utils;

import java.util.Random;

public class RandomStringBuilderGenerator {
  private static final RandomStringBuilderGenerator instance = new RandomStringBuilderGenerator();
  private final Random random;
  private final String numberAndLowerCase;

  public static RandomStringBuilderGenerator getInstance() {
    return instance;
  }

  private RandomStringBuilderGenerator() {
    random = new Random();
    numberAndLowerCase = "0123456789abcdefghijklmnopqrstuvwxyz";
  }

  public StringBuilder generateNumberAndLowerCase(int length) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < length; i++) {
      int number = random.nextInt(numberAndLowerCase.length());
      builder.append(numberAndLowerCase.charAt(number));
    }
    return builder;
  }
}
