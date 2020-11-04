package io.github.r2d2project.core.configuration;

public class MySQL57Dialect extends org.hibernate.dialect.MySQL57Dialect {
  @Override
  public String getTableTypeString() {
    return "ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci";
  }
}
