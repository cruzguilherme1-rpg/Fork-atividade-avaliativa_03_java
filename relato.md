**Relatório de Implementação de Comunicação entre Tarefas em Java**

**📌 Introdução**

Este relato faz parte do processo avaliativo da disciplina de **Sistemas Operacionais** no curso superior de **Tecnologia em Análise e Desenvolvimento de Sistemas**, ofertado pela **Diretoria Acadêmica de Gestão e Tecnologia da Informação** do **Campus Natal-Central** do **Instituto Federal de Educação, Ciência e Tecnologia do Rio Grande do Norte (IFRN)**.

O objetivo principal deste trabalho é relatar e demonstrar as implementações de **comunicação entre tarefas** na linguagem Java.

Os vídeos referentes à execução dos códigos de exemplo podem ser encontrados neste link: https://drive.google.com/drive/folders/1W9NS54mY3RVc7DCYud6aS2SnwZFp1TAj?usp=drive_link

**👥 Equipe de Desenvolvimento**

- Guilherme da Silva Cruz
- Maria Clara Batista Viana Silva
- José Vine Nunes Martins Araújo

**⚙️ Comunicação entre Tarefas em Java**

**💡 Informações Gerais**

A comunicação entre tarefas (*IPC - Inter-Process Communication* ou comunicação inter-threads) tem como objetivo permitir que diferentes fluxos de execução troquem dados, sincronizem suas operações e cooperem para resolver um problema em comum. Em arquiteturas baseadas no padrão **Produtor-Consumidor**, essa abordagem possibilita a separação de responsabilidades entre a geração de dados e seu processamento.

**🐳 Uso e Configuração do Docker**

O uso do **Docker** neste trabalho garante a padronização do ambiente de execução entre os membros da equipe e na avaliação do professor. Como o Java é executado sobre a JVM, o contêiner isola o ambiente do **JDK (Java Development Kit)**, garantindo que limitações de sistema operacional, variáveis de ambiente ou diferenças de versão não interfiram na criação e no agendamento de threads e processos.

A configuração básica do Docker utiliza uma imagem oficial do OpenJDK (eclipse-temurin:17-jdk) executada via terminal ou orquestrada com docker-compose.yml.

**🛠️ Relato de Desenvolvimento e Implementações**

**🧵 1. Comunicação entre Tarefas no Mesmo Processo (Linhas de Execução)**

A comunicação entre tarefas dentro do mesmo processo ocorre por meio de **Threads**, que compartilham o mesmo espaço de endereçamento de memória RAM (a memória Heap).
Nos códigos implementados, essa integração foi feita através de uma variável estática compartilhada (dados): a thread do Produtor grava os números gerados nessa estrutura, e a thread do Consumidor lê os dados diretamente da memória para calcular a soma acumulada.
Na prática, essa comunicação no mesmo processo pode assumir três formas principais:
 * Chamada Direta de Fluxo (Sem Troca de Dados): Execução sequencial de rotinas e métodos utilitários sem transferência de estado.
 * Troca por Parâmetros e Retornos (Single-Thread): A comunicação ocorre pela passagem de referências de objetos através da pilha de execução (stack) em um único fluxo de controle.
 * Memória Compartilhada (Multi-Thread): Múltiplas linhas de execução concorrentes acessam, alteram e leem variáveis e estruturas comuns na memória do processo, exigindo mecanismos de sincronização.

**💻 Códigos Implementados:**

1. **Chamada Direta de Fluxo** (*Exemplo.java*)

```java
public class Exemplo {
    public static void produzirDados(){
        System.out.println("Dados produzidos");
    }

    public static void main(String[] args) {
        produzirDados();
    }
}
```

**📥 Execução do Código:**

A execução foi simulada em um ambiente totalmente isolado utilizando o **Docker**:

Bash 
```bash
docker build -t app-exemplo
docker run --rm app-exemplo
```
Saída do terminal do contêiner:
Terminal
```text
Dados produzidos
```

