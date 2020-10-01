package io.github.messagehelper.core.processor.rule._if;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.messagehelper.core.processor.log.content.Type;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

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
  IS(Type.STRING.getBitMap() << Byte.SIZE);

  public static final String DTO;

  static {
    ObjectNode output = ObjectMapperSingleton.getInstance().getNodeFactory().objectNode();
    ArrayNode data = ObjectMapperSingleton.getInstance().getNodeFactory().arrayNode();
    //
    ObjectNode item;
    ArrayNode rangeOfDetail;
    for (Operator operator : Operator.values()) {
      item = ObjectMapperSingleton.getInstance().getNodeFactory().objectNode();
      item.put("operator", operator.name());
      if (operator.equals(Operator.IS)) {
        item.put("logContentPathValueType", "boolean | number | string | null | object | array");
        item.put("detailType", "string");
        rangeOfDetail = ObjectMapperSingleton.getInstance().getNodeFactory().arrayNode();
        for (Type t : Type.values()) {
          rangeOfDetail.add(t.name());
        }
        item.set("detailRange", rangeOfDetail);
        data.add(item);
        continue;
      }
      item.put("logContentPathValueType", operator.LogContentPathValueType().name().toLowerCase());
      if (operator.detailType() == Boolean.class) {
        item.put("detailType", "boolean");
      } else if (operator.detailType() == Double.class) {
        item.put("detailType", "number");
      } else if (operator.detailType() == String.class) {
        item.put("detailType", "string");
      } else { // operator.detailType() == null
        item.set("detailType", NullNode.getInstance());
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

  public Type LogContentPathValueType() {
    if ((operandDescription & Type.BOOLEAN.getBitMap()) != 0) {
      return Type.BOOLEAN;
    } else if ((operandDescription & Type.NUMBER.getBitMap()) != 0) {
      return Type.NUMBER;
    } else if ((operandDescription & Type.STRING.getBitMap()) != 0) {
      return Type.STRING;
    } else if ((operandDescription & Type.NULL.getBitMap()) != 0) {
      return Type.NULL;
    } else if ((operandDescription & Type.OBJECT.getBitMap()) != 0) {
      return Type.OBJECT;
    } else if ((operandDescription & Type.ARRAY.getBitMap()) != 0) {
      return Type.ARRAY;
    } else {
      throw new RuntimeException(
          String.format("`Operator.%s` should suit an enum of `Type`.", this.name()));
    }
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
