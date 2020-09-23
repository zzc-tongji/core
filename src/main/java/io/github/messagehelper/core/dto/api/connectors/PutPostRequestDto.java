package io.github.messagehelper.core.dto.api.connectors;

import io.github.messagehelper.core.dto.api.ApiTokenRequestDto;
import io.github.messagehelper.core.mysql.Constant;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;

public class PutPostRequestDto extends ApiTokenRequestDto {
  @Length(
      min = 1,
      max = Constant.INSTANCE_LENGTH,
      message =
          "instance: required, a not \""
              + Constant.CONNECTOR_INSTANCE_VIRTUAL
              + "\" string with length in [1, "
              + Constant.INSTANCE_LENGTH
              + "] which cannot be converted to long")
  @NotNull(
      message =
          "instance: required, a not \""
              + Constant.CONNECTOR_INSTANCE_VIRTUAL
              + "\" string with length in [1, "
              + Constant.INSTANCE_LENGTH
              + "] which cannot be converted to long")
  private String instance;

  @Length(
      min = 1,
      max = Constant.CATEGORY_LENGTH,
      message = "category: required, string with length in [1, " + Constant.CATEGORY_LENGTH + "]")
  @NotNull(
      message = "category: required, string with length in [1, " + Constant.CATEGORY_LENGTH + "]")
  private String category;

  @Length(
      min = 1,
      max = Constant.CONNECTOR_URL_LENGTH,
      message =
          "url: required, url string with length in [1, " + Constant.CONNECTOR_URL_LENGTH + "]")
  @NotNull(
      message =
          "url: required, url string with length in [1, " + Constant.CONNECTOR_URL_LENGTH + "]")
  @URL(
      message =
          "url: required, url string with length in [1, " + Constant.CONNECTOR_URL_LENGTH + "]")
  private String url;

  @Length(
      min = 1,
      max = Constant.CONNECTOR_RPC_TOKEN_LENGTH,
      message =
          "rpcToken: required, string with length in [1, "
              + Constant.CONNECTOR_RPC_TOKEN_LENGTH
              + "]")
  @NotNull(
      message =
          "rpcToken: required, string with length in [1, "
              + Constant.CONNECTOR_RPC_TOKEN_LENGTH
              + "]")
  private String rpcToken;

  public String getInstance() {
    return instance;
  }

  public void setInstance(String instance) {
    this.instance = instance;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

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
