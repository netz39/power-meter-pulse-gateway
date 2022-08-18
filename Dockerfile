#
# This dockerfile expects a compiled artifact in the target folder.
# Call "mvn clean package" first!
#
FROM openjdk:17-jdk-slim

RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

EXPOSE 8080
HEALTHCHECK --interval=5s CMD curl --fail http://localhost:8080/health || exit 1

COPY target/pwr-mtr-pls-gw-*.jar /usr/local/lib/power-meter-pulse-gateway.jar

ENTRYPOINT ["java","-jar","/usr/local/lib/power-meter-pulse-gateway.jar"]
