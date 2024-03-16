FROM openjdk:17
COPY ./build/libs/memoraize-0.0.1-SNAPSHOT.jar memoraize.jar
ENTRYPOINT ["java", "-jar", "memoraize.jar"]
