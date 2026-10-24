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
    private static final List<PrintWriter> clientes = new CopyOnWriteArrrayList<>();
    private static final List<String> apelidos = new CopyOnWriteArrayList<>();

    public static void main(String[] args){
        int porta = 5000;

        try (ServerSocket servidor = new ServerSocket(porta)) {
            System.out.println("Servidor de chat iniciado em localhost: " + porta);
            
            while (true) {
                Socket cliente = servidor.accept();
                System.out.println("Conexão estabelecida com " + clienteSocket.getRemoteSocketAddress());

                Thread threadCliente = new Thread(new ClienteHandler(clienteSocket));
                threadCliente.start();
            }
        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }

    // Envia mensagem para todos os clientes conectados (exceto o remetente)
    private static void broadcast(String mensagem, PrintWriter remetente) {
        for (PrintWriter cliente : clientes) {
            if (cliente != remetente) {
                cliente.println(mensagem);
            }
        }
    }

    // Handler responsável por cada cliente em uma thread separado
    private static class ClienteHandler implement Runnable {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String apelido

        public ClienteHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true, StrandartCharsets.UTF_8);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            
                // Solicita o apelido do cliente
                out.println("APELIDO");
                apelido = in.readLine();

                if (apelido != null && !apelido.isEmpty()) {
                    //Registrar cliente
                    apelido.add(apelido);
                    clientes.add(out);

                    System.out.println("Apelido do cliente: " + apelido);
                    broadcast(apelido + " entrou no chat!", out);
                    out.println("Conectado ao servidor!");

                    //Loop para receber mensagens do cliente
                    String mensagem;
                    while ((mensagem = in.readLine()) != null) {
                        broadcast(mensagem, out);
                    }
                }
            } catch (IoException e) {

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
            broadcast(apelido + " saiu do chat!", out);
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