ARG JAVA_IMAGE_TAG=latest

# Stage 0 - Build batch project
FROM public.ecr.aws/docker/library/eclipse-temurin:${JAVA_IMAGE_TAG} AS builder

COPY . /opt/spring-ddd-template
WORKDIR /opt/spring-ddd-template
RUN ./gradlew batch:build -x check

# Stage 1 - Setup container
FROM public.ecr.aws/docker/library/eclipse-temurin:${JAVA_IMAGE_TAG}

ENTRYPOINT ["java", "-jar","batch.jar"]
