import java.util.ArrayList; //importado para criar e manipular listas dinâmicas
import java.util.List;  //importado para criar e manipular listas dinâmicas
import java.util.Random; //classe para gerar números aleatórios

public class sequencial {

    public static List<Integer> produzirDados(){
        
        List<Integer> dados = new ArrayList<>();    //criação de nova lista chamada 'dados'
        Random random = new Random();   //gerador de números aleatórios chamado 'random'

        for (int i = 0; i < 100; i++){  //laço para a criação da lista 
            dados.add(random.nextInt(111)); //geração e adição do número aleatório entre 0 e 110 na lista
        }
        return dados;   //retorno da lista completa
    }

    public static void consumirDados(List<Integer> dados) {   //recebimento da lista dados
        int resultado = 0;  //variável que acumulará a soma dos números da lista
        for (int num : dados){  //laço for-each para ser lido cada número da lista
            resultado += num;
        }

        System.out.println("Recebeu -> " + resultado);
    }
    public static void principal(){
        System.out.println("Iniciou");

        List<Integer> dados = produzirDados();  //faz a execução da produção dos dados e armazenamento na variável
        consumirDados(dados);   //recebe a lista de dados e calcula a soma dos inteiros

        System.out.println("Finalizou");
    }

    public static void main(String[] args){ //onde entra a aplicação e chama o método principal()
        principal();
    }
}