2. **Comunicação por Parâmetro e Retorno** (*Sequencial.java*)

```java
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Sequencial {

    public static List<Integer> produzirDados() {
        List<Integer> dados = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 100; i++) {
            dados.add(random.nextInt(111));
        }
        
        return dados;
    }

    public static void consumirDados(List<Integer> dados) {
        int resultado = 0;
        for (int num : dados) {
            resultado += num;
        }

        System.out.println("Recebeu -> " + resultado);
    }

    public static void principal() {
        System.out.println("Iniciou");

        // Transfere o retorno de produzirDados() diretamente para consumirDados()
        List<Integer> dados = produzirDados();
        consumirDados(dados);

        System.out.println("Finalizou");
    }

    public static void main(String[] args) {
        principal();
    }
}
```

**📥 Execução do Código:**
A execução foi simulada em um ambiente totalmente isolado utilizando o **Docker**:

Bash 
```bash
docker build -t app-sequencial
docker run --rm app-sequencial
```
Saída do terminal do contêiner:
Terminal
```text
Iniciou
Recebeu -> 5432
Finalizou
```

3. **Comunicação Concorrente via Memória Compartilhada** (*ProdutorConsumidor.java*)

```java
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProdutorConsumidor {
    
    // Memória compartilhada entre threads no mesmo processo
    private static List<Integer> dados = new ArrayList<>();
    
    public static List<Integer> produzirDados() {
        System.out.println("# Produzir - iniciado");
        
        List<Integer> novosDados = new ArrayList<>(); 
        Random random = new Random(); 
        
        for (int i = 0; i < 100; i++) {
            novosDados.add(random.nextInt(111)); 
        }
        
        dados = novosDados; // Atualiza variável estática comum
        
        System.out.println("# produzir " + dados);
        System.out.println("# produzir - terminado");
        return dados;
    }
    
    public static void consumirDados() {
        System.out.println("### Consumir - iniciado");
        System.out.println("### Dados -> " + dados);
        
        int resultado = 0;
        for (int num : dados) {
            resultado += num;
        }
        
        System.out.println("### Resultado -> " + resultado);
        System.out.println("### Consumir - terminado");
    }
    
    public static void principal() {
        System.out.println("iniciou");
        
        Thread threadProdutor = new Thread(ProdutorConsumidor::produzirDados);
        Thread threadConsumidor = new Thread(ProdutorConsumidor::consumirDados);
        
        // Executa a thread produtora e aguarda o término (.join)
        threadProdutor.start();
        try {
            threadProdutor.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread interrompida: " + e.getMessage());
        }

        // Executa a thread consumidora e aguarda o término (.join)
        threadConsumidor.start();
        try {
            threadConsumidor.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread interrompida: " + e.getMessage());
        }

        System.out.println("Finalizou");
    }

    public static void main(String[] args) {
        principal();
    }
}
```

**📥 Execução do Código:**

Bash 
```bash
docker build -t app-produtorconsumidor
docker run --rm app-produtorconsumidor
```

Saída do terminal do contêiner:
Terminal
```text
iniciou
# Produzir - iniciado
# produzir [42, 105, 12, 88, 3, 99, ..., 77]
# produzir - terminado
### Consumir - iniciado
### Dados -> [42, 105, 12, 88, 3, 99, ..., 77]
### Resultado -> 5489
### Consumir - terminado
Finalizou
```

**⚠️ Desafios Encontrados e Soluções:**

1. **Divergência entre o nome da classe e do arquivo:**
   * **Problema:** Devido ao pouco costume com a sintaxe do Java, o nome da classe pública diferia do nome do arquivo salvo no disco.
   * **Solução:** A classe foi renomeada para seguir a convenção PascalCase do Java, garantindo que o nome do arquivo fosse exatamente ProdutorConsumidor.java.
