# Docker

### Build

``` sh
git clean -xfd
docker build -t r2d2project/core .
```

### Run

(Example)

#### Script

``` sh
docker run --detach --name core --publish 8003:8003 --restart always --volume ./mount/config/:/home/app/config/ r2d2project/core
```

#### Compose

``` yaml
version: '3.3'
services:
  core:
    container_name: core
    image: r2d2project/core
    ports:
      - '8003:8003'
    restart: always
    volumes:
      - './mount/config/:/home/app/config/'
      - './mount/data/:/home/app/data/'
```

``` sh
docker-compose up --detach
```

