package io.github.messagehelper.core.processor.log.content;

public enum Type {
  BOOLEAN(1 << Short.SIZE),
  DOUBLE(1 << Short.SIZE + 1),
  STRING(1 << Short.SIZE + 2),
  BOOLEAN_DOUBLE_STRING(BOOLEAN.value | DOUBLE.value | STRING.value),
  NULL(1 << Short.SIZE + 3),
  OBJECT(1 << Short.SIZE + 4),
  ARRAY(1 << Short.SIZE + 5),
  NULL_OBJECT_ARRAY(NULL.value | OBJECT.value | STRING.value);

  private int value;

  Type(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
