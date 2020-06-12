
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Individual {

    ArrayList<ArrayList<Integer>> representationOutMatrix = new ArrayList<>();
    ArrayList<ArrayList<Integer>> representationOutMatrixAux = new ArrayList<>();
    ArrayList<ArrayList<Integer>> representationInMatrix = new ArrayList<>();
    ArrayList<ArrayList<Triplet>> routesMatrix = new ArrayList<>();
    ArrayList<Integer> depotsUsage = new ArrayList<>();
    ArrayList<Integer> depotsAvailable = new ArrayList<>();
    ArrayList<Integer> tripsUsage = new ArrayList<>();
    FileIO fileIO;
    int[][] costMatrix;
    int totalCost = 0;

    Individual(FileIO fileIO) {
        this.fileIO = fileIO;
        ArrayList<Integer> lista = new ArrayList<>();
        costMatrix = fileIO.getCost();
        init();
        for(int i = 0; i < representationOutMatrix.size(); i++){
            for(int j = 0; j < representationOutMatrix.get(i).size(); j++)
                lista.add(representationOutMatrix.get(i).get(j));
            representationOutMatrixAux.add(lista);
            lista = new ArrayList<>();
        }
        generateGenes();
        calculateTotalCost();
    }

    Individual(Individual individual){
        this.representationOutMatrix = individual.representationOutMatrix;
        this.representationInMatrix = individual.representationInMatrix;
        this.routesMatrix = new ArrayList<>(individual.routesMatrix);
        this.depotsUsage = new ArrayList<>(individual.depotsUsage);
        this.depotsAvailable = new ArrayList<>(individual.depotsAvailable);
        this.tripsUsage = new ArrayList<>(individual.tripsUsage);
        this.fileIO = individual.fileIO;
        this.costMatrix = individual.costMatrix;
        this.totalCost = individual.totalCost;
    }

    private void init() {
        for (int i = 0; i < fileIO.getDepots(); i++) {
            depotsUsage.add(0);
            depotsAvailable.add(i);
        }

        for (int i = 0; i < fileIO.getTrips(); i++) {
            tripsUsage.add(i);
        }

        for (int i = 0; i < fileIO.getTrips(); i++) {
            representationInMatrix.add(new ArrayList<>());
            representationOutMatrix.add(new ArrayList<>());
        }

        for (int i = 0; i < fileIO.getTrips(); i++) {
            for (int j = 0; j < fileIO.getTrips(); j++) {
                if (costMatrix[i + fileIO.getDepots()][j + fileIO.getDepots()] != -1) {
                    representationOutMatrix.get(i).add(j);
                    representationInMatrix.get(j).add(i);
                }
            }
        }
    }

    private void generateGenes() {
        Random random = new Random();
        int routeIndex;
        int depotIndex;
        int tripIndex;
        int auxIndex;
        int emptyArrayCounter = 0;

        while (tripsUsage.size() > 0) {

            //Add new route
            routesMatrix.add(new ArrayList<>());
            routeIndex = routesMatrix.size() - 1;

            //Get Depot Index
            depotIndex = getRandomDepot(random);

            //Get first trip in route
            tripIndex = tripsUsage.get(random.nextInt(tripsUsage.size()));
            while (representationOutMatrix.get(tripIndex).size() == 0 && emptyArrayCounter < 5) {
                tripIndex = tripsUsage.get(random.nextInt(tripsUsage.size()));
                emptyArrayCounter++;
            }
            if (emptyArrayCounter < 5)
                emptyArrayCounter = 0;
            tripsUsage.remove(new Integer(tripIndex));

            //Set depot and first trip
            routesMatrix.get(routeIndex).add(new Triplet(depotIndex, depotIndex, tripIndex));

            while (representationOutMatrix.get(tripIndex).size() > 0) {

                for (int i = 0; i < representationInMatrix.get(tripIndex).size(); i++)
                    representationOutMatrix.get(representationInMatrix.get(tripIndex).get(i)).remove(new Integer(tripIndex));

                auxIndex = tripIndex;
                tripIndex = representationOutMatrix.get(tripIndex).get(random.nextInt(representationOutMatrix.get(tripIndex).size()));
                routesMatrix.get(routeIndex).add(new Triplet(depotIndex, auxIndex, tripIndex));
                tripsUsage.remove(new Integer(tripIndex));

            }
            for (int i = 0; i < representationInMatrix.get(tripIndex).size(); i++)
                representationOutMatrix.get(representationInMatrix.get(tripIndex).get(i)).remove(new Integer(tripIndex));

            routesMatrix.get(routeIndex).add(new Triplet(depotIndex, tripIndex, depotIndex));
        }
    }

    private int getRandomDepot(Random random) {
        int value = depotsAvailable.get(random.nextInt(depotsAvailable.size()));
        depotsUsage.set(value, depotsUsage.get(value) + 1);
        if (depotsUsage.get(value) == fileIO.getNrVehicles()[value])
            depotsAvailable.remove(new Integer(value));
        return value;
    }

    public void calculateTotalCost(){
        int cost = 0;
        for(ArrayList<Triplet> list : routesMatrix){
            for(int i = 0; i < list.size(); i ++){
                if(i == 0)
                    cost += costMatrix[list.get(i).second][list.get(i).third + fileIO.getDepots()];
                else if(i == list.size() - 1)
                    cost += costMatrix[list.get(i).second + fileIO.getDepots()][list.get(i).third];
                else
                    cost += costMatrix[list.get(i).second + fileIO.getDepots()][list.get(i).third + fileIO.getDepots()];
            }
        }
        totalCost = cost;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public ArrayList<ArrayList<Triplet>> getRoutesMatrix(){
        return routesMatrix;
    }
}
