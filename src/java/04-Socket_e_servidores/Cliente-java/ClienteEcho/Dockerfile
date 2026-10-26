#para executar docker run -it --network="host" cliente-echo-java

FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY ClienteEcho.java .

RUN javac ClienteEcho.java 

CMD ["java", "ClienteEcho"]