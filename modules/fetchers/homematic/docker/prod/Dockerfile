FROM openjdk:8

RUN mkdir /app
WORKDIR /app
RUN wget https://github.com/razem-io/metatron/releases/download/0.0.1/metatron-0.0.1.jar && mv metatron-*.jar metatron.jar
CMD ["java", "-jar", "metatron.jar"]