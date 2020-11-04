package io.github.r2d2project.core.processor.rule;

import io.github.r2d2project.core.processor.log.Log;
import io.github.r2d2project.core.processor.log.content.Unit;
import io.github.r2d2project.core.processor.rule._if.Condition;
import io.github.r2d2project.core.processor.rule._if.Result;
import io.github.r2d2project.core.persistence.po.RulePo;

import java.util.List;
import java.util.Map;

public class Rule implements Comparable<Rule> {
  public static final int HIT = -1;
  public static final int MISS_INSTANCE = -2;
  public static final int MISS_CATEGORY = -3;
  public static final int MISS_CONTENT_FORMAT = -4;

  public static Rule parse(RulePo po) {
    Rule rule = new Rule();
    rule.id = po.getId();
    rule.name = po.getName();
    rule.ifLogInstanceEqual = po.getIfLogInstanceEqual();
    rule.ifLogCategoryEqual = po.getIfLogCategoryEqual();
    rule.ifLogContentSatisfy = Condition.jsonToList(po.getIfLogContentSatisfy());
    rule.thenUseConnectorId = po.getThenUseConnectorId();
    rule.thenUseUrlPath = po.getThenUseUrlPath();
    rule.thenUseHeaderContentType = po.getThenUseHeaderContentType();
    rule.thenUseBodyJson = po.getThenUseBodyJson();
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
  private Boolean thenUseBodyJson;
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

  public Boolean getThenUseBodyJson() {
    return thenUseBodyJson;
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

  public Result satisfy(Log log) {
    if (!log.getInstance().equals(ifLogInstanceEqual)) {
      return Result.no(".instance", "instance not matched");
    }
    if (!log.getCategory().equals(ifLogCategoryEqual)) {
      return Result.no(".category", "category not matched");
    }
    if (ifLogContentSatisfy.size() <= 0) {
      return Result.yes();
    }
    int index = 0;
    Map<String, Unit> content = log.getContent();
    Unit unit;
    String path;
    for (Condition condition : ifLogContentSatisfy) {
      path = condition.getPath();
      if (content.get(path) == null) {
        // `condition.path` is undefined in `log.content`.
        return Result.no(".content.undefined", String.format("path \"%s\" undefined", path));
      }
      unit = content.get(condition.getPath());
      if (!condition.getOperator().suit(unit.getType())) {
        // `condition.operator` is not suitable for the type of the value
        // of the corresponding path in `log.content`.
        //
        // For example, the type of the value of `log.content.path.example` is string,
        // but `condition.operator` is `GREATER_THAN` (which is only suitable for number).
        return Result.no(
            ".content.mismatched",
            String.format("path \"%s\" mismatched condition [%d]", path, index));
      }
      if (!condition.meet(unit)) {
        // The value of the corresponding path in `log.content`
        // does not meet the condition.
        return Result.no(
            ".content.conflicted",
            String.format("path \"%s\" conflicted condition [%d]", path, index));
      }
      index += 1;
    }
    return Result.yes();
  }

  private Rule() {}
}
