**Relatório de Implementação de Comunicação entre Tarefas em Java**

**📌 Introdução**

Este relato faz parte do processo avaliativo da disciplina de **Sistemas Operacionais** no curso superior de **Tecnologia em Análise e Desenvolvimento de Sistemas**, ofertado pela **Diretoria Acadêmica de Gestão e Tecnologia da Informação** do **Campus Natal-Central** do **Instituto Federal de Educação, Ciência e Tecnologia do Rio Grande do Norte (IFRN)**.

O objetivo principal deste trabalho é relatar e demonstrar as implementações de **comunicação entre tarefas** na linguagem Java.

**👥 Equipe de Desenvolvimento**

- Guilherme da Silva Cruz
- Maria Clara Batista Viana Silva
- José Vine Nunes Martins Araújo

**⚙️ Comunicação entre Tarefas em Java**

**💡 Informações Gerais**

A comunicação entre tarefas (*IPC - Inter-Process Communication* ou comunicação inter-threads) tem como objetivo permitir que diferentes fluxos de execução troquem dados, sincronizem suas operações e cooperem para resolver um problema em comum. Em arquiteturas baseadas no padrão **Produtor-Consumidor**, essa abordagem possibilita a separação de responsabilidades entre a geração de dados e seu processamento.

**🐳 Uso e Configuração do Docker**

O uso do **Docker** neste trabalho garante a padronização do ambiente de execução entre os membros da equipe e na avaliação do professor. Como o Java é executado sobre a JVM, o contêiner isola o ambiente do **JDK (Java Development Kit)**, garantindo que limitações de sistema operacional, variáveis de ambiente ou diferenças de versão não interfiram na criação e no agendamento de threads e processos.

