# onecx-ai-quarkus-svc
OneCx AI quarkus Service


build native image
```
mvn package -Pnative
```

using docker-compose to start containers

```
docker-compose up
```

stop containers optional with -volumes to clean data

```
docker-compose down -volumes
```


debugging
```
mvn clean install -Dmaven.surefire.debug
```