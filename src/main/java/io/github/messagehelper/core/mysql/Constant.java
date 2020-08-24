package io.github.messagehelper.core.mysql;

public class Constant {
  // config
  public static final int CONFIG_KEY_LENGTH = 256;
  public static final int CONFIG_VALUE_LENGTH = 1024;
  // connector
  public static final int CONNECTOR_LENGTH = 64;
  public static final int CONNECTOR_URL_LENGTH = 2048;
  // log
  public static final int LOG_INSTANCE_LENGTH = 64;
  public static final int LOG_LEVEL_LENGTH = 4;
  public static final String LOG_LEVEL_ERR = "ERR";
  public static final String LOG_LEVEL_WARN = "WARN";
  public static final String LOG_LEVEL_INFO = "INFO";
  public static final String LOG_LEVEL_VERB = "VERB";
  public static final String LOG_LEVEL_SILL = "SILL";
  public static final int LOG_CATEGORY_LENGTH = 256;
  public static final int LOG_CONTENT_LENGTH = 8192;
  // rule
  public static final int RULE_NAME_LENGTH = 256;
  public static final int RULE_CONTENT_LENGTH = 2048;
  // token
  public static final int TOKEN_LENGTH = 32;
}
