import java.io.IOException;
import java.ioPrintWriter;
import java.net.ServerSocket;
import java.net.socket;
import java.nio.charset.StandardCharsets;

public class ServidorBasico{
    public static void main(String[] args){
        String host = "localhost";
        int porta = 5000;

        // try-with-resources garante que o ServerSocket seja fechado ao final 
        try (ServidorSocket servidor = new ServerSocket(porta)){

            system.out.println("servidor escutando em " + host + ":" + porta);

            // Aguardar por uma conexão (bloqueante)
            try (Socket conexao = servidor.accept()) {
                System.out.println = new PrintWriter(conexao.getOutputStream(), true, StandardCharsets.UTF_8);
            
                String mensagem = "Bem vindo ao servidor!";
                out.println(mensagem);
            } // O socket da conexão com o cliente é fechado automaticamente aqui ao sair do try interno
        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
        } finally {
            System.out.println("Servidor encerrado");
        }
    }
}