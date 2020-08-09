package io.github.messagehelper.core.dto.api.deliveries;

import io.github.messagehelper.core.dto.TokenRequestDto;
import io.github.messagehelper.core.rule.then.RuleThen;

import javax.validation.constraints.NotNull;

public class PostRequestDto extends TokenRequestDto {
  @NotNull(message = "payload: required, string as json")
  private RuleThen payload;

  public RuleThen getPayload() {
    return payload;
  }

  public void setPayload(RuleThen payload) {
    this.payload = payload;
  }
}
