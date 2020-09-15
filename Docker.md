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
docker run --detach --name core --publish 8003:8003 --restart always --volume ./mount/config/:/home/app/config/ messagehelper/core
```

#### Compose

``` yaml
version: '3.3'
services:
  core:
    container_name: core
    image: messagehelper/core
    ports:
      - '8003:8003'
    restart: always
    volumes:
      - './mount/config/:/home/app/config/'
```

``` sh
docker-compose up --detach
```

