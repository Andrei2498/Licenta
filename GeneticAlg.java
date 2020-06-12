
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class GeneticAlg {

    private ArrayList<ArrayList<Integer>> representationOutMatrix;
    private ArrayList<ArrayList<Integer>> representationInMatrix;
    private Population population;
    private FileIO fileIO;
    private int[][] costMatrix;
    private int populationSize;
    private Random random = new Random();

    public GeneticAlg(String filename, int populationSize){
        this.fileIO = new FileIO(filename);
        this.population = new Population(populationSize, fileIO);
        this.populationSize = populationSize;
        this.costMatrix = population.getIndividuals()[0].costMatrix;
        this.representationOutMatrix = population.getIndividuals()[0].representationOutMatrixAux;
        this.representationInMatrix = population.getIndividuals()[0].representationInMatrix;
    }

    public void run(double joinRate, double relocateRate){

        population.calculatePopulationFitness();
        selection();

        for(int i = 0; i < population.getIndividuals().length; i++){
            join(i, joinRate);
            population.getIndividuals()[i].calculateTotalCost();
        }

        for(int i = 0; i < population.getIndividuals().length; i++) {
            relocate(i, relocateRate);
            population.getIndividuals()[i].calculateTotalCost();
        }

    }

    public Individual getBestIndividual(){
        int cost = Integer.MAX_VALUE;
        int index = 0;
        for(int i = 0; i < population.getIndividuals().length; i++)
            if(population.getIndividuals()[i].totalCost < cost) {
                cost = population.getIndividuals()[i].totalCost;
                index = i;
            }
        return population.getIndividuals()[index];
    }

    private void selection(){
        Individual[] newIndividuals = new Individual[populationSize];
        Individual[] populationIndividuals = population.getIndividuals();
        Random random = new Random();
        double[] fitnessList = population.getFitnessList();
        double randomNumber;
        int populationIndex;
        int indexIndividuals = 0;


        while(indexIndividuals != populationSize){
            randomNumber = random.nextDouble();
            populationIndex = random.nextInt(populationSize);

            if(fitnessList[populationIndex] >= randomNumber) {
                newIndividuals[indexIndividuals] = new Individual(populationIndividuals[populationIndex]);
                indexIndividuals ++;
            }
        }

        population.changeIndividualsVector(newIndividuals);
    }

    private void join(int individualIndex, double rate){

        ArrayList<ArrayList<Triplet>> routes = new ArrayList<>();
        ArrayList<ArrayList<Triplet>> routesMatrix = population.getIndividuals()[individualIndex].routesMatrix;
        ArrayList<Triplet> newRoute;

        for(int i = 0; i < routesMatrix.size(); i++){
            if(rate >= random.nextDouble()){
                routes.add(routesMatrix.get(i));
            }
        }

        if(routes.size() % 2 != 0) {
            routes.remove(routes.size() - 1);
        }

        routes.sort(Comparator.comparing(ArrayList::size));

        if(routes.size() > 0){
            for(int i = 0; i < routes.size(); i += 2){
                newRoute = attemptUnification(routes.get(i), routes.get(i + 1), population.getIndividuals()[individualIndex]);

                if(newRoute.size() != 0){
                    routesMatrix.remove(routes.get(i));
                    routesMatrix.remove(routes.get(i + 1));
                    routesMatrix.add(newRoute);
                }
            }
        }

    }

    private ArrayList<Triplet> attemptUnification(ArrayList<Triplet> firstRoute, ArrayList<Triplet> secondRoute, Individual individual){
        ArrayList<Triplet> route = new ArrayList<>();
        ArrayList<Integer> first = new ArrayList<>();
        ArrayList<Integer> second = new ArrayList<>();
        ArrayList<Integer> combined;
        int depotIndex;
        int index;

        for(int i = 0; i < firstRoute.size() - 1; i++)
            first.add(firstRoute.get(i).third);

        for(int j = 0; j < secondRoute.size() - 1; j++)
            second.add(secondRoute.get(j).third);

        combined = uniteTwoRoutes(first, second);
        if(combined.size() != first.size() + second.size())
            combined = uniteTwoRoutes(second, first);

        if(combined.size() == first.size() + second.size()) {

            if(costMatrix[firstRoute.get(0).first][combined.get(0) + fileIO.getDepots()] +
                    costMatrix[combined.get(combined.size() - 1) + fileIO.getDepots()][firstRoute.get(0).first]
               < costMatrix[secondRoute.get(0).first][combined.get(0) + fileIO.getDepots()] +
                    costMatrix[combined.get(combined.size() - 1) + fileIO.getDepots()][secondRoute.get(0).first]) {

                depotIndex = firstRoute.get(0).first;
                index = secondRoute.get(0).first;
            }
            else{
                depotIndex = secondRoute.get(0).first;
                index = firstRoute.get(0).first;
            }

            individual.depotsUsage.set(index, individual.depotsUsage.get(index) - 1);
            if(individual.depotsUsage.get(index) == fileIO.getNrVehicles()[index] - 1 && !individual.depotsAvailable.contains(index)){
                individual.depotsAvailable.add(index);
            }

            route.add(new Triplet(depotIndex, depotIndex, combined.get(0)));
            for (int i = 1; i < combined.size(); i++) {
                route.add(new Triplet(depotIndex, combined.get(i - 1), combined.get(i)));
            }
            route.add(new Triplet(depotIndex, combined.get(combined.size() - 1), depotIndex));

        }

        return route;
    }

    private ArrayList<Integer> uniteTwoRoutes(ArrayList<Integer> route1, ArrayList<Integer> route2){
        ArrayList<Integer> newRoute = new ArrayList<>();
        int index = 0;


        while (representationInMatrix.get(route1.get(0)).contains(route2.get(index))) {
            newRoute.add(route2.get(index));
            index++;
            if(index == route2.size())
                break;
        }
        newRoute.add(route1.get(0));

        for(int j = 1; j < route1.size(); j++) {
            try {
                while (representationOutMatrix.get(newRoute.get(newRoute.size() - 1)).contains(route2.get(index)) &&
                        representationOutMatrix.get(route2.get(index)).contains(route1.get(j))) {
                    newRoute.add(route2.get(index));
                    index++;
                    if (index == route2.size())
                        break;
                }
                newRoute.add(route1.get(j));
            }catch (Exception e){
                System.out.println(newRoute.get(newRoute.size() - 1));
                System.out.println(route2.get(index));
            }
        }

        if(index != route2.size()) {
            while (representationOutMatrix.get(route1.get(route1.size() - 1)).contains(route2.get(index))) {
                newRoute.add(route2.get(index));
                index++;
                if (index == route2.size())
                    break;
            }
        }

        return newRoute;
    }

    private void relocate(int individualIndex, double rate){
        ArrayList<ArrayList<Triplet>> routesMatrix = population.getIndividuals()[individualIndex].routesMatrix;
        int result;

        for(int i = 0; i < routesMatrix.size(); i++){
            if(random.nextDouble() <= rate) {
                result = checkIfChangePossible(routesMatrix.get(i), population.getIndividuals()[individualIndex]);
                if (result != -1) {

                    changeBusesAvailability(routesMatrix.get(i).get(0).first, result, population.getIndividuals()[individualIndex]);
                    for (int j = 0; j < routesMatrix.get(i).size(); j++)
                        routesMatrix.get(i).get(j).first = result;
                    routesMatrix.get(i).get(0).second = result;
                    routesMatrix.get(i).get(routesMatrix.get(i).size() - 1).third = result;

                }
            }
        }

        population.getIndividuals()[individualIndex].routesMatrix = routesMatrix;
    }

    private int checkIfChangePossible(ArrayList<Triplet> route, Individual individual){
        int costNow, costAfter;
        int aux = Integer.MAX_VALUE;
        int indexNew = -1;
        costNow = costMatrix[route.get(0).second][route.get(0).third + fileIO.getDepots()] +
                  costMatrix[route.get(route.size() - 1).second + fileIO.getDepots()][route.get(route.size() - 1).third];
        for(int i = 0; i < individual.depotsAvailable.size(); i++){
            costAfter = costMatrix[individual.depotsAvailable.get(i)][route.get(0).third + fileIO.getDepots()] +
                        costMatrix[route.get(route.size() - 1).second + fileIO.getDepots()][individual.depotsAvailable.get(i)];
            if(costNow > costAfter && costAfter  < aux){
                aux = costAfter;
                indexNew = individual.depotsAvailable.get(i);
            }
        }
        return indexNew;
    }

    private void changeBusesAvailability(int formerDepot, int curentDepot, Individual individual){
        individual.depotsUsage.set(formerDepot, individual.depotsUsage.get(formerDepot) - 1);
        individual.depotsUsage.set(curentDepot, individual.depotsUsage.get(curentDepot) + 1);
        if(individual.depotsUsage.get(formerDepot) == fileIO.getNrVehicles()[formerDepot] - 1 && !individual.depotsAvailable.contains(formerDepot))
            individual.depotsAvailable.add(formerDepot);
        if(individual.depotsUsage.get(curentDepot) == fileIO.getNrVehicles()[curentDepot])
            individual.depotsAvailable.remove(new Integer(curentDepot));
    }


}
