package io.github.r2d2project.core.dto.api.logs;

public class GetResponse {
  private Data data;

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public GetResponse() {
    data = new Data();
  }
}
