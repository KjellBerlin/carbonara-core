# The Carbonara App

The Carbonara app is an open-source project designed to demonstrate how various components can be combined to create a fully functional end-to-end solution, with both frontend and backend. This React Native app is inspired by a sample from Creative Tim.

Important Notice:
This project is provided without a license. As such, the use of this code—whether for commercial or non-commercial purposes—is strictly prohibited without explicit permission from the project owner. The code is shared solely for educational purposes and as a source of inspiration.

If you wish to use the code, please contact me to request permission.

Some articles about the carbonara app can be found on my medium account https://medium.com/@kjell.lilliestolze.

## Build and Push
### Build docker image
Do not forget to use right tag
```bash
docker build -t netflixnetflix/carbonara-core:0.6 .
```

### Push docker image do google cloud artifact registry
Update tag
```bash
docker tag netflixnetflix/carbonara-core:0.6 {artifact-registry-url}:0.6
```
```bash
docker push {artifact-registry-url}:0.6
```

## Running locally

To run it locally, you also need to run a local MongoDB instance for example in a docker container.

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
