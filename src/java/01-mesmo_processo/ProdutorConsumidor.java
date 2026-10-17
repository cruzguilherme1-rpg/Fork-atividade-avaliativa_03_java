import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProdutorConsumidor{
    private static List<Integer> dados = new ArrayList<>();
    
    //gera os 100 números aleatórios mas a lista é atribuída na variável estática 'dados'
    public static List<Integer> produzirDados(){
        System.out.println("# Produzir - iniciado");
        
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
        
        threadProdutor.start();
        
        threadConsumidor.start();

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

