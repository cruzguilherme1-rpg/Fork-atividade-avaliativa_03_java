import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;

public class ClienteSocket {
    public static void main(String[] args) {
        String host = "localhost";
        int porta = 5000;

        // O try-with-resources garante que o socket seja fechado automaticamente ao final
        try (Socket cliente = new Socket(host, porta)) {
            System.out.println("Conectado ao servidor em " + host + ":" + porta);

            // Criar um leitor para receber os dados do servidor
            InputStreamReader isr = new InputStreamReader(cliente.getInputStream(), "UTF-8");
            BufferedReader leitor = new BufferedReader(isr);

            // Receber e ler os dados
            String mensagem = leitor.readLine();
            System.out.println("Mensagem recebida: " + mensagem);

        } catch (IOException e) {
            System.err.println("Erro na conexão: " + e.getMessage());
        }

        System.out.println("Conexão encerrada");
    }
}