package io.github.messagehelper.core.processor.rule._if;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.messagehelper.core.dto.api.rules.PutPostRequestDto;
import io.github.messagehelper.core.exception.RuleIfInvalidContentException;
import io.github.messagehelper.core.processor.log.content.Type;
import io.github.messagehelper.core.processor.log.content.Unit;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.PatternSyntaxException;

public class Condition {
  public static String validateJsonAsList(String json) {
    JsonNode input;
    try {
      input = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new RuleIfInvalidContentException(
          PutPostRequestDto.EXCEPTION_MESSAGE_IF_LOG_CONTENT_SATISFY);
    }
    if (!input.isContainerNode() || !input.isArray()) {
      throw new RuleIfInvalidContentException(
          PutPostRequestDto.EXCEPTION_MESSAGE_IF_LOG_CONTENT_SATISFY);
    }
    ArrayNode output = ObjectMapperSingleton.getInstance().getNodeFactory().arrayNode();
    Iterator<JsonNode> iterator = input.elements();
    //
    JsonNode inputNode;
    JsonNode temp;
    String string;
    Operator operator;
    ObjectNode outputNode;
    int index = 0;
    while (iterator.hasNext()) {
      // input
      inputNode = iterator.next();
      outputNode = ObjectMapperSingleton.getInstance().getNodeFactory().objectNode();
      // .path
      temp = inputNode.get("path");
      if (temp != null && temp.isTextual()) {
        outputNode.put("path", temp.asText());
      } else {
        throw new RuleIfInvalidContentException(
            String.format("ifLogContentSatisfy[%d].path: required, string", index));
      }
      // .operator
      temp = inputNode.get("operator");
      if (temp != null && temp.isTextual()) {
        string = temp.asText();
        try {
          operator = Operator.valueOf(string);
        } catch (IllegalArgumentException e) {
          throw new RuleIfInvalidContentException(operatorMessage(index));
        }
        outputNode.put("operator", string);
        if (operator.detailType() != null) {
          // .detail
          temp = inputNode.get("detail");
          if (temp == null) {
            throw new RuleIfInvalidContentException(
                String.format(
                    "ifLogContentSatisfy[%d].detail: required, %s",
                    index, detailMessage(operator)));
          }
          if (operator.equals(Operator.IS)) {
            if (temp.isTextual()) {
              string = output.asText();
              try {
                Type.valueOf(string);
              } catch (IllegalArgumentException e) {
                throw new RuleIfInvalidContentException(
                    String.format(
                        "ifLogContentSatisfy[%d].detail: required, string in %s",
                        index, Type.ENUM_NAME_COLLECTION));
              }
              outputNode.put("detail", output.asText());
            } else {
              throw new RuleIfInvalidContentException(
                  String.format(
                      "ifLogContentSatisfy[%d].detail: required, string in %s",
                      index, Type.ENUM_NAME_COLLECTION));
            }
          } else if (operator.detailType() == Boolean.class && temp.isBoolean()) {
            outputNode.put("detail", output.asBoolean());
          } else if (operator.detailType() == Double.class && temp.isDouble()) {
            outputNode.put("detail", output.asDouble());
          } else if (operator.detailType() == String.class && temp.isTextual()) {
            outputNode.put("detail", output.asText());
          } else {
            throw new RuleIfInvalidContentException(
                String.format(
                    "ifLogContentSatisfy[%d].detail: required, %s",
                    index, detailMessage(operator)));
          }
        }
      } else {
        throw new RuleIfInvalidContentException(operatorMessage(index));
      }
      // output
      output.add(outputNode);
      index += 1;
    }
    return output.toString();
  }

