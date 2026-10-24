import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServidorBasico {
    public static void main(String[] args) {
        String host = "localhost";
        int porta = 5000;

        // try-with-resources garante que o ServerSocket seja fechado ao final
        try (ServerSocket servidor = new ServerSocket(porta)) {
            // Em Java, SO_REUSEADDR vem habilitado por padrão em ServerSocket na maioria das plataformas,
            // mas pode ser configurado explicitamente com servidor.setReuseAddress(true);

            System.out.println("Servidor escutando em " + host + ":" + porta);

            // Aguardar por uma conexão (bloqueante)
            try (Socket conexao = servidor.accept()) {
                System.out.println("Conexão estabelecida com " + conexao.getRemoteSocketAddress());

                // Criar canal de saída para enviar mensagem
                PrintWriter out = new PrintWriter(conexao.getOutputStream(), true, StandardCharsets.UTF_8);

                String mensagem = "Bem-vindo ao servidor!";
                out.println(mensagem);

            } // O socket da conexão com o cliente é fechado automaticamente aqui ao sair do try interno

        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        } finally {
            System.out.println("Servidor encerrado");
        }
    }
}