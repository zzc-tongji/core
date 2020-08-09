package io.github.messagehelper.core.dto.api.connectors;

import io.github.messagehelper.core.dto.TokenRequestDto;
import io.github.messagehelper.core.mysql.Constant;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;

public class PutPostRequestDto extends TokenRequestDto {
  @Length(
      min = 1,
      max = Constant.CONNECTOR_LENGTH,
      message = "instance: required, string with length in [1, " + Constant.CONNECTOR_LENGTH + "]")
  @NotNull(
      message = "instance: required, string with length in [1, " + Constant.CONNECTOR_LENGTH + "]")
  private String instance;

  @Length(
      min = 1,
      max = Constant.CONNECTOR_LENGTH,
      message = "category: required, string with length in [1, " + Constant.CONNECTOR_LENGTH + "]")
  @NotNull(
      message = "category: required, string with length in [1, " + Constant.CONNECTOR_LENGTH + "]")
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
      max = Constant.CONNECTOR_LENGTH,
      message =
          "connectorToken: required, string with length in [1, " + Constant.CONNECTOR_LENGTH + "]")
  @NotNull(
      message =
          "connectorToken: required, string with length in [1, " + Constant.CONNECTOR_LENGTH + "]")
  private String connectorToken;

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

  public String getConnectorToken() {
    return connectorToken;
  }

  public void setConnectorToken(String connectorToken) {
    this.connectorToken = connectorToken;
  }
}