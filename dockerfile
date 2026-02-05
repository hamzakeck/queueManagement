

FROM maven:3.9.9-eclipse-temurin-11 AS build

WORKDIR /workspace


COPY pom.xml ./
RUN mvn -B -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests clean package


FROM tomcat:10.1-jre11-temurin


RUN rm -rf /usr/local/tomcat/webapps/*


COPY --from=build /workspace/target/queue-management.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 6061

CMD ["catalina.sh", "run"]
