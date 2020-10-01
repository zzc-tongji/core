package io.github.messagehelper.core.processor.log.content;

public enum Type {
  BOOLEAN(1),
  NUMBER(1 << 1),
  STRING(1 << 2),
  NULL(1 << 3),
  OBJECT(1 << 4),
  ARRAY(1 << 5);

  public static final String ENUM_NAME_COLLECTION;

  static {
    StringBuilder builder = new StringBuilder();
    builder.append("{");
    for (Type t : Type.values()) {
      builder.append("\"");
      builder.append(t);
      builder.append("\", ");
    }
    builder.append("}");
    ENUM_NAME_COLLECTION = builder.toString();
  }

  private final int bitMap;

  Type(int bitMap) {
    this.bitMap = bitMap;
  }

  public int getBitMap() {
    return bitMap;
  }
}
