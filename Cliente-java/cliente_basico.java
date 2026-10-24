import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;

public class ClienteSocket {
    public static void main(String[] args){
        String host = "localhost";
        int porta = 5000

        try (Socket cliente = new Socket(host, porta)) {
            System.out.println("conectando ao servidos em " + host + ":" + porta);

            InputStreamReader isr = new InputStreamReader(cliente.getINputStream(), "UTF-8");
            BufferedReader leitor = new BufferedReader(isr);

            String mensagem = leitor.readline();
            System.out.println("mensagem recebida: " + mensagem);

        } catch (IOException e) {
            System.err.println("Erro na conexão: " + e.mensagem());
        }

        System.out.println("Conexão encerrada");
    }
}