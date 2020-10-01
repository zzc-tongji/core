package io.github.messagehelper.core.processor.log.content;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.messagehelper.core.dto.rpc.log.PostRequestDto;
import io.github.messagehelper.core.exception.LogContentInvalidException;
import io.github.messagehelper.core.utils.ObjectMapperSingleton;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Unit {
  public static Map<String, Unit> toMap(String json) {
    JsonNode node;
    try {
      node = ObjectMapperSingleton.getInstance().readTree(json);
    } catch (JsonProcessingException e) {
      throw new LogContentInvalidException(PostRequestDto.EXCEPTION_MESSAGE_CONTENT);
    }
    StringBuilder builder = new StringBuilder();
    Map<String, Unit> result = new HashMap<>();
    helper(node, builder, result);
    return result;
  }

  private static void helper(JsonNode node, StringBuilder path, Map<String, Unit> result) {
    if (node.isBoolean()) {
      result.put(path.toString(), new Unit(path.toString(), Type.BOOLEAN, node.asBoolean()));
      return;
    }
    if (node.isNumber()) {
      result.put(path.toString(), new Unit(path.toString(), Type.NUMBER, node.asDouble()));
      return;
    }
    if (node.isTextual()) {
      result.put(path.toString(), new Unit(path.toString(), Type.STRING, node.asText()));
      return;
    }
    if (node.isNull()) {
      result.put(path.toString(), new Unit(path.toString(), Type.NULL, NullNode.getInstance()));
      return;
    }
    int pathLength = path.length();
    if (node.isObject()) {
      result.put(path.toString(), new Unit(path.toString(), Type.OBJECT, node));
      //
      Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
      //
      Map.Entry<String, JsonNode> current;
      String key;
      while (iterator.hasNext()) {
        current = iterator.next();
        key = current.getKey();
        // ignore
        if (key.contains(".") || key.contains("[") || key.contains("]")) {
          continue;
        }
        // append
        path.append(".");
        path.append(key);
        // recursive
        helper(current.getValue(), path, result);
        // back tracking
        path.delete(pathLength, path.length());
      }
      return;
    }
    if (node.isArray()) {
      result.put(path.toString(), new Unit(path.toString(), Type.ARRAY, node));
      //
      Iterator<JsonNode> iterator = node.elements();
      //
      int index = 0;
      while (iterator.hasNext()) {
        // append
        path.append(".[");
        path.append(index);
        path.append("]");
        // recursive
        helper(iterator.next(), path, result);
        // back tracking
        path.delete(pathLength, path.length());
        // index
        index += 1;
      }
    }
  }

  private final String path;
  private final Type type;
  private final Object value;

  public String getPath() {
    return path;
  }

  public Type getType() {
    return type;
  }

  public Unit(String path, Type type, Object value) {
    this.path = path;
    this.type = type;
    this.value = value;
  }

  @Override
  public int hashCode() {
    return path.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Unit)) {
      return false;
    }
    return obj.hashCode() == this.hashCode();
  }

  public Boolean valueAsBoolean() {
    if (!type.equals(Type.BOOLEAN)) {
      throw new RuntimeException(message(Type.BOOLEAN));
    }
    return (Boolean) value;
  }

  public Double valueAsDouble() {
    if (!type.equals(Type.NUMBER)) {
      throw new RuntimeException(message(Type.NUMBER));
    }
    return (Double) value;
  }

  public String valueAsString() {
    if (!type.equals(Type.STRING)) {
      throw new RuntimeException(message(Type.STRING));
    }
    return (String) value;
  }

  public JsonNode valueAsObjectOrArray() {
    if (!type.equals(Type.OBJECT) && !type.equals(Type.ARRAY)) {
      throw new RuntimeException(message(Type.OBJECT));
    }
    return (JsonNode) value;
  }

  public ObjectNode valueAsObject() {
    if (!type.equals(Type.OBJECT)) {
      throw new RuntimeException(message(Type.OBJECT));
    }
    return (ObjectNode) value;
  }

  public ArrayNode valueAsArray() {
    if (!type.equals(Type.ARRAY)) {
      throw new RuntimeException(message(Type.ARRAY));
    }
    return (ArrayNode) value;
  }

  public String valueToString() {
    return value.toString();
  }

  private String message(Type type) {
    switch (type) {
      case BOOLEAN:
        return "invalid type, use `valueAsBoolean` instead";
      case NUMBER:
        return "invalid type, use `valueAsDouble instead`";
      case STRING:
        return "invalid type. use `valueAsString instead`";
      default: // NULL, OBJECT, ARRAY
        return "invalid type, use `valueIsNull`, `valueIsObject` or `valueIsArray` instead";
    }
  }
}
