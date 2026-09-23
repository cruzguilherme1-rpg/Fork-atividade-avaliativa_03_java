import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static List<Integer> produzirDados(){
        
        List<Integer> dados = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 100; i++){
            dados.add(random.nextInt(111));
        }

        return dados;
    }

    public static void consumirDados(List<Integer> dados) {
        
        //resultado do sum(dados)
        int resultado = 0;
        for (int num : dados){
            resultado += num;
        }

        System.out.println("recebeu -> " + resultado)
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