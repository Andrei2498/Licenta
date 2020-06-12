
public class Population {

    private int populationSize;
    private Individual[] individuals;
    private double[] fitnessList;
    private int[] costList;
    private double maxCost = Integer.MIN_VALUE;
    private double minCost = Integer.MAX_VALUE;

    public Population(int populationSize, FileIO fileIO){
        initializePopulation(populationSize, fileIO);
    }

    private void initializePopulation(int populationSize, FileIO fileIO){

        this.populationSize = populationSize;
        individuals = new Individual[populationSize];
        fitnessList = new double[populationSize];
        costList = new int[populationSize];
        int counter = 0;

        while (counter < populationSize){
            try {
                individuals[counter] = new Individual(fileIO);
                counter++;
            } catch (Exception e){
                continue;
            }
        }
    }

    public void calculatePopulationFitness(){
        for(int i = 0; i < populationSize; i++){
            costList[i] = individuals[i].getTotalCost();
            if(costList[i] > maxCost)
                maxCost = costList[i];
            if(costList[i] < minCost)
                minCost = costList[i];
        }

        maxCost = 1.001 * maxCost;
        minCost = 0.999 * minCost;

        for(int i = 0; i < populationSize; i++){
            fitnessList[i] = 1 - (costList[i] - minCost)/(maxCost - minCost);
        }

    }

    public void changeIndividualsVector(Individual[] individuals){
        this.individuals = individuals;
    }

    public double[] getFitnessList(){
        return this.fitnessList;
    }

    public Individual[] getIndividuals(){
        return this.individuals;
    }
}
