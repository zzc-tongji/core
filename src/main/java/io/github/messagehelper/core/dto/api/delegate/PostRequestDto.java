package io.github.messagehelper.core.dto.api.delegate;

import io.github.messagehelper.core.dto.TokenRequestDto;
import io.github.messagehelper.core.utils.Delegate;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class PostRequestDto extends TokenRequestDto implements Delegate {
  @Length(min = 1, message = "instance: required, string with length >= 1")
  @NotNull
  private String instance;

  @Length(min = 1, message = "path: required, string with length >= 1")
  @NotNull
  private String path;

  @Length(min = 1, message = "body: required, JSON string with length >= 1")
  @NotNull
  private String body;

  @Override
  public String getInstance() {
    return instance;
  }

  public void setInstance(String instance) {
    this.instance = instance;
  }

  @Override
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }
}
