# Core application
## Build and Push
### Build docker image
Do not forget to use right tag
```bash
docker build -t netflixnetflix/carbonara-core:0.6 .
```

### Push docker image do google cloud artifact registry
Update tag
```bash
docker tag netflixnetflix/carbonara-core:0.6 europe-west3-docker.pkg.dev/carbonara-395019/carbonara-repo/carbonara-core:0.6
```
```bash
docker push europe-west3-docker.pkg.dev/carbonara-395019/carbonara-repo/carbonara-core:0.6
```

## Running locally
### Running on docker locally
- Profile docker-dev is being used, to connect to db running in docker
- Use the right tag
```bash
docker compose up
```
```bash
docker run -d -it -p 8080:8080 --name carbonara-core -e "SPRING_PROFILES_ACTIVE=staging" --add-host host.docker.internal:host-gateway netflixnetflix/carbonara-core:0.6
```
### Running on local machine
```bash
docker compose up
```
- Set active profile to dev
- Start application via gui