import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ConsumidorClient {

    public static void main(String[] args) {
        // O nome do host 'produtor-host' será resolvido pelo Docker
        String host = System.getenv().getOrDefault("PRODUTOR_HOST", "produtor-host");
        int porta = 5000;

        System.out.println("### [Cliente/Consumidor] Tentando conectar ao produtor em " + host + ":" + porta);

        try {
            // Conecta ao Socket do Servidor
            Socket socket = new Socket(host, porta);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Lê a resposta enviada pelo servidor
            String resposta = in.readLine();
            System.out.println("### [Cliente/Consumidor] Dados recebidos via Socket -> " + resposta);

            // Trata a string "[1, 2, 3...]" de volta para inteiros e calcula a soma
            String limpo = resposta.replace("[", "").replace("]", "").trim();
            String[] numeros = limpo.split(",\\s*");

            int resultado = 0;
            for (String numStr : numeros) {
                if (!numStr.isEmpty()) {
                    resultado += Integer.parseInt(numStr);
                }
            }

            System.out.println("### [Cliente/Consumidor] Resultado da Soma -> " + resultado);
            socket.close();

        } catch (Exception e) {
            System.err.println("Erro no consumidor: " + e.getMessage());
        }
    }
}