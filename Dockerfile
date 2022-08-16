#
# This dockerfile expects a compiled artifact in the target folder.
# Call "mvn clean package" first!
#
FROM openjdk:17-jdk-slim

EXPOSE 8080
#HEALTHCHECK --interval=10s CMD curl --fail http://localhost:8080/health || exit 1

COPY target/pwr-mtr-pls-gw-*.jar /usr/local/lib/power-meter-pulse-gateway.jar

ENTRYPOINT ["java","-jar","/usr/local/lib/power-meter-pulse-gateway.jar"]
