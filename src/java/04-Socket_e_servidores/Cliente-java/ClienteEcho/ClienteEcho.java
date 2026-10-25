import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClienteEcho {
    public static void main(String[] args){
        String host = "localhost";
        int porta = 5000;

        // try-with-resources gerencia e fecha automaticamente o Socket e o Scanner
        try (Socket cliente = new Socket(host, porta);
            Scanner scanner = new Scanner(System.in);
            PrintWriter out = new PrintWriter(cliente.getOutputStream(), true, StandardCharsets.UTF_8);
            BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream(), StandardCharsets.UTF_8))) {  
            
            System.out.println("Conectado ao servidor em " + host + ":" + porta);
            System.out.println("digite \"sair\" para encerrar a conexão");
            
            while (true) {
                System.out.print("Digite uma mensagem: ");
                String mensagem = scanner.nextLine();

                if ("sair".equalsIgnoreCase(mensagem)) {
                    System.out.println("Encerrando a conexão...");
                    break;
                }
                
                // Enviar mensagem ao servidor
                out.println(mensagem);
                
                // Receber resposta do servidor
                String resposta = in.readLine();
                if (resposta != null) {
                    System.out.println("Resposta do servidor: " + resposta + "\n");
                } else {
                    System.out.println("Servidor encerrou a conexão.");
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Erro na conexão: " + e.getMessage());
        } finally {
            System.out.println("Conexão encerrada");
        }
    }
}