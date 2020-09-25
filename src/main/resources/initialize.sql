-- [config]
CREATE TABLE IF NOT EXISTS `config`
(
    `item_key`   varchar(256)  NOT NULL,
    `item_value` varchar(1024) NOT NULL,
    PRIMARY KEY (`item_key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

-- [config] API document
--
-- the URL of API document
INSERT IGNORE INTO config (item_key, item_value)
VALUES ('core.api-document',
        'https://editor.swagger.io/?url=https%3a%2f%2fraw.githubusercontent.com%2fmessage-helper%2fcore%2fmaster%2fdoc%2fapi.yaml');

-- [config] frontend config
--
-- JSON string
INSERT IGNORE INTO config (item_key, item_value)
VALUES ('core.frontend-config', '{}');

-- [config] ID generator
--
-- requirement:
--
-- - Requests of `GET` method without any authorization should be accepted.
-- - Responses' headers should be `application/json:charset;charset=UTF-8`.
-- - Responses' body should be a valid JSON string like `{"id":3075681472512}`.
INSERT IGNORE INTO config (item_key, item_value)
VALUES ('core.id-generator', 'https://id.zzc.icu/');

-- [config] instance
--
-- instance name shown in log
INSERT IGNORE INTO config (item_key, item_value)
VALUES ('core.instance', 'core');

-- [config] RPC token
--
-- token for any requests under path `/rpc`
INSERT IGNORE INTO config (item_key, item_value)
VALUES ('core.rpc-token', 'core8r3ufurm9tqomosuul0s5s9ts6ko8g85pijxudbvpm2jtb2w01od1z69h5vi');

-- [connector]
CREATE TABLE IF NOT EXISTS `connector`
(
    `id`        bigint(20)    NOT NULL,
    `instance`  varchar(64)   NOT NULL,
    `category`  varchar(256)  NOT NULL,
    `url`       varchar(1024) NOT NULL,
    `rpc_token` varchar(64)   NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_ca0pclol2sc0l7fvh7txq94r7` (`instance`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- [log]
CREATE TABLE IF NOT EXISTS `log`
(
    `id`           bigint(20)     NOT NULL,
    `instance`     varchar(64)    NOT NULL,
    `category`     varchar(256)   NOT NULL,
    `level`        char(4)        NOT NULL,
    `timestamp_ms` bigint(20)     NOT NULL,
    `content`      varchar(16000) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

-- [rule]
CREATE TABLE IF NOT EXISTS `rule`
(
    `id`                     bigint(20)    NOT NULL,
    `name`                   varchar(256)  NOT NULL,
    `if_log_instance_equal`  varchar(64)   NOT NULL,
    `if_log_category_equal`  varchar(256)  NOT NULL,
    `if_log_content_satisfy` varchar(1024) NOT NULL,
    `then_use_connector_id`  bigint(20)    NOT NULL,
    `then_use_http_method`   varchar(64)   NOT NULL,
    `then_use_url_path`      varchar(1024) NOT NULL,
    `then_use_body_template` varchar(4096) NOT NULL,
    `priority`               int(11)       NOT NULL,
    `terminate`              bit(1)        NOT NULL,
    `enable`                 bit(1)        NOT NULL,
    `annotation`             varchar(1024) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_g0aibm7vybna15mqfxis5nnf1` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;


-- [token]
CREATE TABLE IF NOT EXISTS `token`
(
    `token`                char(32)   NOT NULL,
    `expired_timestamp_ms` bigint(20) NOT NULL,
    PRIMARY KEY (`token`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;
