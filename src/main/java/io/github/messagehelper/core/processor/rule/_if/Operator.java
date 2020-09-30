package io.github.messagehelper.core.processor.rule._if;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.messagehelper.core.processor.log.content.Type;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

public enum Operator {
  // BOOLEAN
  TRUE(Type.BOOLEAN.getValue() | 1),
  FALSE(Type.BOOLEAN.getValue() | 2),
  // DOUBLE
  EQUAL_TO(Type.NUMBER.getValue() | 1 << Short.SIZE - 1 | 1),
  GREATER_THAN(Type.NUMBER.getValue() | 1 << Short.SIZE - 1 | 2),
  GREATER_THAN_OR_EQUAL_TO(Type.NUMBER.getValue() | 1 << Short.SIZE - 1 | 3),
  LESS_THAN(Type.NUMBER.getValue() | 1 << Short.SIZE - 1 | 4),
  LESS_THAN_OR_EQUAL_TO(Type.NUMBER.getValue() | 1 << Short.SIZE - 1 | 5),
  // STRING
  MATCH_REGEX(Type.STRING.getValue() | 1 << Short.SIZE - 1 | 1),
  CONTAIN(Type.STRING.getValue() | 1 << Short.SIZE - 1 | 2),
  NOT_CONTAIN(Type.STRING.getValue() | 1 << Short.SIZE - 1 | 3),
  EMPTY(Type.STRING.getValue() | 4),
  NOT_EMPTY(Type.STRING.getValue() | 5),
  // ALL
  IS(Type.ALL.getValue() | 1 << Short.SIZE - 1 | 1);

  public static final String DTO;

  private static final int NEED_DETAIL_MASK = 32768; // 0b 00000000 00000000 10000000 00000000

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
        item.put("suit", "boolean | number | string | null | object | array");
        item.put("typeOfDetail", "string");
        rangeOfDetail = ObjectMapperSingleton.getInstance().getNodeFactory().arrayNode();
        for (Type t : Type.values()) {
          if (t.equals(Type.ALL)) {
            continue;
          }
          rangeOfDetail.add(t.name());
        }
        item.set("rangeOfDetail", rangeOfDetail);
      } else if (operator.suit(Type.BOOLEAN)) {
        item.put("suit", "boolean");
        item.set("typeOfDetail", NullNode.getInstance());
      } else if (operator.suit(Type.NUMBER)) {
        item.put("suit", "number");
        item.put("typeOfDetail", "number");
      } else if (operator.suit(Type.STRING)) {
        item.put("suit", "string");
        if (operator.needDetail()) {
          item.put("typeOfDetail", "string");
        } else {
          item.set("typeOfDetail", NullNode.getInstance());
        }
      } else { // Type.NULL, Type.OBJECT, Type.ARRAY
        item.put("suit", "null | object | array");
        item.set("typeOfDetail", NullNode.getInstance());
      }
      data.add(item);
    }
    output.set("data", data);
    DTO = output.toString();
  }

  private final int value;

  public int getValue() {
    return value;
  }

  Operator(int value) {
    this.value = value;
  }

  public boolean suit(Type type) {
    return (this.value & type.getValue()) != 0;
  }

  public boolean needDetail() {
    return (this.value & NEED_DETAIL_MASK) != 0;
  }
}
