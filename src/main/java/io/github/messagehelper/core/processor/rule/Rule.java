package io.github.messagehelper.core.processor.rule;

import io.github.messagehelper.core.mysql.po.RulePo;
import io.github.messagehelper.core.processor.log.Log;
import io.github.messagehelper.core.processor.log.content.Unit;
import io.github.messagehelper.core.processor.rule._if.Condition;

import java.util.List;
import java.util.Map;

public class Rule implements Comparable<Rule> {
  public static final int HIT = 0;
  public static final int MISS_INSTANCE = -1;
  public static final int MISS_CATEGORY = -2;
  public static final int MISS_CONTENT_FORMAT = Integer.MAX_VALUE;

  public static Rule parse(RulePo po) {
    Rule rule = new Rule();
    rule.id = po.getId();
    rule.name = po.getName();
    rule.ifLogInstanceEqual = po.getIfLogInstanceEqual();
    rule.ifLogCategoryEqual = po.getIfLogCategoryEqual();
    rule.ifLogContentSatisfy = Condition.toList(po.getIfLogContentSatisfy());
    rule.thenUseConnectorId = po.getThenUseConnectorId();
    rule.thenUseUrlPath = po.getThenUseUrlPath();
    rule.thenUseHeaderContentType = po.getThenUseHeaderContentType();
    rule.thenUseHeaderJson = po.getThenUseHeaderJson();
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
  private List<Condition> ifLogContentSatisfy;
  private Long thenUseConnectorId;
  private String thenUseUrlPath;
  private String thenUseHeaderContentType;
  private Boolean thenUseHeaderJson;
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

  public List<Condition> getIfLogContentSatisfy() {
    return ifLogContentSatisfy;
  }

  public Long getThenUseConnectorId() {
    return thenUseConnectorId;
  }

  public String getThenUseUrlPath() {
    return thenUseUrlPath;
  }

  public String getThenUseHeaderContentType() {
    return thenUseHeaderContentType;
  }

  public Boolean getThenUseHeaderJson() {
    return thenUseHeaderJson;
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

  public int satisfy(Log log) {
    if (!log.getInstance().equals(ifLogInstanceEqual)) {
      return MISS_INSTANCE;
    }
    if (!log.getCategory().equals(ifLogCategoryEqual)) {
      return MISS_CATEGORY;
    }
    if (ifLogContentSatisfy.size() <= 0) {
      return HIT;
    }
    int index = 0;
    int skip = 0;
    Map<String, Unit> content = log.getContent();
    Unit unit;
    for (Condition condition : ifLogContentSatisfy) {
      if (content.get(condition.getPath()) == null) {
        // `condition.path` is undefined in `log.content`.
        //
        // => skip (not consider)
        skip += 1;
        continue;
      }
      unit = content.get(condition.getPath());
      if (!condition.getOperator().suit(unit.getType())) {
        // `condition.operator` is not suitable for the type of the value
        // of the corresponding path in `log.content`.
        //
        // For example, the type of the value of `log.content.path.example` is string,
        // but `condition.operator` is `GREATER_THAN` (which is only suitable for number).
        //
        // => skip (not consider)
        skip += 1;
        continue;
      }
      if (!condition.meet(unit)) {
        return index + 1;
      }
      index += 1;
    }
    if (skip >= content.size()) {
      // If `log.content` skips all conditions, `log` will not satisfy the rule.
      return MISS_CONTENT_FORMAT;
    }
    return HIT;
  }

  private Rule() {}
}