A configuração básica do Docker utiliza uma imagem oficial do OpenJDK ([mcr.microsoft.com/devcontainers/java:3-25-trixie](https://mcr.microsoft.com/devcontainers/java:3-25-trixie)) executada via terminal ou orquestrada com docker-compose.yml.

**🛠️ Relato de Desenvolvimento e Implementações**

**🧵 1. Comunicação entre Tarefas no Mesmo Processo (Linhas de Execução)**

A comunicação entre tarefas no mesmo processo ocorre através de **Threads** que compartilham o mesmo espaço de endereçamento de memória. No código implementado, foi utilizada uma variável estática compartilhada (dados) onde a thread do Produtor escreve os números gerados e a thread do Consumidor lê esses dados para realizar a soma acumulada.

**💻 Código Implementado:**

Java
```
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProdutorConsumidor {
    // Lista compartilhada entre as Threads na memória do mesmo processo
    private static List<Integer> dados = new ArrayList<>();
    
    public static List<Integer> produzirDados(){
        System.out.println("# produzir - iniciado");
        
        List<Integer> novosDados = new ArrayList<>();
        Random random = new Random();
        
        for (int i = 0; i < 100; i++){
            novosDados.add(random.nextInt(111));
        }
        
        dados = novosDados;
        
        System.out.println("# produzir " + dados);
        System.out.println("# produzir - terminado");
        return dados;
    }
    
    public static void consumirDados(){
        System.out.println("### consumir - iniciado");
        System.out.println("### dados -> " + dados);
        
        int resultado = 0;
        for (int num : dados) {
            resultado += num;
        }
        
        System.out.println("### resultado -> " + resultado);
        System.out.println("### consumir - terminado");
    }
    
    public static void principal() {
        System.out.println("iniciou");
        
        // Criação das Threads associadas aos métodos da classe
        Thread threadProdutor = new Thread(ProdutorConsumidor::produzirDados);
        Thread threadConsumidor = new Thread(ProdutorConsumidor::consumirDados);
        
        // Sincronização explícita do fluxo com join()
        threadProdutor.start();
        try {
            threadProdutor.join();
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
            System.err.println("Thread interrompida: " + e.getMessage());
        }

        threadConsumidor.start();
        try {
            threadConsumidor.join();
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
            System.err.println("Thread interrompida: " + e.getMessage());
        }

        System.out.println("Finalizou");
    }

    public static void main(String[] args){
        principal();
    }
}
```

**📥 Execução do Código:**

O código foi compilado e executado utilizando o terminal com os comandos do Java via contêiner Docker:

Bash
```
javac ProdutorConsumidor.java
java ProdutorConsumidor
```
Exemplo de saída do terminal:

Terminal

```
iniciou
# produzir - iniciado
# produzir [102, 45, 12, 88, 3, 91, 55, ...]
# produzir - terminado
### consumir - iniciado
### dados -> [102, 45, 12, 88, 3, 91, 55, ...]
### resultado -> 5410
### consumir - terminado
Finalizou
```

**⚠️ Desafios Encontrados e Soluções:**

1. **Divergência entre o nome da classe e do arquivo:**
   *  **Problema:** Devido ao pouco costume com a sintaxe do Java, o nome da classe pública diferia do nome do arquivo salvo.
   *  **Solução:** A classe foi renomeada para seguir a convenção *PascalCase* do Java, garantindo que o nome do arquivo fosse exatamente ProdutorConsumidor.java.
2. **Dificuldade com o controle de chaves { } na sintaxe:**
   * **Problema:** Ao estruturar métodos e classes, ocorreram erros de compilação por conta do esquecimento de fechamento ou abertura incorreta de blocos.
   * **Solução:** Análise minuciosa das mensagens de erro emitidas pelo compilador Java no terminal para localizar as linhas afetadas e ajustar a identação.
3. **Condição de Corrida (*Race Condition*):**
   * **Problema:** Sem a sincronização adequada, a thread do consumidor iniciava antes que o produtor preenchesse a lista, gerando leitura de lista vazia ou inconsistente.
   * **Solução:** Uso do método .join(), que força a Thread principal a aguardar a finalização do produtor antes de iniciar a execução do consumidor.

**💻 2. Comunicação entre Tarefas em Processos Diferentes no Mesmo Computador**

Nesta etapa, o padrão é adaptado para executar a produção de dados de forma isolada do fluxo de execução sequencial de um único processo de aplicação. O programa primeiro chama a rotina de produção de forma modular para salvar o resultado antes de invocar o consumidor no processo principal.

**💻 Código Implementado:**

Java
```
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class sequencial {

    public static List<Integer> produzirDados(){
        List<Integer> dados = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 100; i++){
            dados.add(random.nextInt(111));
        }

        return dados;
    }

    public static void consumirDados(List<Integer> dados) {
        int resultado = 0;
        for (int num : dados){
            resultado += num;
        }

        System.out.println("recebeu -> " + resultado);
    }

    public static void principal(){
        System.out.println("iniciou");

        List<Integer> dados = produzirDados();
        consumirDados(dados);

        System.out.println("finalizou");
    }

    public static void main(String[] args){
        principal();
    }
}
```

**📥 Execução do Código:**

O código foi compilado e executado diretamente no terminal via Docker:

Bash
```
javac sequencial.java
java sequencial
```
Exemplo de saída do terminal:
Terminal
````
iniciou
recebeu -> 5621
finalizou
````

**⚠️ Desafios Encontrados e Soluções:**

1. **Escopo e Visibilidade de Dados:**
   * **Problema:** A ausência de uma variável estática global exigiu uma forma segura de transferir a referência da lista entre os contextos de produção e consumo.
   * **Solução:** Os dados foram encapsulados no retorno da função produzirDados() e repassados explicitamente como parâmetro de entrada no método consumirDados(dados).

**🌐 3. Comunicação entre Tarefas em Computadores/Contêineres Diferentes**

Para simular o esqueleto de tarefas modulares prontas para execução em nós de computação ou em contêineres Docker distintos, utilizou-se um modelo minimalista para o disparo e execução isolada da produção de dados.

**💻 Código Implementado:**

Java
```
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
```
docker build -t app-produtor .
docker run --rm app-produtor
```
Saída do terminal do contêiner:
Terminal
```
Dados produzidos
```

**⚠️ Desafios Encontrados e Soluções:**

1. **Problemas no Controle de Versão (Git Error / Permissão 403):**
   * **Problema:** Ao enviar as correções e documentações via git push, ocorreu o erro 403 (Permission Denied) devido a conflitos com credenciais salvas no sistema e falta de permissão direta de escrita no repositório.
   * **Solução:** Alinhamento com a equipe para que o proprietário do repositório enviasse o convite formal de colaboração no GitHub, além do ajuste nas credenciais locais do Git.

**🏁 Considerações Finais**

Todas as implementações propostas foram concluídas e executadas com sucesso. Através deste trabalho, a equipe compreendeu na prática os conceitos fundamentais de concorrência, o papel das *Threads* no compartilhamento de memória, o domínio da sintaxe do Java e a relevância dos mecanismos de sincronização (join) para evitar condições de corrida em **Sistemas Operacionais**.

Como recomendação para turmas futuras, sugere-se realizar testes avançados removendo propositalmente as travas de sincronização (join) para observar a não-determinação no escalonamento de threads pelo sistema operacional, além de adotar contêineres Docker desde o início da fase de desenvolvimento.