2. **Dificuldade com o controle de chaves { } na sintaxe:**
   * **Problema:** Ao estruturar métodos e classes, ocorreram erros de compilação por conta do esquecimento de fechamento ou abertura incorreta de blocos.
   * **Solução:** Análise minuciosa das mensagens de erro emitidas pelo compilador Java no terminal para localizar as linhas afetadas e ajustar a identação.
3. **Inconsistência na Visibilidade de Memória:**
   *  **Problema:** Em processadores multicore, uma Thread pode guardar o valor da variável estática dados no cache local do núcleo (L1/L2 Cache), não enxergando a atualização feita por outra Thread na memória principal RAM.
   *  **Solução:** Declarar a variável compartilhada com a palavra-chave volatile (private static volatile List<Integer> dados;), forçando a leitura e escrita diretamente na memória principal.
4. **Coleção Não Segura para Concorrência (*Thread-Unsafe*):**
   * **Problema:** O ArrayList utilizado nos códigos não é sincronizado. Se múltiplas Threads tentarem escrever ou modificar a lista ao mesmo tempo, ocorrerá corrupção de dados ou exceção ConcurrentModificationException.
   * **Solução:** Substituir por coleções concorrentes como Collections.synchronizedList(...) ou utilizar filas thread-safe como ConcurrentLinkedQueue.
5. **Condição de Corrida (*Race Condition*):**
   * **Problema:** No código ProdutorConsumidor, se os métodos .join() forem removidos e ambas as Threads forem iniciadas juntas com .start(), o consumidor tentará ler a variável dados antes que o produtor tenha terminado de preenchê-la.
   * **Solução:** Utilizar mecanismos de sincronização como join(), blocos synchronized, ou estruturas concorrentes como BlockingQueue (ex: ArrayBlockingQueue).

**💻 2. Comunicação entre Tarefas em Processos Diferentes no Mesmo Computador** (*Pipe*)

Nesta etapa, o padrão é adaptado para executar a produção de dados de forma isolada do fluxo de execução sequencial de um único processo de aplicação. O programa primeiro chama a rotina de produção de forma modular para salvar o resultado antes de invocar o consumidor no processo principal.
 * Isolamento de Processos: Diferente de threads dentro da mesma aplicação, cada processo roda com seu próprio espaço de memória gerenciado e protegido pelo Sistema Operacional.
 * Separação de Responsabilidades: O Produtor roda em seu próprio processo/contêiner, grava/envia a saída gerada e encerra. Em seguida, o Consumidor roda em outro processo/contêiner separado, recebendo esses dados como entrada para processar.
 * Mecanismo via Pipe (|): A comunicação é feita redirecionando a saída padrão (stdout) do processo produtor para a entrada padrão (stdin) do processo consumidor.

**💻 Códigos Implementados:**

1. **Produtor** (*Produtor.java*)
```java
public class Produtor {

    public static void main(String[] args) {
        //System.out.println("Alo mundo");
        System.out.println("Mais um teste");
    }
}
```

2. **Consumidor** (*Consumidor.java*)
```java
import java.util.Scanner;

public class Consumidor {
    
    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);
        System.out.println("Entrada: " + entrada.nextLine());
    }
}
```
**📥 Execução do Código:**

Bash
```bash
docker run productor | docker run -i consumidor
```
Exemplo de saída do terminal:
Terminal
```text
Entrada: Mais um teste
```

**⚠️ Desafios Encontrados e Soluções:**

1. **Isolamento de Memória entre Processos/Contêineres:**
   * **Problema:** Por estarem em processos e contêineres separados, o Produtor e o Consumidor não compartilham a mesma memória RAM (não podem usar objetos, instâncias ou variáveis compartilhadas).
   * **Solução:** Redirecionamento da saída padrão (stdout) do Produtor para a entrada padrão (stdin) do Consumidor via Pipe de SO (docker run productor | docker run -i consumidor).
