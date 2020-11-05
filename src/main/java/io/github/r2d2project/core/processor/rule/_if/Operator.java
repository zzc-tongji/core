package io.github.r2d2project.core.processor.rule._if;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.r2d2project.core.processor.log.content.Type;
import io.github.r2d2project.core.utils.ObjectMapperSingleton;

import java.util.ArrayList;
import java.util.List;

public enum Operator {
  TRUE(Type.BOOLEAN.getBitMap(), 0),
  FALSE(Type.BOOLEAN.getBitMap(), 0),
  EQUAL_TO(Type.NUMBER.getBitMap(), Type.NUMBER.getBitMap()),
  GREATER_THAN(Type.NUMBER.getBitMap(), Type.NUMBER.getBitMap()),
  GREATER_THAN_OR_EQUAL_TO(Type.NUMBER.getBitMap(), Type.NUMBER.getBitMap()),
  LESS_THAN(Type.NUMBER.getBitMap(), Type.NUMBER.getBitMap()),
  LESS_THAN_OR_EQUAL_TO(Type.NUMBER.getBitMap(), Type.NUMBER.getBitMap()),
  MATCH_REGEX(Type.STRING.getBitMap(), Type.STRING.getBitMap()),
  CONTAIN(Type.STRING.getBitMap(), Type.STRING.getBitMap()),
  NOT_CONTAIN(Type.STRING.getBitMap(), Type.STRING.getBitMap()),
  EXACT(Type.STRING.getBitMap(), Type.STRING.getBitMap()),
  NOT_EXACT(Type.STRING.getBitMap(), Type.STRING.getBitMap()),
  EMPTY(Type.STRING.getBitMap(), 0),
  NOT_EMPTY(Type.STRING.getBitMap(), 0),
  MEMBER_NUMBER_EQUAL_TO(Type.OBJECT.getBitMap() | Type.ARRAY.getBitMap(), Type.NUMBER.getBitMap()),
  MEMBER_NUMBER_GREATER_THAN(
      Type.OBJECT.getBitMap() | Type.ARRAY.getBitMap(), Type.NUMBER.getBitMap()),
  MEMBER_NUMBER_GREATER_THAN_OR_EQUAL_TO(
      Type.OBJECT.getBitMap() | Type.ARRAY.getBitMap(), Type.NUMBER.getBitMap()),
  MEMBER_NUMBER_LESS_THAN(
      Type.OBJECT.getBitMap() | Type.ARRAY.getBitMap(), Type.NUMBER.getBitMap()),
  MEMBER_NUMBER_LESS_THAN_OR_EQUAL_TO(
      Type.OBJECT.getBitMap() | Type.ARRAY.getBitMap(), Type.NUMBER.getBitMap()),
  CONTAIN_MEMBER_WITH_KEY(Type.OBJECT.getBitMap(), Type.STRING.getBitMap()),
  NOT_CONTAIN_MEMBER_WITH_KEY(Type.OBJECT.getBitMap(), Type.STRING.getBitMap()),
  IS(
      Type.BOOLEAN.getBitMap()
          | Type.NUMBER.getBitMap()
          | Type.STRING.getBitMap()
          | Type.NULL.getBitMap()
          | Type.OBJECT.getBitMap()
          | Type.ARRAY.getBitMap(),
      Type.STRING.getBitMap());

  public static final String DTO;

  static {
    ObjectNode output = ObjectMapperSingleton.getInstance().getNodeFactory().objectNode();
    ArrayNode data = ObjectMapperSingleton.getInstance().getNodeFactory().arrayNode();
    //
    ObjectNode item;
    ArrayNode logContentPathValueType;
    ArrayNode detailRange;
    for (Operator operator : Operator.values()) {
      item = ObjectMapperSingleton.getInstance().getNodeFactory().objectNode();
      item.put("operator", operator.name());
      logContentPathValueType = ObjectMapperSingleton.getInstance().getNodeFactory().arrayNode();
      for (Type t : operator.LogContentPathValueType()) {
        logContentPathValueType.add(t.name().toLowerCase());
      }
      item.set("logContentPathValueType", logContentPathValueType);
      if (operator.detailType() == Boolean.class) {
        item.put("detailType", "boolean");
      } else if (operator.detailType() == Double.class) {
        item.put("detailType", "number");
      } else if (operator.detailType() == String.class) {
        item.put("detailType", "string");
      }
      if (operator.equals(Operator.IS)) {
        detailRange = ObjectMapperSingleton.getInstance().getNodeFactory().arrayNode();
        for (Type t : Type.values()) {
          detailRange.add(t.name());
        }
        item.set("detailRange", detailRange);
      }
      data.add(item);
    }
    output.set("data", data);
    DTO = output.toString();
  }

  private final int bitMapOperand1; // applicable type(s) of content path value
  private final int bitMapOperand2; // requirement of detail type

  Operator(int bitMapOperand1, int bitMapOperand2) {
    this.bitMapOperand1 = bitMapOperand1;
    this.bitMapOperand2 = bitMapOperand2;
  }

  public boolean suit(Type type) {
    return (this.bitMapOperand1 & type.getBitMap()) != 0;
  }

  public List<Type> LogContentPathValueType() {
    List<Type> result = new ArrayList<>();
    if ((bitMapOperand1 & Type.BOOLEAN.getBitMap()) != 0) {
      result.add(Type.BOOLEAN);
    }
    if ((bitMapOperand1 & Type.NUMBER.getBitMap()) != 0) {
      result.add(Type.NUMBER);
    }
    if ((bitMapOperand1 & Type.STRING.getBitMap()) != 0) {
      result.add(Type.STRING);
    }
    if ((bitMapOperand1 & Type.NULL.getBitMap()) != 0) {
      result.add(Type.NULL);
    }
    if ((bitMapOperand1 & Type.OBJECT.getBitMap()) != 0) {
      result.add(Type.OBJECT);
    }
    if ((bitMapOperand1 & Type.ARRAY.getBitMap()) != 0) {
      result.add(Type.ARRAY);
    }
    if (result.size() <= 0) {
      throw new RuntimeException(
          String.format("`Operator.%s` should suit an enum of `Type`.", this.name()));
    }
    return result;
  }

  @SuppressWarnings("rawtypes")
  public Class detailType() {
    if ((bitMapOperand2 & Type.BOOLEAN.getBitMap()) != 0) {
      return Boolean.class;
    } else if ((bitMapOperand2 & Type.NUMBER.getBitMap()) != 0) {
      return Double.class;
    } else if ((bitMapOperand2 & Type.STRING.getBitMap()) != 0) {
      return String.class;
    } else {
      return null;
    }
  }
}
