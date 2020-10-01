package io.github.messagehelper.core.processor.rule._if;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.messagehelper.core.processor.log.content.Type;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

import java.util.ArrayList;
import java.util.List;

public enum Operator {
  TRUE(Type.BOOLEAN.getBitMap()),
  FALSE(Type.BOOLEAN.getBitMap()),
  EQUAL_TO(Type.NUMBER.getBitMap() | Type.NUMBER.getBitMap() << Byte.SIZE),
  GREATER_THAN(Type.NUMBER.getBitMap() | Type.NUMBER.getBitMap() << Byte.SIZE),
  GREATER_THAN_OR_EQUAL_TO(Type.NUMBER.getBitMap() | Type.NUMBER.getBitMap() << Byte.SIZE),
  LESS_THAN(Type.NUMBER.getBitMap() | Type.NUMBER.getBitMap() << Byte.SIZE),
  LESS_THAN_OR_EQUAL_TO(Type.NUMBER.getBitMap() | Type.NUMBER.getBitMap() << Byte.SIZE),
  MATCH_REGEX(Type.STRING.getBitMap() | Type.STRING.getBitMap() << Byte.SIZE),
  CONTAIN(Type.STRING.getBitMap() | Type.STRING.getBitMap() << Byte.SIZE),
  NOT_CONTAIN(Type.STRING.getBitMap() | Type.STRING.getBitMap() << Byte.SIZE),
  EMPTY(Type.STRING.getBitMap()),
  NOT_EMPTY(Type.STRING.getBitMap()),
  ELEMENT_NUMBER_EQUAL_TO(
      Type.OBJECT.getBitMap() | Type.ARRAY.getBitMap() | Type.NUMBER.getBitMap() << Byte.SIZE),
  ELEMENT_NUMBER_GREATER_THAN(
      Type.OBJECT.getBitMap() | Type.ARRAY.getBitMap() | Type.NUMBER.getBitMap() << Byte.SIZE),
  ELEMENT_NUMBER_GREATER_THAN_OR_EQUAL_TO(
      Type.OBJECT.getBitMap() | Type.ARRAY.getBitMap() | Type.NUMBER.getBitMap() << Byte.SIZE),
  ELEMENT_NUMBER_LESS_THAN(
      Type.OBJECT.getBitMap() | Type.ARRAY.getBitMap() | Type.NUMBER.getBitMap() << Byte.SIZE),
  ELEMENT_NUMBER_LESS_THAN_OR_EQUAL_TO(
      Type.OBJECT.getBitMap() | Type.ARRAY.getBitMap() | Type.NUMBER.getBitMap() << Byte.SIZE),
  IS(
      Type.BOOLEAN.getBitMap()
          | Type.NUMBER.getBitMap()
          | Type.STRING.getBitMap()
          | Type.NULL.getBitMap()
          | Type.OBJECT.getBitMap()
          | Type.ARRAY.getBitMap()
          | Type.STRING.getBitMap() << Byte.SIZE);

  public static final String DTO;

  static {
    ObjectNode output = ObjectMapperSingleton.getInstance().getNodeFactory().objectNode();
    ArrayNode data = ObjectMapperSingleton.getInstance().getNodeFactory().arrayNode();
    //
    ObjectNode item;
    List<Type> typeList;
    StringBuilder builder;
    ArrayNode rangeOfDetail;
    for (Operator operator : Operator.values()) {
      item = ObjectMapperSingleton.getInstance().getNodeFactory().objectNode();
      item.put("operator", operator.name());
      typeList = operator.LogContentPathValueType();
      builder = new StringBuilder();
      for (int i = 0; i < typeList.size(); i++) {
        builder.append(typeList.get(i).name().toLowerCase());
        if (i != typeList.size() - 1) {
          builder.append(" | ");
        }
      }
      item.put("logContentPathValueType", builder.toString());
      if (operator.detailType() == Boolean.class) {
        item.put("detailType", "boolean");
      } else if (operator.detailType() == Double.class) {
        item.put("detailType", "number");
      } else if (operator.detailType() == String.class) {
        item.put("detailType", "string");
      } else { // operator.detailType() == null
        item.set("detailType", NullNode.getInstance());
      }
      if (operator.equals(Operator.IS)) {
        rangeOfDetail = ObjectMapperSingleton.getInstance().getNodeFactory().arrayNode();
        for (Type t : Type.values()) {
          rangeOfDetail.add(t.name());
        }
        item.set("detailRange", rangeOfDetail);
      }
      data.add(item);
    }
    output.set("data", data);
    DTO = output.toString();
  }

  // 1st byte (lowest) : applicable type(s) of content path value
  // 2nd byte          : requirement of detail type
  private final int operandDescription;

  Operator(int operandDescription) {
    this.operandDescription = operandDescription;
  }

  public boolean suit(Type type) {
    return (this.operandDescription & type.getBitMap()) != 0;
  }

  public List<Type> LogContentPathValueType() {
    List<Type> result = new ArrayList<>();
    if ((operandDescription & Type.BOOLEAN.getBitMap()) != 0) {
      result.add(Type.BOOLEAN);
    }
    if ((operandDescription & Type.NUMBER.getBitMap()) != 0) {
      result.add(Type.NUMBER);
    }
    if ((operandDescription & Type.STRING.getBitMap()) != 0) {
      result.add(Type.STRING);
    }
    if ((operandDescription & Type.NULL.getBitMap()) != 0) {
      result.add(Type.NULL);
    }
    if ((operandDescription & Type.OBJECT.getBitMap()) != 0) {
      result.add(Type.OBJECT);
    }
    if ((operandDescription & Type.ARRAY.getBitMap()) != 0) {
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
    if ((operandDescription >>> Byte.SIZE & Type.BOOLEAN.getBitMap()) != 0) {
      return Boolean.class;
    } else if ((operandDescription >>> Byte.SIZE & Type.NUMBER.getBitMap()) != 0) {
      return Double.class;
    } else if ((operandDescription >>> Byte.SIZE & Type.STRING.getBitMap()) != 0) {
      return String.class;
    } else {
      return null;
    }
  }
}