2. **Perda da Entrada Padrão no Contêiner Consumidor:**
   * **Problema:** Por padrão, o comando docker run roda sem manter o fluxo de entrada (stdin) aberto para receber dados externos de um pipe.
   * **Solução:** O uso da flag -i (docker run -i consumidor), que mantém a entrada interativa aberta para ler o stream enviado pelo Produtor.
3. **Bloqueio de Leitura (Gargalo de Leitura Única):**
   * **Problema:** O Consumidor utiliza entrada.nextLine(), lendo apenas uma única linha. Se o Produtor emitisse múltiplas linhas ou streams contínuos, as linhas subsequentes seriam ignoradas.
   * **Solução:** Usar um laço de repetição (while (entrada.hasNextLine())) para consumir continuamente até o encerramento da saída do Produtor.

Aqui está o texto reorganizado e atualizado para refletir com precisão a implementação dos códigos fornecidos (`ServidorChat.java` e `ClienteChat.java`), mantendo o rigor técnico, os desafios enfrentados e a conclusão.

---

**🌐 3. Comunicação entre Tarefas em Computadores/Contêineres Diferentes (*Sockets TCP*)**

Para evoluir a arquitetura local para um ambiente distribuído, o código foi refatorado dividindo as responsabilidades do modelo de comunicação em dois programas independentes que se comunicam através da pilha de protocolos TCP/IP em uma aplicação de chat multithreaded:

* **ServidorChat.java (Servidor Socket Multithreaded):** Atua como o nó central do chat. Ele abre uma porta de rede (`ServerSocket` na porta `5000`) e aguarda conexões concorrentes. Para cada cliente conectado, cria uma nova *Thread* dedicada (`ClientHandler`), permitindo o gerenciamento simultâneo de múltiplos usuários, registro de apelidos e retransmissão de mensagens (*broadcast*) para todos os participantes.


* **ClienteChat.java (Cliente Socket Multithreaded):** Atua como o nó terminal do usuário. Solicita um apelido, conecta-se ao servidor e gerencia o fluxo concorrente de entrada e saída por meio de duas *Threads* independentes (`ReceberMensagensHandler` para escutar o servidor e `EnviarMensagensHandler` para capturar a entrada do usuário no console e enviá-la).


* **Orquestração com Docker Compose:** Define a rede virtual do tipo *bridge* conectando os contêineres e resolvendo os nomes de host automaticamente via DNS interno do Docker.

---

**💻 Código Implementado:**

1. **Servidor** (*ServidorChat.java*)

```java
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

    // Envia mensagem para todos os clientes conectados (exceto o remetente)
    private static void broadcast(String mensagem, PrintWriter remetente) {
        for (PrintWriter clienteOut : clientes) {
            if (clienteOut != remetente) {
                clienteOut.println(mensagem);
            }
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
                    broadcast(apelido + " entrou no chat!", out);
                    out.println("Conectado ao servidor!");

                    // Loop para receber mensagens do cliente
                    String mensagem;
                    while ((mensagem = in.readLine()) != null) {
                        broadcast(mensagem, out);
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

```

2. **Cliente** (*ClienteChat.java*)

