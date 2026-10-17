import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProdutorServer {

    public static List<Integer> produzirDados() {
        List<Integer> novosDados = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            novosDados.add(random.nextInt(111));
        }
        return novosDados;
    }

    public static void main(String[] args) {
        int porta = 5000;
        System.out.println("# [Servidor/Produtor] Aguardando conexões na porta " + porta + "...");

        try (ServerSocket serverSocket = new ServerSocket(porta)) {
            while (true) {
                // Aguarda um cliente (Consumidor) se conectar
                Socket socket = serverSocket.accept();
                System.out.println("# [Servidor/Produtor] Consumidor conectado: " + socket.getInetAddress());

                // Produz os dados
                List<Integer> dados = produzirDados();
                
                // Envia a lista para o consumidor através do fluxo de saída do Socket
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(dados.toString());

                System.out.println("# [Servidor/Produtor] Dados enviados via Socket: " + dados);
                socket.close();
            }
        } catch (Exception e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }
}