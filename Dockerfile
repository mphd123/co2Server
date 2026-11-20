FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .

COPY src ./src
RUN mvn package -DskipTests
# nooe -DskipTests if the dat abse is changed to not be local

FROM eclipse-temurin:21-jre
WORKDIR /app
# pyhton for matplotlip
RUN apt-get update && apt-get install -y --no-install-recommends \
        python3 python3-pip \
    && rm -rf /var/lib/apt/lists/*

RUN pip3 install --break-system-packages --no-cache-dir \
        grpcio protobuf matplotlib
CMD mkdir ./data

COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/src/main/python ./src/main/python

RUN mkdir  /app/data


ENV MPLBACKEND=Agg

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]