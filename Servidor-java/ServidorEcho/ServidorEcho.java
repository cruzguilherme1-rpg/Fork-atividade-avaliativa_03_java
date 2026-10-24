import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServidorEcho {
    public static void main(String[] args) {
        int porta = 5000;

        try (ServerSocket servidor = new ServerSocket(porta)) {
            System.out.println("Servidor Echo escutando em localhost:" + porta);

            while (true) {
                System.out.println("\nAguardando conexão...");
                Socket conexao = servidor.accept();
                System.out.println("Conectado com " + conexao.getRemoteSocketAddress());

                try (
                    BufferedReader in = new BufferedReader(
                        new InputStreamReader(conexao.getInputStream(), StandardCharsets.UTF_8));
                    PrintWriter out = new PrintWriter(
                        conexao.getOutputStream(), true, StandardCharsets.UTF_8)
                ) {
                    String mensagem;
                    // LER dados até que o cliente encerre o fluxo (retorna null)
                    while ((mensagem = in.readLine()) != null) {
                        System.out.println("Recebido: " + mensagem);

                        // Enviar dados de volta (echo)
                        String resposta = "Echo: " + mensagem;
                        out.println(resposta);
                    }
                    System.out.println("Cliente desconectou");

                } catch (IOException e) {
                    System.err.println("Erro na comunicação com o cliente: " + e.getMessage());
                } finally {
                    try {
                        conexao.close();
                    } catch (IOException e) {
                        System.err.println("Erro ao fechar conexão: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }
}