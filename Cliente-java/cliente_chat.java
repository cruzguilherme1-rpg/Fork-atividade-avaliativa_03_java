import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClienteChat {
    private static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

        System.out.println("Escolha seu apelido: ");
        apelido = scanner.nextLine();

        try {
            Socket cliente = new Socket("localhost", 5000);
            
            // Thread para receber mensagens
            Thread threadReceber = new Thread(new ReceberMensagensHandler(cliente));
            threadReceber.start();

            Thread threadEnviar = new Thread(new EnviarMensagensHandler(cliente, scanner));
            threadEnviar.start();
        } catch (IOException e){
            System.err.println("não foi possivel conectar ao servidor: " + e.getMessage());   
        }
    }
    
    // Task Runnable para receber mensagens do servidor
    private static class ReceberMensagensHandler implements Runnable {
        private final Socket cliente;

        public ReceberMensagensHandler(Socket cliente){
            this.cliente = cliente;
        }

        @Override
        public void run(){
            try (BufferedReader in = new BufferedReader(
                new InputStreamReader(cliente.getINputStream(), StandardCharsets.UTF_8))
                PrintWriter out = new PrintWriter(
                    cliente.getOutputSteam(), true, StandardCharsets.UTF_8)) {

                        String mensagem;
                        while ((mensagem = in.readline()) != null) {
                            if("APELIDO".equals(mensagem)) {
                                out.println(apelido);
                            } else {
                                System.out.println(mensagem);
                            }
                        }
                    } catch (IOException e ){
                        System.out.println("Erro ao receber mensagem!");
                    } finally {
                        fecharSocket(cliente);
                    }
        }
    }

    private static class EnviarMensagensHandLer implements Runnable {
        private final Socket cliente;
        private final Scanner scanner;

        public EnviarMensagensHandler(Socket cliente, Scanner scanner) {
            this.cliente = cliente;
            this.scanner = scanner;
        }

        @Override
        public void run() {
            try (PrintWriter out = new PrintWriter(
                cliente.getOutputSteam(), true, StandardCharsets.UTF_8)) {
                    String mensagem;
                    while (true) {
                        mensagem = scanner.nextLine();
                        out.println(mensagem);
                    }
                } catch (IOException e) {
                    System.out.println("Erro ao enviar mensagem!");
                } finally {
                    fecharSocket(cliente);
                }
        }

    }

    pruvate static void fecharSocket(Socket cliente) {
        try {
            if (cliente != null && !cliente.isClosed()) {
                cliente.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}