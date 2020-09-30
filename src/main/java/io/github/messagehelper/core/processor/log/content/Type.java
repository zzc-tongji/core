package io.github.messagehelper.core.processor.log.content;

public enum Type {
  BOOLEAN(1 << Short.SIZE),
  NUMBER(1 << Short.SIZE + 1),
  STRING(1 << Short.SIZE + 2),
  NULL(1 << Short.SIZE + 3),
  OBJECT(1 << Short.SIZE + 4),
  ARRAY(1 << Short.SIZE + 5),
  ALL(BOOLEAN.value | NUMBER.value | STRING.value | NULL.value | OBJECT.value | ARRAY.value);

  private final int value;

  Type(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
