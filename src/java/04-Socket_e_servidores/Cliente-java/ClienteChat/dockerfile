#para rodar docker run -it --network="host" cliente-chat-java

FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY ClienteChat.java .

RUN javac ClienteChat.java 

CMD ["java", "ClienteChat"]