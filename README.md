# core

### Prerequisite

- A database of **MySQL 5.7+** is required.
- Initialize database.
  - Review file [template.initialize.sql](./prerequisite/template.initialize.sql).
  - Customize and replace content between `<...>`, then adjust comments to enable them.
  - Connect to the database and execute.
- Create application properties.
  - Review file [template.application.properties](./prerequisite/template.application.properties).
  - Customize and replace content between  `<...>`.
  - Rename and place it at path `<jar-location>/config/application.properties`.

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

Use file [develop.initialize.sql](./prerequisite/develop.initialize.sql) to initialize database.

### Others

- All code files are edited by [IntelliJ IDEA](https://www.jetbrains.com/idea/).
- All ".md" files are edited by [Typora](http://typora.io/).
- The style of all ".md" files is [Github Flavored Markdown](https://guides.github.com/features/mastering-markdown/#GitHub-flavored-markdown).
- There is a LF (Linux) at the end of each line.
