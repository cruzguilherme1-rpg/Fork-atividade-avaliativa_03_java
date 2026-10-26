#para executar docker run --network="host" cliente-socket-java

FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY ClienteSocket.java .

RUN javac ClienteSocket.java 

CMD ["java", "ClienteSocket"]