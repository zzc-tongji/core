package io.github.r2d2project.core.configuration;

public class MariaDB102Dialect extends org.hibernate.dialect.MariaDB102Dialect {
  @Override
  public String getTableTypeString() {
    return "ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci";
  }
}
