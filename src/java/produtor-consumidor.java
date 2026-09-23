import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main{
    //lista compartilhada entre as Threads
    private static List<Integer> dados = new ArrayList<>();
    
    public static List<Integer> produzirDados(){
        System.out.println("# produzir - iniciado");
        
        List<Integer> novosDados = new ArrayList<>(); //nova lista vazia na memória para armazenar números
        Random random = new Random(); //gerador de números aleatórios.
        
        for (int i = 0; i < 100; i++){
            novosDados.add(random.nextInt(111)); //aqui é onde ele realmente gera os valores 
        }
        
        dados = novosDados;
        
        System.out.println("#pruduzir " + dados);
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
        
        //criação das Threads
        Thread threadProdutor = new Thread(Main::produzirDados);
        Thread threadConsumidor = new Thread(Main::consumirDados);
        
        //inicialização das Threads
        threadProdutor.start();
        threadConsumidor.start();
        
        System.out.println("finalizou");
    }
    
    public static void main(){
        principal();
    }
}