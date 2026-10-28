import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServidorChat {
    private static final List<PrintWriter> clientes = new CopyOnWriteArrayList<>();
    private static final List<String> apelidos = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        int porta = 5000;

        try (ServerSocket servidor = new ServerSocket(porta)) {
            System.out.println("Servidor de Chat iniciado em localhost:" + porta);

            while (true) {
                Socket clienteSocket = servidor.accept();
                System.out.println("Conexão estabelecida com " + clienteSocket.getRemoteSocketAddress());

                // Criar thread para gerenciar o novo cliente
                Thread thread = new Thread(new ClientHandler(clienteSocket));
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }

    // Envia mensagem para todos os clientes conectados
    private static void broadcast(String mensagem) {
        for (PrintWriter clienteOut : clientes) {
            clienteOut.println(mensagem);
        }
    }

    // Handler responsável por cada cliente em uma thread separada
    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String apelido;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

                // Solicitar apelido
                out.println("APELIDO");
                apelido = in.readLine();

                if (apelido != null && !apelido.isEmpty()) {
                    // Registrar cliente
                    apelidos.add(apelido);
                    clientes.add(out);

                    System.out.println("Apelido do cliente: " + apelido);
                    broadcast(apelido + " entrou no chat!");
                    out.println("Conectado ao servidor!");

                    // Loop para receber mensagens do cliente
                    String mensagem;
                    while ((mensagem = in.readLine()) != null) {
                        broadcast(mensagem);
                    }
                }
            } catch (IOException e) {
                // Conexão perdida ou erro de leitura
            } finally {
                desconectar();
            }
        }

        private void desconectar() {
            if (out != null) {
                clientes.remove(out);
            }
            if (apelido != null) {
                apelidos.remove(apelido);
                broadcast(apelido + " saiu do chat!");
                System.out.println(apelido + " desconectou");
            }
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}