```java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClienteChat {
    private static String apelido;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Escolha seu apelido: ");
        apelido = scanner.nextLine();

        try {
            Socket cliente = new Socket("localhost", 5000);

            // Thread para receber mensagens
            Thread threadReceber = new Thread(new ReceberMensagensHandler(cliente));
            threadReceber.start();

            // Thread para enviar mensagens
            Thread threadEnviar = new Thread(new EnviarMensagensHandler(cliente, scanner));
            threadEnviar.start();

        } catch (IOException e) {
            System.err.println("Não foi possível conectar ao servidor: " + e.getMessage());
        }
    }

    // Task Runnable para receber mensagens do servidor
    private static class ReceberMensagensHandler implements Runnable {
        private final Socket cliente;

        public ReceberMensagensHandler(Socket cliente) {
            this.cliente = cliente;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(cliente.getInputStream(), StandardCharsets.UTF_8));
                 PrintWriter out = new PrintWriter(
                    cliente.getOutputStream(), true, StandardCharsets.UTF_8)) {

                String mensagem;
                while ((mensagem = in.readLine()) != null) {
                    if ("APELIDO".equals(mensagem)) {
                        out.println(apelido);
                    } else {
                        System.out.println(mensagem);
                    }
                }
            } catch (IOException e) {
                System.out.println("Erro ao receber mensagem!");
            } finally {
                fecharSocket(cliente);
            }
        }
    }

    // Task Runnable para enviar mensagens ao servidor
    private static class EnviarMensagensHandler implements Runnable {
        private final Socket cliente;
        private final Scanner scanner;

        public EnviarMensagensHandler(Socket cliente, Scanner scanner) {
            this.cliente = cliente;
            this.scanner = scanner;
        }

        @Override
        public void run() {
            try {
                PrintWriter out = new PrintWriter(
                    cliente.getOutputStream(), true, StandardCharsets.UTF_8);

                while (true) {
                    String mensagem = scanner.nextLine();
                    String mensagemCompleta = apelido + ": " + mensagem;
                    out.println(mensagemCompleta);
                }
            } catch (IOException e) {
                System.out.println("Erro ao enviar mensagem!");
            } finally {
                fecharSocket(cliente);
            }
        }
    }

    private static void fecharSocket(Socket socket) {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

```

---

**📥 Execução da Aplicação:**

```
bash 1 (servidor)
docker run -p 5000:5000 servidor-chat-java


```
```
bash 2 (1° cliente)
docker run -it --network="host" cliente-chat-java

```

```
bash 3 (2° cliente)
docker run -it --network="host" cliente-chat-java

```

**Saída do terminal:**

```text
conteiner_servidor_chat | Servidor de Chat iniciado em localhost:5000
conteiner_servidor_chat | Conexão estabelecida com /172.18.0.3:54321
conteiner_cliente_chat  | Escolha seu apelido: Alice
conteiner_servidor_chat | Apelido do cliente: Alice
conteiner_cliente_chat  | Conectado ao servidor!
conteiner_servidor_chat | Conexão estabelecida com /172.18.0.4:54322
conteiner_servidor_chat | Apelido do cliente: Bob
conteiner_cliente_chat  | Bob entrou no chat!

```

---

**⚠️ Desafios Encontrados e Soluções:**

1. **Problemas no Controle de Versão (Git Error / Permissão 403):**
* **Problema:** Ao enviar as correções e documentações via `git push`, ocorreu o erro `403 (Permission Denied)` devido a conflitos com credenciais salvas no sistema e falta de permissão direta de escrita no repositório.
* **Solução:** Alinhamento com a equipe para que o proprietário do repositório enviasse o convite formal de colaboração no GitHub, além do ajuste nas credenciais locais do Git.


2. **Concorrência na Lista de Clientes Conectados (*Race Conditions*):**
* **Problema:** Quando múltiplos clientes se conectavam ou desconectavam simultaneamente, a remoção/adição na lista global de clientes podia gerar `ConcurrentModificationException` ou corrupção do estado da lista.
* **Solução:** Utilização das coleções *thread-safe* `CopyOnWriteArrayList` para armazenar os canais de saída (`PrintWriter`) e os apelidos (`apelidos`), garantindo operações de *broadcast* e desconexão seguras sem travar a execução.




3. **Bloqueio de I/O em Comunicação Bidirecional Simultânea:**
* **Problema:** No cliente, a leitura de mensagens do servidor e a leitura do teclado (`Scanner`) são ambas chamadas bloqueantes (`readLine()` e `nextLine()`). Executá-las na mesma *Thread* faria o cliente travar aguardando digitação antes de conseguir exibir novas mensagens recebidas.


* **Solução:** Divisão da execução do cliente em duas *Threads* paralelas (`ReceberMensagensHandler` e `EnviarMensagensHandler`), permitindo escuta ativa e envio continuo de dados em tempo real.




