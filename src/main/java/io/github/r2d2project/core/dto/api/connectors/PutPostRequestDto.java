package io.github.r2d2project.core.dto.api.connectors;

import io.github.r2d2project.core.dto.api.ApiTokenRequestDto;
import io.github.r2d2project.core.storage.Constant;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;

public class PutPostRequestDto extends ApiTokenRequestDto {
  public static final String EXCEPTION_MESSAGE_URL =
      "url: required, url string with length in [1, " + Constant.CONNECTOR_URL_LENGTH + "]";
  private static final String EXCEPTION_MESSAGE_RPC_TOKEN =
      "rpcToken: required, string with length in [1, " + Constant.CONNECTOR_RPC_TOKEN_LENGTH + "]";

  @Length(min = 1, max = Constant.CONNECTOR_URL_LENGTH, message = EXCEPTION_MESSAGE_URL)
  @NotNull(message = EXCEPTION_MESSAGE_URL)
  @URL(message = EXCEPTION_MESSAGE_URL)
  private String url;

  @Length(min = 1, max = Constant.CONNECTOR_RPC_TOKEN_LENGTH, message = EXCEPTION_MESSAGE_RPC_TOKEN)
  @NotNull(message = EXCEPTION_MESSAGE_RPC_TOKEN)
  private String rpcToken;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getRpcToken() {
    return rpcToken;
  }

  public void setRpcToken(String rpcToken) {
    this.rpcToken = rpcToken;
  }
}
