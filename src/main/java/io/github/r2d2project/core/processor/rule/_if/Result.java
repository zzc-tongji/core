package io.github.r2d2project.core.processor.rule._if;

public class Result {
  public static Result yes() {
    return new Result(true, "", "");
  }

  public static Result no(String categorySuffix, String message) {
    return new Result(false, categorySuffix, message);
  }

  private final boolean satisfied;
  private final String categorySuffix;
  private final String message;

  public boolean isSatisfied() {
    return satisfied;
  }

  public String getCategorySuffix() {
    return categorySuffix;
  }

  public String getMessage() {
    return message;
  }

  private Result(boolean satisfied, String categorySuffix, String message) {
    this.satisfied = satisfied;
    this.categorySuffix = categorySuffix;
    this.message = message;
  }
}
