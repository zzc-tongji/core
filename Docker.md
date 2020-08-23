# Docker

### Build

``` sh
git clean -xfd
docker build -t messagehelper/core .
```

### Run

(Example)

#### Script

``` sh
docker run -d --restart on-failure --name core -v ./config/:/home/app/config/ -p 8003:8003 messagehelper/core
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
    image: messagehelper/core
```

``` sh
docker-compose up -d
```

