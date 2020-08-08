# Docker

### Build

``` sh
git clean -xfd
docker build -t zzcgwu/github_message-helper_core .
```

### Run

(Example)

#### Script

``` sh
docker run -d --restart on-failure --name core -v ./config/:/home/app/config/ -p 8003:8003 zzcgwu/github_message-helper_core
```

#### Compose

``` yaml
version: '3.3'
services:
  core:
    restart: on-failure
    container_name: core
    volumes:
      - './config/:/home/app/config/'
    ports:
      - '8003:8003'
    image: zzcgwu/github_message-helper_core
```

``` sh
docker-compose up -d
```

