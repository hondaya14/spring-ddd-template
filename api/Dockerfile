ARG JAVA_IMAGE_TAG=latest

# Stage 0 - Build api project
FROM public.ecr.aws/docker/library/eclipse-temurin:${JAVA_IMAGE_TAG} AS builder

COPY . /opt/spring-ddd-template
WORKDIR /opt/spring-ddd-template
RUN ./gradlew api:build -x check

# Stage 1 - Setup container
FROM public.ecr.aws/docker/library/eclipse-temurin:${JAVA_IMAGE_TAG}

COPY --from=builder /opt/spring-ddd-template/api/build/libs/api.jar .

ENTRYPOINT ["java", "-jar","api.jar"]
