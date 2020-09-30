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
  public static String validateList(String json) {
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
    ArrayNode output = ObjectMapperSingleton.getInstance().createArrayNode();
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
      outputNode = ObjectMapperSingleton.getInstance().createObjectNode();
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
        if (operator.needDetail()) {
          // .detail
          temp = inputNode.get("detail");
          if (temp == null) {
            throw new RuleIfInvalidContentException(
                String.format(
                    "ifLogContentSatisfy[%d].detail: required, %s",
                    index, detailMessage(operator)));
          } else if (temp.isBoolean() && operator.suit(Type.BOOLEAN)) {
            outputNode.put("detail", output.asBoolean());
          } else if (temp.isDouble() && operator.suit(Type.DOUBLE)) {
            outputNode.put("detail", output.asDouble());
          } else if (temp.isTextual() && operator.suit(Type.STRING)) {
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

  public static List<Condition> toList(String json) {
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
    if (operator.suit(Type.BOOLEAN)) {
      return "boolean";
    } else if (operator.suit(Type.DOUBLE)) {
      return "number";
    } else if (operator.suit(Type.STRING)) {
      return "string";
    } else {
      return "";
    }
  }

  private static Condition helper(JsonNode node) {
    String path = node.get("path").asText();
    Operator operator = Operator.valueOf(node.get("operator").asText());
    Object detail = null;
    JsonNode temp = node.get("path");
    if (temp.isBoolean() && operator.suit(Type.BOOLEAN)) {
      detail = temp.asBoolean();
    } else if (temp.isDouble() && operator.suit(Type.DOUBLE)) {
      detail = temp.asDouble();
    } else if (temp.isTextual() && operator.suit(Type.STRING)) {
      detail = temp.asText();
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
      case NULL:
        return unit.valueIsNull();
      case NOT_NULL:
        return !unit.valueIsNull();
      case OBJECT:
        return unit.valueIsObject();
      case NOT_OBJECT:
        return !unit.valueIsObject();
      case ARRAY:
        return unit.valueIsArray();
      default: // NOT_ARRAY
        return !unit.valueIsArray();
    }
  }

  private Double detailAsDouble() {
    if (!(detail instanceof Double)) {
      throw new RuntimeException("Type of `detail` should be `Double`.");
    }
    return (Double) detail;
  }

  private String detailAsString() {
    if (!(detail instanceof String)) {
      throw new RuntimeException("Type of `detail` should be `String`.");
    }
    return (String) detail;
  }
}