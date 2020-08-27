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
# url => jdbc:mysql://<ip-or-domain>:<port>/<database-name>
spring.datasource.url=
# username
spring.datasource.username=
# password
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

``` bash
git clean -xfd

./mvnw clean && ./mvnw package # macOS and Linux only
.\mvnw.cmd clean && .\mvnw.cmd package # Windows only

java -jar ./target/id.jar
```

### Docker

Execute following commands to pull and run:

```bash
docker pull messagehelper/core

docker run -d --restart on-failure --name core -v ./config/:/home/app/config/ -p 8003:8003 messagehelper/core
```

Path `/home/app/` in docker container acts as `<jar-location>`.

See [Docker.md](./Docker.md) for more details.

### Develop

Execute following commands before making any change.

``` bash
git config --local core.autocrlf input
git config --local core.safecrlf true
```

### Database Review

``` mysql
SELECT * from dev.config;
SELECT * from dev.connector;
SELECT * from dev.rule;
SELECT * from dev.token;
SELECT * from dev.log ORDER BY timestamp_ms DESC LIMIT 100;
```

### Others

- All code files are edited by [IntelliJ IDEA](https://www.jetbrains.com/idea/).
- All ".md" files are edited by [Typora](http://typora.io/).
- The style of all ".md" files is [Github Flavored Markdown](https://guides.github.com/features/mastering-markdown/#GitHub-flavored-markdown).
- There is a LF (Linux) at the end of each line.
