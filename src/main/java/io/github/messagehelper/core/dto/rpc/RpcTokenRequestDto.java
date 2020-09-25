package io.github.messagehelper.core.dto.rpc;

public class RpcTokenRequestDto {
  private String rpcToken;

  public String getRpcToken() {
    if (rpcToken == null) {
      return "";
    }
    return rpcToken;
  }

  public void setRpcToken(String apiToken) {
    if (apiToken == null) {
      this.rpcToken = "";
    }
    this.rpcToken = apiToken;
  }
}
