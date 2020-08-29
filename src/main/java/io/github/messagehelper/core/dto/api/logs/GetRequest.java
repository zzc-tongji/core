package io.github.messagehelper.core.dto.api.logs;

public class GetRequest {
  private static final int DEFAULT_PAGE = 1;
  private static final int DEFAULT_SIZE = 100;
  private static final String DEFAULT_ORDER = "id";

  // url without query string (not-nullable, default: "")
  private String url;
  // valid statement counter (default: 0)
  private int statementNumber;
  // statement (all nullable)
  private Long idGreaterThan;
  private Long idLessThan;
  private String instanceContain;
  private String levelContain;
  private String categoryContain;
  private Long timestampMsGreaterThan;
  private Long timestampMsLessThan;
  private String contentContain;
  // sort
  private String order; // (not-nullable, default: DEFAULT_ORDER)
  private boolean ascending; // (default: false)
  // pagination
  private long page; // (default: DEFAULT_PAGE)
  private long size; // (default: DEFAULT_SIZE)

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    if (url == null || url.length() <= 0) {
      this.url = "";
    }
    this.url = url;
  }

  public int getStatementNumber() {
    return statementNumber;
  }

  public Long getIdGreaterThan() {
    return idGreaterThan;
  }

  private void setIdGreaterThan(String idGreaterThan) {
    try {
      if (idGreaterThan == null || idGreaterThan.length() <= 0) {
        this.idGreaterThan = null;
        return;
      }
      this.idGreaterThan = Long.parseLong(idGreaterThan);
      statementNumber += 1;
    } catch (NumberFormatException e) {
      this.idGreaterThan = null;
    }
  }

  public Long getIdLessThan() {
    return idLessThan;
  }

  private void setIdLessThan(String idLessThan) {
    try {
      if (idLessThan == null || idLessThan.length() <= 0) {
        this.idLessThan = null;
        return;
      }
      this.idLessThan = Long.parseLong(idLessThan);
      statementNumber += 1;
    } catch (NumberFormatException e) {
      this.idLessThan = null;
    }
  }

  public String getInstanceContain() {
    return instanceContain;
  }

  private void setInstanceContain(String instanceContain) {
    if (instanceContain == null || instanceContain.length() <= 0) {
      this.instanceContain = null;
      return;
    }
    this.instanceContain = instanceContain;
    statementNumber += 1;
  }

  public String getLevelContain() {
    return levelContain;
  }

  private void setLevelContain(String levelContain) {
    if (levelContain == null || levelContain.length() <= 0) {
      this.levelContain = null;
      return;
    }
    this.levelContain = levelContain;
    statementNumber += 1;
  }

  public String getCategoryContain() {
    return categoryContain;
  }

  private void setCategoryContain(String categoryContain) {
    if (categoryContain == null || categoryContain.length() <= 0) {
      this.categoryContain = null;
      return;
    }
    this.categoryContain = categoryContain;
    statementNumber += 1;
  }

  public Long getTimestampMsGreaterThan() {
    return timestampMsGreaterThan;
  }

  private void setTimestampMsGreaterThan(String timestampMsGreaterThan) {
    try {
      if (timestampMsGreaterThan == null || timestampMsGreaterThan.length() <= 0) {
        this.timestampMsGreaterThan = null;
        return;
      }
      this.timestampMsGreaterThan = Long.parseLong(timestampMsGreaterThan);
      statementNumber += 1;
    } catch (NumberFormatException e) {
      this.timestampMsGreaterThan = null;
    }
  }

  public Long getTimestampMsLessThan() {
    return timestampMsLessThan;
  }

  private void setTimestampMsLessThan(String timestampMsLessThan) {
    try {
      if (timestampMsLessThan == null || timestampMsLessThan.length() <= 0) {
        this.timestampMsLessThan = null;
        return;
      }
      this.timestampMsLessThan = Long.parseLong(timestampMsLessThan);
      statementNumber += 1;
    } catch (NumberFormatException e) {
      this.timestampMsLessThan = null;
    }
  }

  public String getContentContain() {
    return contentContain;
  }

  private void setContentContain(String contentContain) {
    if (contentContain == null || contentContain.length() <= 0) {
      this.contentContain = null;
      return;
    }
    this.contentContain = contentContain;
    statementNumber += 1;
  }

  public String getOrder() {
    return order;
  }

  private void setOrder(String order) {
    if (order == null || order.length() <= 0) {
      this.order = DEFAULT_ORDER;
      return;
    }
    if (!order.equals("id")
        && !order.equals("instance")
        && !order.equals("level")
        && !order.equals("category")
        && !order.equals("timestampMs")
        && !order.equals("content")) {
      this.order = DEFAULT_ORDER;
      return;
    }
    this.order = order;
  }

  public boolean getAscending() {
    return ascending;
  }

  private void setAscending(String ascending) {
    this.ascending = (ascending != null && ascending.equals("true"));
  }

  public long getPage() {
    return page;
  }

  public void setPage(String page) {
    try {
      if (page == null || page.length() <= 0) {
        this.page = DEFAULT_PAGE;
        return;
      }
      long p = Long.parseLong(page);
      this.page = p <= 0 ? DEFAULT_PAGE : p;
    } catch (NumberFormatException e) {
      this.page = DEFAULT_PAGE;
    }
  }

  public long getSize() {
    return size;
  }

  public void setSize(String size) {
    try {
      if (size == null || size.length() <= 0) {
        this.size = DEFAULT_SIZE;
        return;
      }
      long s = Long.parseLong(size);
      this.size = s <= 0 ? DEFAULT_SIZE : s;
    } catch (NumberFormatException e) {
      this.size = DEFAULT_SIZE;
    }
  }

  public GetRequest(
      String url,
      String idGreaterThan,
      String idLessThan,
      String instanceContain,
      String levelContain,
      String categoryString,
      String timestampMsGreaterThan,
      String timestampMsLessThan,
      String contentContain,
      String order,
      String ascending,
      String page,
      String size) {
    setUrl(url);
    statementNumber = 0;
    setIdGreaterThan(idGreaterThan);
    setIdLessThan(idLessThan);
    if (this.idGreaterThan != null && this.idLessThan != null) {
      if (this.idGreaterThan >= this.idLessThan) {
        this.idGreaterThan = null;
        this.idLessThan = null;
        statementNumber -= 2;
      }
    }
    setInstanceContain(instanceContain);
    setLevelContain(levelContain);
    setCategoryContain(categoryString);
    setTimestampMsGreaterThan(timestampMsGreaterThan);
    setTimestampMsLessThan(timestampMsLessThan);
    if (this.timestampMsGreaterThan != null && this.timestampMsLessThan != null) {
      if (this.timestampMsGreaterThan >= this.timestampMsLessThan) {
        this.timestampMsGreaterThan = null;
        this.timestampMsLessThan = null;
        statementNumber -= 2;
      }
    }
    setContentContain(contentContain);
    setOrder(order);
    setAscending(ascending);
    setPage(page);
    setSize(size);
  }

  public String generatePreviousUrl() {
    StringBuilder builder = new StringBuilder();
    // page
    return generateUrl(
        builder.append(url).append("?").append(Constant.PAGE).append("=").append(page - 1));
  }

  public String generateNextUrl() {
    StringBuilder builder = new StringBuilder();
    // page
    return generateUrl(
        builder.append(url).append("?").append(Constant.PAGE).append("=").append(page + 1));
  }

  private String generateUrl(StringBuilder builder) {
    int remainStatementCounter = statementNumber;
    // size
    builder.append("&");
    builder.append(Constant.SIZE);
    builder.append("=");
    builder.append(size);
    // order
    builder.append("&");
    builder.append(Constant.ORDER);
    builder.append("=");
    builder.append(order);
    // ascending
    builder.append("&");
    builder.append(Constant.ASCENDING);
    builder.append("=");
    builder.append(ascending);
    if (remainStatementCounter > 0) {
      builder.append("&");
    }
    // id
    if (idGreaterThan != null) {
      builder.append(Constant.ID_GREATER_THAN);
      builder.append("=");
      builder.append(idGreaterThan);
      if (--remainStatementCounter > 0) {
        builder.append("&");
      }
    }
    if (idLessThan != null) {
      builder.append(Constant.ID_LESS_THAN);
      builder.append("=");
      builder.append(idLessThan);
      if (--remainStatementCounter > 0) {
        builder.append("&");
      }
    }
    // instance
    if (instanceContain != null) {
      builder.append(Constant.INSTANCE_CONTAIN);
      builder.append("=");
      builder.append(instanceContain);
      if (--remainStatementCounter > 0) {
        builder.append("&");
      }
    }
    // level
    if (levelContain != null) {
      builder.append(Constant.LEVEL_CONTAIN);
      builder.append("=");
      builder.append(levelContain);
      if (--remainStatementCounter > 0) {
        builder.append("&");
      }
    }
    // category
    if (categoryContain != null) {
      builder.append(Constant.CATEGORY_CONTAIN);
      builder.append("=");
      builder.append(categoryContain);
      if (--remainStatementCounter > 0) {
        builder.append("&");
      }
    }
    // timestamp_ms
    if (timestampMsGreaterThan != null) {
      builder.append(Constant.TIMESTAMP_MS_GREATER_THAN);
      builder.append("=");
      builder.append(timestampMsGreaterThan);
      if (--remainStatementCounter > 0) {
        builder.append("&");
      }
    }
    if (timestampMsLessThan != null) {
      builder.append(Constant.TIMESTAMP_MS_LESS_THAN);
      builder.append("=");
      builder.append(timestampMsLessThan);
      if (--remainStatementCounter > 0) {
        builder.append("&");
      }
    }
    // contain
    if (contentContain != null) {
      builder.append(Constant.CONTENT_CONTAIN);
      builder.append("=");
      builder.append(contentContain);
      if (--remainStatementCounter > 0) {
        builder.append("&");
      }
    }
    return builder.toString();
  }
}
