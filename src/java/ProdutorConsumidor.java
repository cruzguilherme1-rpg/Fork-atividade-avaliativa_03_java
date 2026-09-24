import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProdutorConsumidor{
    //lista compartilhada entre as Threads
    private static List<Integer> dados = new ArrayList<>();
    
    //gera os 100 números aleatórios mas a lista é atribuída na variável estática 'dados'
    
    public static List<Integer> produzirDados(){
        System.out.println("# Produzir - iniciado");
        
        List<Integer> novosDados = new ArrayList<>(); //nova lista vazia na memória para armazenar números
        Random random = new Random(); //gerador de números aleatórios.
        
        for (int i = 0; i < 100; i++){
            novosDados.add(random.nextInt(111)); //aqui é onde ele realmente gera os valores 
        }
        
        dados = novosDados;
        
        System.out.println("# produzir " + dados);
        System.out.println("# produzir - terminado");
        return dados;
    }
    
    public static void consumirDados(){
        System.out.println("### Consumir - iniciado");
        System.out.println("### Dados -> " + dados);
        //acesso direto da variável compartilhada 'dados'
        int resultado = 0;
        for (int num : dados) {
            resultado += num;
        }
        
        System.out.println("### Resultado -> " + resultado);
        System.out.println("### Consumir - terminado");
    }
    
    public static void principal() {
        System.out.println("iniciou");
        
        //criação das Threads
        //uso da sintaxe(Classe::metodo) para passar os métodos estáticos como tarefas executáveis
        Thread threadProdutor = new Thread(ProdutorConsumidor::produzirDados);
        Thread threadConsumidor = new Thread(ProdutorConsumidor::consumirDados);
        
        //inicialização da thread do produtor
        threadProdutor.start();
        try{
            threadProdutor.join();  //faz com que a thread principal pare e espere a threadProdutor termine para avançar para a próxima linha.
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
            System.err.println("Thread interrompida: " + e.getMessage());
        }
        threadConsumidor.start();
        //espera a thread do consumidor acabar antes de finalizar
        try{
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

