# core

### Prerequisite

- A database of **MySQL 5.7+** is required.

- Create application properties.
  - Place the following content in file `<jar-location>/config/application.properties`.
  - Fill out database connection parameters.
  - **Do not modify other lines.**
  

``` properties
server.port=8003
# BEGIN
#
# setting.debug
# => Set as `true` to show all exceptions in local log (console).
setting.debug=
# setting.global-cors
# => Set as `true` to enable CORS for all APIs.
setting.global-cors=
# spring.datasource.url
# => The format is `jdbc:mysql://<ip-or-domain>:<port>/<database-name>`.
spring.datasource.url=
# spring.datasource.username
spring.datasource.username=
# spring.datasource.password
spring.datasource.password=
#
# END
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.schema=classpath:initialize.sql
spring.datasource.initialization-mode=always
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hipernate.format_sql=true
spring.jpa.properties.usb_sq/_comments=true
```

### Run

``` sh
git clean -xfd

./mvnw clean && ./mvnw package # macOS and Linux only
.\mvnw.cmd clean && .\mvnw.cmd package # Windows only

java -jar ./target/id.jar
```

### Docker

Execute following commands to pull and run:

```sh
docker pull messagehelper/core

docker run --detach --name core --publish 8003:8003 --restart always --volume ./mount/config/:/home/app/config/ messagehelper/core
```

Path `/home/app/` in docker container acts as `<jar-location>`.

See [Docker.md](./Docker.md) for more details.

### Develop

Execute following commands before making any change.

``` sh
git config --local core.autocrlf input
git config --local core.safecrlf true
```

### Data Review

``` mysql
SELECT count(id) FROM log;
SELECT * FROM log WHERE level = "INFO" OR level = "WARN" OR level = "ERR" ORDER BY timestamp_ms DESC LIMIT 100;
SELECT * FROM config;
SELECT * FROM connector;
SELECT * FROM rule ORDER BY priority ASC;
SELECT * FROM token;
```

### Others

- All code files are edited by [IntelliJ IDEA](https://www.jetbrains.com/idea/).
- All ".md" files are edited by [Typora](http://typora.io/).
- The style of all ".md" files is [Github Flavored Markdown](https://guides.github.com/features/mastering-markdown/#GitHub-flavored-markdown).
- There is a LF (Linux) at the end of each line.
