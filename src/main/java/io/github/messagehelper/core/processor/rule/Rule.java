package io.github.messagehelper.core.processor.rule;

import io.github.messagehelper.core.mysql.po.RulePo;
import io.github.messagehelper.core.processor.log.Log;

public class Rule implements Comparable<Rule> {
  public static Rule parse(RulePo po) {
    Rule rule = new Rule();
    rule.id = po.getId();
    rule.name = po.getName();
    rule.ifLogInstanceEqual = po.getIfLogInstanceEqual();
    rule.ifLogCategoryEqual = po.getIfLogCategoryEqual();
    rule.ifLogContentSatisfy =
        RuleIf.parse(po.getIfLogContentSatisfy(), po.getIfLogCategoryEqual());
    rule.thenUseConnectorId = po.getThenUseConnectorId();
    rule.thenUseHttpMethod = po.getThenUseHttpMethod();
    rule.thenUseUrlPath = po.getThenUseUrlPath();
    rule.thenUseBodyTemplate = po.getThenUseBodyTemplate();
    rule.priority = po.getPriority();
    rule.terminate = po.getTerminate();
    rule.enable = po.getEnable();
    rule.annotation = po.getAnnotation();
    return rule;
  }

  private Long id;
  private String name;
  private String ifLogInstanceEqual;
  private String ifLogCategoryEqual;
  private RuleIf ifLogContentSatisfy;
  private Long thenUseConnectorId;
  private String thenUseHttpMethod;
  private String thenUseUrlPath;
  private String thenUseBodyTemplate;
  private Integer priority;
  private Boolean terminate;
  private Boolean enable;
  private String annotation;

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getIfLogInstanceEqual() {
    return ifLogInstanceEqual;
  }

  public String getIfLogCategoryEqual() {
    return ifLogCategoryEqual;
  }

  public RuleIf getIfLogContentSatisfy() {
    return ifLogContentSatisfy;
  }

  public Long getThenUseConnectorId() {
    return thenUseConnectorId;
  }

  public String getThenUseHttpMethod() {
    return thenUseHttpMethod;
  }

  public String getThenUseUrlPath() {
    return thenUseUrlPath;
  }

  public String getThenUseBodyTemplate() {
    return thenUseBodyTemplate;
  }

  public Integer getPriority() {
    return priority;
  }

  public Boolean getTerminate() {
    return terminate;
  }

  public Boolean getEnable() {
    return enable;
  }

  public String getAnnotation() {
    return annotation;
  }

  @Override
  public int compareTo(Rule o) {
    return this.priority - o.priority;
  }

  public boolean satisfy(Log log) {
    if (!log.getInstance().equals(ifLogInstanceEqual)) {
      return false;
    }
    if (!log.getCategory().equals(ifLogCategoryEqual)) {
      return false;
    }
    return ifLogContentSatisfy.satisfy(log);
  }

  private Rule() {}
}
