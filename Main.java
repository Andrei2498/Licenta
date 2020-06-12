import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss:SSS");
        LocalDateTime now = LocalDateTime.now();
        now = LocalDateTime.now();
        System.out.println(dtf.format(now));
        int numberOfGenerations = 1000;
        GeneticAlg geneticAlg = new GeneticAlg("src\\m4n500s0.inp", 50);
        for(int i = 0; i < numberOfGenerations; i++) {
            geneticAlg.run(0.5, 0.5);
            Individual individual = geneticAlg.getBestIndividual();
            System.out.println("Generatia: " + i + " cu costul: " + individual.getTotalCost());
        }
        now = LocalDateTime.now();
        System.out.println();
        System.out.println(now);


    }
}
