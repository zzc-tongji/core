# core

### Deploy and Run

#### Java

``` sh
git clean -xfd

./mvnw clean && ./mvnw package # macOS and Linux only
.\mvnw.cmd clean && .\mvnw.cmd package # Windows only

mkdir ./config/
mkdir ./data/

java -jar ./target/core.jar

#         listening port => tcp/8003
# optional configuration => ./config/application.properties
#       default database => ./data/database.sqlite3

# The newly generated config `core.rpc-token` will be shown in console log 
# with context `ATTENTION: core.rpc-token ==`.
```

#### Docker

```sh
docker pull r2d2project/core

docker run --detach --name core --publish 8003:8003 --restart always --volume ./mount/config/:/home/app/config/ --volume ./mount/data/:/home/app/data/ r2d2project/core

#         listening port => tcp/8003
# optional configuration => ./mount/config/application.properties
#       default database => ./mount/data/database.sqlite3

# The newly generated config `core.rpc-token` will be shown in docker log 
# with context `ATTENTION: core.rpc-token ==`.
```

See [Docker.md](./Docker.md) for more details.

### Database

#### SQLite 3

SQLite 3 is used to persist data by default. Data are is stored in file `<work-directory>/data/database.sqlite3`.

If the database file does not exist, it will be created by the default DDL. For safety reason, A random string will be assigned to config `core.rpc-token` at the first-run, which will be shown in log with context `ATTENTION: core.rpc-token ==`.

It is easy to quickly deploy the program and migrate the data by using such file-based database. However, there are still some disadvantages.

- The program has to write logs one by one (synchronized) which causes response delay increasing. The reason is - SQLite 3 will locked the whole database when there exists a connection of writing.
- Emoji characters cannot be correctly stored since SQLite 3 does not support charset `utf8mb4`.

Using MySQL 5.7 or MariaDB 10.2 instead is a better choice. (Other versions are not fully tested.)

#### MySQL 5.7

Create file `<work-directory>/config/application.properties` and fill out the following content.

``` properties
spring.jpa.database-platform=io.github.r2d2project.core.persistence.dialect.MySQL57Dialect
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://<ip-or-domain>:<port>/<database-name>
spring.datasource.username=<username>
spring.datasource.password=<password>
```

Replace all contents between `<>` with your own value. **DO NOT MODIFY OTHERS.**

Restart the program. It should run normally if every things is OK.

If the database is empty, the program will create tables and items by the default DDL. For safety reason, A random string will be assigned to config `core.rpc-token` at the first-run, which will be shown in log with context `ATTENTION: core.rpc-token ==`.

#### MariaDB 10.2

``` properties
spring.jpa.database-platform=io.github.r2d2project.core.persistence.dialect.MariaDB102Dialect
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.datasource.url=jdbc:mariadb://<ip-or-domain>:<port>/<database-name>
spring.datasource.username=<username>
spring.datasource.password=<password>
```

#### Default DDL

``` sqlite
-- config

create table config
(
    item_key   varchar(256)  not null primary key,
    item_value varchar(1024) not null
);

INSERT INTO config (item_key, item_value)
VALUES ('core.api-document',
        'https://editor.swagger.io/?url=https%3a%2f%2fraw.githubusercontent.com%2fr2-d2-project%2fcore%2fmaster%2fdocument%2fapi.yaml');

INSERT INTO config (item_key, item_value)
VALUES ('core.frontend-config', '{}');

INSERT INTO config (item_key, item_value)
VALUES ('core.id-generator', 'https://id.zzc.icu/');

INSERT INTO config (item_key, item_value)
VALUES ('core.instance', 'core');

INSERT INTO config (item_key, item_value)
VALUES ('core.rpc-token', 'RANDOMLY_GENERATED_STRING');

-- connector

create table connector
(
    id        bigint        not null primary key,
    category  varchar(256)  not null,
    instance  varchar(64)   not null unique,
    rpc_token varchar(64)   not null,
    url       varchar(1024) not null
);

INSERT INTO connector (id, category, instance, rpc_token, url)
VALUES (0, 'webhook-connector', 'core', '', '');

-- rule

create table rule
(
    id                           bigint        not null primary key,
    annotation                   varchar(1024)  not null,
    enable                       boolean       not null,
    if_log_category_equal        varchar(256)  not null,
    if_log_content_satisfy       varchar(4096) not null,
    if_log_instance_equal        varchar(64)  not null,
    name                         varchar(256)  not null unique,
    priority                     integer       not null,
    terminate                    boolean       not null,
    then_use_body_json           boolean       not null,
    then_use_body_template       varchar(4096) not null,
    then_use_connector_id        bigint        not null,
    then_use_header_content_type varchar(64)   not null,
    then_use_url_path            varchar(1024) not null
);

-- token

create table token
(
    token                char(64) not null primary key,
    expired_timestamp_ms bigint   not null
);

-- log

create table log
(
    id           bigint         not null primary key,
    category     varchar(256)   not null,
    content      varchar(16000) not null,
    instance     varchar(64)    not null,
    level        char(4)        not null,
    timestamp_ms bigint         not null
);
```

#### Preview

``` sql
select count(id) FROM log;
select * from log where level = "INFO" OR level = "WARN" OR level = "ERR" order by timestamp_ms desc limit 100;
select * from config;
select * from connector;
select * from rule order by priority asc;
select * from token;
```

### Develop

#### Git

Execute following commands before making any change.

``` sh
git config --local core.autocrlf false
git config --local core.safecrlf true
git config --local core.eol lf
```

#### application.properties

Add the following content to `<work-directory>/config/application.properties`.

``` properties
setting.debug=true
# Set as `true` to show all exceptions in console.
# Set as `false` to show just `RuntimeException` in console.

setting.global-cors=true
# Set as `true` to enable CORS for all APIs.
# Set as `false` to enable CORS for only `/rpc/log` and `/rpc/status`.

logging.level.org.hibernate.SQL=DEBUG
# Show SQL operations.

logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
# Show parameters of SQL operations.
```