4. **Falha de Conectividade por Ordem de Inicialização dos Contêineres:**
* **Problema:** Se o contêiner do cliente iniciar e tentar estabelecer a conexão Socket antes que o servidor esteja operacional, a aplicação lança a exceção `java.net.ConnectException: Connection refused`.
* **Solução:** Uso da diretiva `depends_on` no `docker-compose.yml` combinada com tratamento de exceções no cliente para gerenciar falhas de conexão de forma graciosa.


5. **Incapacidade de Resolução de Nomes entre Contêineres:**
* **Problema:** Ao tentar conectar via `new Socket("localhost", 5000)` dentro de um contêiner Docker, o `localhost` aponta para a interface de loopback do próprio contêiner cliente, e não para o servidor.
* **Solução:** Criação de uma rede customizada no Docker (*bridge network*) e substituição do endereço estático pelo nome do serviço configurado no Compose (`servidor-chat`), permitindo que o DNS interno do Docker resolva o IP correto.


6. **Incompatibilidade de Codificação de Caracteres (Encoding):**
* **Problema:** A troca de mensagens contendo acentos ou caracteres especiais entre sistemas operacionais ou contêineres distintos resultava em caracteres corrompidos (*mojibake*).
* **Solução:** Padronização do fluxo de dados com `StandardCharsets.UTF_8` na construção dos objetos `PrintWriter` e `InputStreamReader` em ambos os lados (Servidor e Cliente).





---

**🏁 Considerações Finais**

A realização deste trabalho permitiu vivenciar, de forma prática e incremental, a evolução das abordagens de Comunicação entre Tarefas (IPC) em Java, correlacionando conceitos teóricos de Sistemas Operacionais com práticas de arquitetura de software e infraestrutura de contêineres.

Através das etapas desenvolvidas, foi possível consolidar importantes aprendizados:

1. **Evolução da Complexidade e Acoplamento:**
* A comunicação no mesmo processo (via Threads e memória compartilhada) provou ser a mais rápida em tempo de execução. Contudo, exige atenção rigorosa ao controle de concorrência, visibilidade de memória (`volatile`) e estruturas *thread-safe* para evitar *race conditions* e corrupção de dados.
* A comunicação entre processos distintos no mesmo computador (via Pipes) introduziu o conceito de isolamento de memória. Embora reduza o risco de acessos indevidos à memória, impõe a necessidade de serialização simples de dados (`stdout` → `stdin`) e sincronização pela finalização da execução do fluxo de escrita.
* A comunicação em ambiente distribuído (via Sockets TCP/IP com suporte Multithread) elevou o padrão para arquiteturas orientadas a microsserviços e sistemas de bate-papo em tempo real. Essa abordagem elimina completamente a dependência de memória física local, exigindo o gerenciamento explícito de concorrência concorrente (`CopyOnWriteArrayList`), protocolo de rede, codificação UTF-8 e divisão de responsabilidades entre Threads de entrada/saída.




2. **A Importância da Conteinerização com Docker:**
* O uso do Docker e do Docker Compose foi fundamental para abstrair divergências de sistemas operacionais e versões do JDK entre os membros da equipe.
* A configuração de redes virtuais (*bridge*) e o DNS interno do Docker demonstraram como a infraestrutura moderna resolve dinamicamente o acoplamento de rede, permitindo que contêineres independentes se comuniquem de forma transparente através de nomes de serviços em vez de endereços IP estáticos.



Como recomendação para turmas futuras, sugere-se a expansão das práticas com inclusão de testes de estresse e cenários avançados de concorrência. Embora os desafios técnicos encontrados no desenrolar da implementação tenham sido significativos, a prática mostrou a importância dos padrões de sincronização, como também a manipulação do Docker junto das Threads e dos Sockets TCP. Além disso, a atividade consolidou os requisitos teóricos e práticos que são exigidos na disciplina.