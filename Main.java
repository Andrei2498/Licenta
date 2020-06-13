import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        int counter = 0;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss:SSS");
        LocalDateTime now = LocalDateTime.now();
        now = LocalDateTime.now();
        System.out.println(dtf.format(now));
        int numberOfGenerations = 500;
        int currentGenerationBestCost = 0;
        double joinRate = 0.1;
        double relocateRate = 0.1;
        GeneticAlg geneticAlg = new GeneticAlg("src\\m4n500s0.inp", 100);
        for(int i = 0; i < numberOfGenerations; i++) {
            geneticAlg.run(joinRate, relocateRate);
            Individual individual = geneticAlg.getBestIndividualInCurrentGeneration();
            Individual bestIndividual = geneticAlg.getBestIndividual();
            System.out.println("Generatia: " + i + " cu costul: " + individual.getTotalCost() + " , cel mai bun: " + bestIndividual.getTotalCost());
            if(currentGenerationBestCost == 0)
                currentGenerationBestCost = individual.getTotalCost();
            else if(currentGenerationBestCost == individual.getTotalCost())
                counter++;
            else {
                counter = 0;
                currentGenerationBestCost = individual.getTotalCost();
            }

            if(counter == 25 && relocateRate < 0.5 && joinRate < 0.5){
                joinRate += 0.1;
                relocateRate += 0.1;
                counter = 0;
            }
            if(counter == 100)
                break;
        }
        System.out.println(joinRate + " " + relocateRate);
        now = LocalDateTime.now();
        System.out.println();
        System.out.println(now);


    }
}
