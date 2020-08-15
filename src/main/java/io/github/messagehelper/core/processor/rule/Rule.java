package io.github.messagehelper.core.processor.rule;

import io.github.messagehelper.core.mysql.po.RulePo;
import io.github.messagehelper.core.processor.rule._if.RuleIf;
import io.github.messagehelper.core.processor.rule.then.RuleThen;

public class Rule implements Comparable<Rule> {
  public static Rule parse(RulePo po) {
    Rule rule = new Rule();
    rule.setId(po.getId());
    rule.setName(po.getName());
    rule.setRuleIf(po.getIfContent());
    rule.setRuleThen(po.getThenContent());
    rule.setPriority(po.getPriority());
    rule.setTerminate(po.getTerminate());
    return rule;
  }

  private Long id;
  private String name;
  private RuleIf ruleIf;
  private RuleThen ruleThen;
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

  public RuleThen getRuleThen() {
    return ruleThen;
  }

  public void setRuleThen(String json) {
    ruleThen = RuleThen.parse(json);
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
