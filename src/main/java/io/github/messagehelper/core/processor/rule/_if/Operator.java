package io.github.messagehelper.core.processor.rule._if;

import io.github.messagehelper.core.processor.log.content.Type;

public enum Operator {
  // BOOLEAN
  TRUE(Type.BOOLEAN.getValue() | 1),
  FALSE(Type.BOOLEAN.getValue() | 2),
  // DOUBLE
  EQUAL_TO(Type.DOUBLE.getValue() | 1 << Short.SIZE - 1 | 1),
  GREATER_THAN(Type.DOUBLE.getValue() | 1 << Short.SIZE - 1 | 2),
  GREATER_THAN_OR_EQUAL_TO(Type.DOUBLE.getValue() | 1 << Short.SIZE - 1 | 3),
  LESS_THAN(Type.DOUBLE.getValue() | 1 << Short.SIZE - 1 | 4),
  LESS_THAN_OR_EQUAL_TO(Type.DOUBLE.getValue() | 1 << Short.SIZE - 1 | 5),
  // STRING
  MATCH_REGEX(Type.STRING.getValue() | 1 << Short.SIZE - 1 | 1),
  CONTAIN(Type.STRING.getValue() | 1 << Short.SIZE - 1 | 2),
  NOT_CONTAIN(Type.STRING.getValue() | 1 << Short.SIZE - 1 | 3),
  EMPTY(Type.STRING.getValue() | 4),
  NOT_EMPTY(Type.STRING.getValue() | 5),
  // NULL, OBJECT, ARRAY
  NULL(Type.NULL_OBJECT_ARRAY.getValue() | 1),
  NOT_NULL(Type.NULL_OBJECT_ARRAY.getValue() | 2),
  OBJECT(Type.NULL_OBJECT_ARRAY.getValue() | 3),
  NOT_OBJECT(Type.NULL_OBJECT_ARRAY.getValue() | 4),
  ARRAY(Type.NULL_OBJECT_ARRAY.getValue() | 5),
  NOT_ARRAY(Type.NULL_OBJECT_ARRAY.getValue() | 6);

  private static final int NEED_DETAIL_MASK = 32768; // 0b 00000000 00000000 10000000 00000000

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
