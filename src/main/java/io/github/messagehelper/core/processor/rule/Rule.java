package io.github.messagehelper.core.processor.rule;

import io.github.messagehelper.core.mysql.po.RulePo;
import io.github.messagehelper.core.processor.rule._if.RuleIf;

public class Rule implements Comparable<Rule> {
  @SuppressWarnings("Duplicates")
  public static Rule parse(RulePo po) {
    Rule rule = new Rule();
    rule.setId(po.getId());
    rule.setName(po.getName());
    rule.setRuleIf(po.getRuleIf());
    rule.setRuleThenInstance(po.getRuleThenInstance());
    rule.setRuleThenMethod(po.getRuleThenMethod());
    rule.setRuleThenPath(po.getRuleThenPath());
    rule.setBodyTemplate(po.getBodyTemplate());
    rule.setPriority(po.getPriority());
    rule.setTerminate(po.getTerminate());
    rule.setEnable(po.getEnable());
    return rule;
  }

  private Long id;
  private String name;
  private RuleIf ruleIf;
  private String ruleThenInstance;
  private String ruleThenMethod;
  private String ruleThenPath;
  private String bodyTemplate;
  private Integer priority;
  private Boolean terminate;
  private Boolean enable;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public RuleIf getRuleIf() {
    return ruleIf;
  }

  public void setRuleIf(String json) {
    ruleIf = RuleIf.parse(json);
  }

  public String getRuleThenInstance() {
    return ruleThenInstance;
  }

  public void setRuleThenInstance(String ruleThenInstance) {
    this.ruleThenInstance = ruleThenInstance;
  }

  public String getRuleThenMethod() {
    return ruleThenMethod;
  }

  public void setRuleThenMethod(String ruleThenMethod) {
    this.ruleThenMethod = ruleThenMethod;
  }

  public String getRuleThenPath() {
    return ruleThenPath;
  }

  public void setRuleThenPath(String ruleThenPath) {
    this.ruleThenPath = ruleThenPath;
  }

  public String getBodyTemplate() {
    return bodyTemplate;
  }

  public void setBodyTemplate(String bodyTemplate) {
    this.bodyTemplate = bodyTemplate;
  }

  public Integer getPriority() {
    return priority;
  }

  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  public Boolean getTerminate() {
    return terminate;
  }

  public void setTerminate(Boolean terminate) {
    this.terminate = terminate;
  }

  public Boolean getEnable() {
    return enable;
  }

  public void setEnable(Boolean enable) {
    this.enable = enable;
  }

  @Override
  public int compareTo(Rule o) {
    return this.priority - o.priority;
  }

  private Rule() {}
}