  public static List<Condition> jsonToList(String json) {
    // Assume that `json` has been validated and normalized.
    JsonNode node;
    try {
      node = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    List<Condition> result = new ArrayList<>();
    Iterator<JsonNode> iterator = node.elements();
    while (iterator.hasNext()) {
      result.add(helper(iterator.next()));
    }
    return result;
  }

  public static String listToJson(List<Condition> conditionList) {
    JsonNode node = ObjectMapperSingleton.getInstance().convertValue(conditionList, JsonNode.class);
    Iterator<JsonNode> iterator = node.elements();
    //
    JsonNode current;
    Condition condition;
    int index = 0;
    while (iterator.hasNext()) {
      current = iterator.next();
      condition = conditionList.get(index);
      if (condition.getOperator().detailType() == Boolean.class) {
        ((ObjectNode) current).put("detail", condition.detailAsBoolean());
      } else if (condition.getOperator().detailType() == Double.class) {
        ((ObjectNode) current).put("detail", condition.detailAsDouble());
      } else { // condition.getOperator().detailType() == String.class
        ((ObjectNode) current).put("detail", condition.detailAsString());
      }
      index += 1;
    }
    return node.toString();
  }

  private static String operatorMessage(int index) {
    StringBuilder builder = new StringBuilder();
    builder.append("ifLogContentSatisfy[");
    builder.append(index);
    builder.append("].operator: required, string in {");
    for (Operator o : Operator.values()) {
      builder.append("\"");
      builder.append(o);
      builder.append("\", ");
    }
    builder.append("}");
    return builder.toString();
  }

  private static String detailMessage(Operator operator) {
    if (operator.detailType() == Boolean.class) {
      return "boolean";
    } else if (operator.detailType() == Double.class) {
      return "number";
    } else { // operator.detailType() == String.class
      return "string";
    }
  }

  private static Condition helper(JsonNode node) {
    String path = node.get("path").asText();
    Operator operator = Operator.valueOf(node.get("operator").asText());
    Object detail;
    JsonNode temp = node.get("detail");
    if (temp.isDouble()) {
      detail = temp.asDouble();
    } else if (temp.isTextual()) {
      detail = temp.asText();
    } else {
      detail = null;
    }
    return new Condition(path, operator, detail);
  }

  private final String path;
  private final Operator operator;
  private final Object detail;

  public Condition(String path, Operator operator, Object detail) {
    this.path = path;
    this.operator = operator;
    this.detail = detail;
  }

  public String getPath() {
    return path;
  }

  public Operator getOperator() {
    return operator;
  }

  public boolean meet(Unit unit) {
    // Assume that `this.operator` suits `unit.type`
    switch (operator) {
      case TRUE:
        return unit.valueAsBoolean();
      case FALSE:
        return !unit.valueAsBoolean();
      case EQUAL_TO:
        return unit.valueAsDouble().equals(detailAsDouble());
      case GREATER_THAN:
        return unit.valueAsDouble() > detailAsDouble();
      case GREATER_THAN_OR_EQUAL_TO:
        return unit.valueAsDouble() >= detailAsDouble();
      case LESS_THAN:
        return unit.valueAsDouble() < detailAsDouble();
      case LESS_THAN_OR_EQUAL_TO:
        return unit.valueAsDouble() <= detailAsDouble();
      case MATCH_REGEX:
        try {
          return unit.valueAsString().matches(detailAsString());
        } catch (PatternSyntaxException e) {
          // If regex is invalid, treat it as "^.*$" (match everything).
          return true;
        }
      case CONTAIN:
        return unit.valueAsString().contains(detailAsString());
      case NOT_CONTAIN:
        return !unit.valueAsString().contains(detailAsString());
      case EMPTY:
        return unit.valueAsString().length() <= 0;
      case NOT_EMPTY:
        return unit.valueAsString().length() > 0;
      default: // IS
        return unit.getType().name().equals(detailAsString());
    }
  }

  private Boolean detailAsBoolean() {
    if (!(detail instanceof Boolean)) {
      throw new RuntimeException("`detail` should be `Boolean`.");
    }
    return (Boolean) detail;
  }

  private Double detailAsDouble() {
    if (!(detail instanceof Double)) {
      throw new RuntimeException("`detail` should be `Double`.");
    }
    return (Double) detail;
  }

  private String detailAsString() {
    if (!(detail instanceof String)) {
      throw new RuntimeException("`detail` should be `String`.");
    }
    return (String) detail;
  }
}
