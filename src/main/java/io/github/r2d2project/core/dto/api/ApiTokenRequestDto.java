package io.github.r2d2project.core.dto.api;

public class ApiTokenRequestDto {
  private String apiToken;

  public String getApiToken() {
    if (apiToken == null) {
      return "";
    }
    return apiToken;
  }

  public void setApiToken(String apiToken) {
    if (apiToken == null) {
      this.apiToken = "";
    }
    this.apiToken = apiToken;
  }
}
