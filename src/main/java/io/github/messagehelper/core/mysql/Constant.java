package io.github.messagehelper.core.mysql;

public class Constant {
  // general
  public static final int INSTANCE_LENGTH = 64;
  public static final int CATEGORY_LENGTH = 256;
  // config
  public static final int CONFIG_KEY_LENGTH = 256;
  public static final int CONFIG_VALUE_LENGTH = 1024;
  // connector
  public static final int CONNECTOR_URL_LENGTH = 1024;
  public static final int CONNECTOR_RPC_TOKEN_LENGTH = 64;
  public static final long CONNECTOR_ID_VIRTUAL = 0L;
  // log
  public static final String LOG_COLUMN_NAME_ID = "id";
  public static final String LOG_COLUMN_NAME_INSTANCE = "instance";
  public static final String LOG_COLUMN_NAME_LEVEL = "level";
  public static final String LOG_COLUMN_NAME_CATEGORY = "category";
  public static final String LOG_COLUMN_NAME_TIMESTAMP_MS = "timestamp_ms";
  public static final String LOG_COLUMN_NAME_CONTENT = "content";
  public static final int LOG_LEVEL_LENGTH = 4;
  public static final String LOG_LEVEL_ERR = "ERR";
  public static final String LOG_LEVEL_WARN = "WARN";
  public static final String LOG_LEVEL_INFO = "INFO";
  public static final String LOG_LEVEL_VERB = "VERB";
  public static final String LOG_LEVEL_SILL = "SILL";
  public static final int LOG_CONTENT_LENGTH = 16000;
  // rule
  public static final int RULE_NAME_LENGTH = 256;
  public static final int RULE_IF_LOG_CONTENT_SATISFY_LENGTH = 4096;
  public static final int RULE_THEN_USE_HEADER_CONTENT_TYPE_LENGTH = 64;
  public static final int RULE_THEN_USE_URL_PATH_LENGTH = 1024;
  public static final int RULE_THEN_USE_BODY_TEMPLATE_LENGTH = 4096;
  public static final int RULE_ANNOTATION_LENGTH = 1024;
  // token
  public static final int TOKEN_LENGTH = 64;
}
