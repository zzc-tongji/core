-- [config]
CREATE TABLE `config`
(
    `item_key`   varchar(256)  NOT NULL,
    `item_value` varchar(1024) NOT NULL,
    PRIMARY KEY (`item_key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

-- [config] frontend config
--
-- JSON string
INSERT INTO dev.config (item_key, item_value)
VALUES ('core.frontend.config', '{}');

-- [config] ID generator
--
-- requirement:
--
-- - Requests of `GET` method without any authorization should be accepted.
-- - Responses' headers should be `application/json:charset;charset=UTF-8`.
-- - Responses' body should be a valid JSON string like `{"id":3075681472512}`.
INSERT INTO config (item_key, item_value)
-- VALUES ('core.id-generator', '<id-generator-url>');
VALUES ('core.id-generator', 'https://id.zzc.icu/');

-- [config] instance
--
-- instance name shown in log
INSERT INTO config (item_key, item_value)
-- VALUES ('core.instance', '<instance>');
VALUES ('core.id-generator', 'core');

-- [config] RPC token
--
-- token for any requests under path `/rpc`
INSERT INTO config (item_key, item_value)
-- VALUES ('core.rpc.token', '<rpc-token>');
VALUES ('core.rpc.token', 'core8r3ufurm9tqomosuul0s5s9ts6ko8g85pijxudbvpm2jtb2w01od1z69h5vi');

-- [connector]
CREATE TABLE `connector`
(
    `id`        bigint(20)    NOT NULL,
    `category`  varchar(64)   NOT NULL,
    `instance`  varchar(64)   NOT NULL,
    `rpc_token` varchar(64)   NOT NULL,
    `url`       varchar(2048) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_ca0pclol2sc0l7fvh7txq94r7` (`instance`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

-- [log]
CREATE TABLE `log`
(
    `id`           bigint(20)    NOT NULL,
    `category`     varchar(256)  NOT NULL,
    `content`      varchar(8192) NOT NULL,
    `instance`     varchar(64)   NOT NULL,
    `level`        char(4)       NOT NULL,
    `timestamp_ms` bigint(20)    NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

-- [rule]
CREATE TABLE `rule`
(
    `id`        bigint(20)    NOT NULL,
    `name`      varchar(256)  NOT NULL,
    `rule_if`   varchar(2048) NOT NULL,
    `rule_then` varchar(2048) NOT NULL,
    `priority`  int(11)       NOT NULL,
    `terminate` bit(1)        NOT NULL,
    `enable`    bit(1)        NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_g0aibm7vybna15mqfxis5nnf1` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

-- [token]
CREATE TABLE `token`
(
    `token`                char(32)   NOT NULL,
    `expired_timestamp_ms` bigint(20) NOT NULL,
    PRIMARY KEY (`token`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

-- [token] permanent API token for development
--
-- For daily usage, comment or remove the following statement for safety.
INSERT INTO dev.token (token, expired_timestamp_ms)
VALUES ('token-4271611034269670790', 0);
