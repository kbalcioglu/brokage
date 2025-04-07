# Build and Test
All tests are running before building jar
```bash
./gradlew clean build
```

# Docker build
```bash
docker build -t stock-brokerage-service .
```
# Docker run
This is not working on my macbook, since it is M4 and there is known issue with it
https://github.com/corretto/corretto-21/issues/85
```bash
 docker run -d --name app -p 8080:8080 stock-brokerage-service
```

# Run application
RUN
[BrokageApplication.java](src/main/java/com/example/brokage/BrokageApplication.java)
# Swagger UI
http://localhost:8080/swagger-ui/index.html#

# Requirements
- First register a user with name and password.
- Use login endpoint to retrieve JWT token
- Set JWT token on swagger UI
- Test other endpoints