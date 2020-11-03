package io.github.r2d2project.core.processor.log;

import io.github.r2d2project.core.dto.rpc.log.PostRequestDto;
import io.github.r2d2project.core.processor.log.content.Unit;

import java.util.Map;

public class Log {
  public static Log parse(PostRequestDto dto) {
    Log log = new Log();
    log.id = dto.getId();
    log.instance = dto.getInstance();
    log.category = dto.getCategory();
    log.level = dto.getLevel();
    log.timestampMs = dto.getTimestampMs();
    log.content = Unit.toMap(dto.getContent());
    return log;
  }

  private Long id;
  private String instance;
  private String level;
  private String category;
  private Long timestampMs;
  private Map<String, Unit> content;

  public Long getId() {
    return id;
  }

  public String getInstance() {
    return instance;
  }

  public String getLevel() {
    return level;
  }

  public String getCategory() {
    return category;
  }

  public Long getTimestampMs() {
    return timestampMs;
  }

  public Map<String, Unit> getContent() {
    return content;
  }

  private Log() {}
}
