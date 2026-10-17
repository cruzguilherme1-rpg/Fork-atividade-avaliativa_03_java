FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY Produtor.java .
RUN javac Exemplo.java Sequencial.java ProdutorConsumidor.java
CMD ["java", "Exemplo", "ProdutorConsumidor"]