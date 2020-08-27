package io.github.messagehelper.core.mysql;

public class Constant {
  // general
  public static final int INSTANCE_LENGTH = 64;
  public static final int CATEGORY_LENGTH = 256;
  // config
  public static final int CONFIG_KEY_LENGTH = 256;
  public static final int CONFIG_VALUE_LENGTH = 1024;
  // connector
  public static final int CONNECTOR_URL_LENGTH = 2048;
  public static final int CONNECTOR_RPC_TOKEN_LENGTH = 64;
  // log
  public static final int LOG_LEVEL_LENGTH = 4;
  public static final String LOG_LEVEL_ERR = "ERR";
  public static final String LOG_LEVEL_WARN = "WARN";
  public static final String LOG_LEVEL_INFO = "INFO";
  public static final String LOG_LEVEL_VERB = "VERB";
  public static final String LOG_LEVEL_SILL = "SILL";

  public static final int LOG_CONTENT_LENGTH = 8192;
  // rule
  public static final int RULE_NAME_LENGTH = 256;
  public static final int RULE_IF_LENGTH = 2048;
  public static final int RULE_THEN_METHOD_LENGTH = 4;
  public static final String RULE_THEN_METHOD_GET = "GET";
  public static final String RULE_THEN_METHOD_POST = "POST";
  public static final int RULE_THEN_PATH_LENGTH = 64;
  public static final int RULE_BODY_TEMPLATE_LENGTH = 4096;
  // token
  public static final int TOKEN_LENGTH = 32;
}
