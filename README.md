# core

### Prerequisite

- A database of **MySQL 5.7+** is required.
- Initialize database.
  - Review file [example.initialize.sql](./prerequisite/example.initialize.sql).
  - Customize and replace content between `<...>`.
  - Connect to the database and execute it.
- Create application properties.
  - Review file [example.application.properties](./prerequisite/example.application.properties).
  - Customize and replace content between  `<...>`.
  - Place the file under the path `./target/config/`.

### Run

``` bash
git clean -xfd

./mvnw clean && ./mvnw package # macOS and Linux only
.\mvnw.cmd clean && .\mvnw.cmd package # Windows only

java -jar ./target/id.jar
```

### Docker

See [Docker.md](./Docker.md) for details.

### Develop

Execute following commands.

``` bash
git config --local core.autocrlf input
git config --local core.safecrlf true
```

### Others

- All code files are edited by [IntelliJ IDEA](https://www.jetbrains.com/idea/).
- All ".md" files are edited by [Typora](http://typora.io/).
- The style of all ".md" files is [Github Flavored Markdown](https://guides.github.com/features/mastering-markdown/#GitHub-flavored-markdown).
- There is a LF (Linux) at the end of each line.