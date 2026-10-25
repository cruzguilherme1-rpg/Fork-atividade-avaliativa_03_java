import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClienteChat {
    private static String apelido;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Escolha seu apelido: ");
        apelido = scanner.nextLine();

        try {
            Socket cliente = new Socket("localhost", 5000);

            // Thread para receber mensagens
            Thread threadReceber = new Thread(new ReceberMensagensHandler(cliente));
            threadReceber.start();

            // Thread para enviar mensagens
            Thread threadEnviar = new Thread(new EnviarMensagensHandler(cliente, scanner));
            threadEnviar.start();

        } catch (IOException e) {
            System.err.println("Não foi possível conectar ao servidor: " + e.getMessage());
        }
    }

    // Task Runnable para receber mensagens do servidor
    private static class ReceberMensagensHandler implements Runnable {
        private final Socket cliente;

        public ReceberMensagensHandler(Socket cliente) {
            this.cliente = cliente;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(cliente.getInputStream(), StandardCharsets.UTF_8));
                 PrintWriter out = new PrintWriter(
                    cliente.getOutputStream(), true, StandardCharsets.UTF_8)) {

                String mensagem;
                while ((mensagem = in.readLine()) != null) {
                    if ("APELIDO".equals(mensagem)) {
                        out.println(apelido);
                    } else {
                        System.out.println(mensagem);
                    }
                }
            } catch (IOException e) {
                System.out.println("Erro ao receber mensagem!");
            } finally {
                fecharSocket(cliente);
            }
        }
    }

    // Task Runnable para enviar mensagens ao servidor
    private static class EnviarMensagensHandler implements Runnable {
        private final Socket cliente;
        private final Scanner scanner;

        public EnviarMensagensHandler(Socket cliente, Scanner scanner) {
            this.cliente = cliente;
            this.scanner = scanner;
        }

        @Override
        public void run() {
            try {
                PrintWriter out = new PrintWriter(
                    cliente.getOutputStream(), true, StandardCharsets.UTF_8);

                while (true) {
                    String mensagem = scanner.nextLine();
                    String mensagemCompleta = apelido + ": " + mensagem;
                    out.println(mensagemCompleta);
                }
            } catch (IOException e) {
                System.out.println("Erro ao enviar mensagem!");
            } finally {
                fecharSocket(cliente);
            }
        }
    }

    private static void fecharSocket(Socket socket) {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}