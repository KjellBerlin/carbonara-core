# Core application

### Build docker image
Do not forget to use right tag
```bash
docker build -t netflixnetflix/carbonara-core:0.2 .
```

### Push docker image
```bash
docker push netflixnetflix/carbonara-core:0.2
```

### Running on docker
Profile prod is always being used, to connect to db running in docker
```bash
docker run -d -it -p 8080:8080 --name carbonara-core -e "SPRING_PROFILES_ACTIVE=prod" --add-host host.docker.internal:host-gateway netflixnetflix/carbonara-core:0.2
```
