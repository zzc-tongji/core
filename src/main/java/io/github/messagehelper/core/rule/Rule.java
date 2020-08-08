package io.github.messagehelper.core.rule;

import io.github.messagehelper.core.mysql.po.RulePo;
import io.github.messagehelper.core.rule._if.RuleIf;
import io.github.messagehelper.core.rule.then.RuleThen;

public class Rule implements Comparable<Rule> {
  private Long id;
  private String name;
  private RuleIf ruleIf;
  private RuleThen ruleThen;
  private Integer priority;
  private Boolean terminate;

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
    ruleThen = new RuleThen(json);
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

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Rule)) {
      return false;
    }
    return obj.hashCode() == this.hashCode();
  }

  @Override
  public int compareTo(Rule o) {
    return this.priority - o.priority;
  }

  public Rule(RulePo po) {
    id = po.getId();
    name = po.getName();
    setRuleIf(po.getIfContent());
    setRuleThen(po.getThenContent());
    priority = po.getPriority();
    terminate = po.getTerminate();
  }
}
