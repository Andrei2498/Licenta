
import java.util.ArrayList;
import java.util.Random;

public class ReprezentareB {

    private int totalCost = 0;
    private int[][][] genes;
    private int i, j, k;
    private FileIO fileIO;
    private ArrayList<Triplet> depotOutTriplets = new ArrayList<>();
    private ArrayList<Triplet> depotInTriplets = new ArrayList<>();
    private ArrayList<Triplet> tripsTriplets = new ArrayList<>();
    private int[] busesOutCounter;
    private int[] busesInCounter;
    private int[] indexOut;
    private int[] indexIn;
    private int[][] cost;

    public ReprezentareB(FileIO fileIO){

        this.genes = new int[fileIO.getDepots()][fileIO.getDepots() + fileIO.getTrips()][fileIO.getDepots() + fileIO.getTrips()];
        this.fileIO = fileIO;
        this.busesOutCounter = new int[fileIO.getDepots()];
        this.busesInCounter = new int[fileIO.getDepots()];
        this.indexOut = new int[fileIO.getDepots()];
        this.indexIn = new int[fileIO.getDepots()];
        this.cost = fileIO.getCost();
        generateGenes();
        calcTotalCost();

    }

    private void generateGenes() {

        Random random = new Random();
        Triplet triplet;
        ArrayList<Triplet> forRemove = new ArrayList<>();

        for (k = 0; k < fileIO.getDepots(); k++) {
            indexIn[k] = depotInTriplets.size();
            for (i = 0; i < fileIO.getDepots() + fileIO.getTrips(); i++) {
                for (j = 0; j < fileIO.getDepots(); j++) {
                    if (k == j && fileIO.getCost()[i][j] != -1)
                        depotInTriplets.add(new Triplet(k, i, j));
                    else
                        genes[k][i][j] = -1;
                }
            }

            indexOut[k] = depotOutTriplets.size();
            for (i = 0; i < fileIO.getDepots(); i++) {
                for (j = 0; j < fileIO.getDepots() + fileIO.getTrips(); j++) {
                    if (k == i && fileIO.getCost()[i][j] != -1)
                        depotOutTriplets.add(new Triplet(k, i, j));
                    else
                        genes[k][i][j] = -1;
                }
            }
        }

        for (i = fileIO.getDepots(); i < fileIO.getDepots() + fileIO.getTrips(); i++) {
            for (j = fileIO.getDepots(); j < fileIO.getDepots() + fileIO.getTrips(); j++) {
                if (fileIO.getCost()[i][j] != -1)
                    tripsTriplets.add(new Triplet(0, i, j));
                else
                    for (k = 0; k < fileIO.getDepots(); k++)
                        genes[k][i][j] = -1;
            }
        }


        while (depotOutTriplets.size() > 0 || depotInTriplets.size() > 0 || tripsTriplets.size() > 0) {
            triplet = getRandomTriplet(depotOutTriplets, depotInTriplets, tripsTriplets, random);

            if (triplet.first == triplet.second && depotOutTriplets.size() > 0 && depotInTriplets.size() > 0) {
                genes[triplet.first][triplet.second][triplet.third] = 1;
                depotOutTriplets.remove(triplet);
                removeColumn(triplet.third);
                busesOutCounter[triplet.first]++;
                if(triplet.first != fileIO.getDepots() - 1){
                    for(k = triplet.first + 1; k < fileIO.getDepots(); k++)
                        indexOut[k] --;
                }

                triplet = depotInTriplets.get(indexIn[triplet.first]);
                genes[triplet.first][triplet.second][triplet.third] = 1;
                depotInTriplets.remove(triplet);
                removeLine(triplet.second);
                if(triplet.first != fileIO.getDepots() - 1)
                    for(k = triplet.first + 1; k < fileIO.getDepots(); k++)
                        indexIn[k] --;
                busesInCounter[triplet.first]++;

//               AFISARE IN-OUT
//                for(i = 0; i < fileIO.getDepots(); i++){
//                    System.out.print("[" + busesOutCounter[i] + "," + busesInCounter[i] + "]" + " ");
//                }
//                System.out.println();
//                System.out.println("Size out: " + depotOutTriplets.size());
//                System.out.println("Size in: " + depotInTriplets.size());

            } else if (triplet.first == triplet.third && depotInTriplets.size() > 0 && depotOutTriplets.size() > 0) {
                genes[triplet.first][triplet.second][triplet.third] = 1;
                depotInTriplets.remove(triplet);
                removeLine(triplet.second);
                busesInCounter[triplet.first]++;
                if(triplet.first != fileIO.getDepots() - 1){
                    for(k = triplet.first + 1; k < fileIO.getDepots(); k++)
                        indexIn[k] --;
                }

                triplet = depotOutTriplets.get(indexOut[triplet.first]);
                genes[triplet.first][triplet.second][triplet.third] = 1;
                depotOutTriplets.remove(triplet);
                removeColumn(triplet.third);
                if(triplet.first != fileIO.getDepots() - 1)
                    for(k = triplet.first + 1; k < fileIO.getDepots(); k++)
                        indexOut[k] --;
                busesOutCounter[triplet.first]++;

                // AFISARE IN-OUT
                //                for(i = 0; i < fileIO.getDepots(); i++){
//                    System.out.print("[" + busesOutCounter[i] + "," + busesInCounter[i] + "]" + " ");
//                }
//                System.out.println();
//                System.out.println("Size out: " + depotOutTriplets.size());
//                System.out.println("Size in: " + depotInTriplets.size());

            } else {
                for (k = 0; k < fileIO.getDepots(); k++) {
                    genes[k][triplet.second][triplet.third] = 1;
                }
                tripsTriplets.remove(triplet);
                removeLine(triplet.second);
                removeColumn(triplet.third);
            }

            for (k = 0; k < fileIO.getDepots(); k++) {
                if (busesOutCounter[k] == fileIO.getNrVehicles()[k]) {
                    for (i = 0; i < depotOutTriplets.size(); i++) {
                        if (depotOutTriplets.get(i).first == k) {
                            genes[k][k][depotOutTriplets.get(i).third] = -1;
                            forRemove.add(depotOutTriplets.get(i));
                            if (k != indexOut.length - 1)
                                for(j = k + 1; j < fileIO.getDepots(); j++)
                                    indexOut[j]--;
                        }
                    }
                    for (i = 0; i < forRemove.size(); i++)
                        depotOutTriplets.remove(forRemove.get(i));
                }
                if (busesInCounter[k] == fileIO.getNrVehicles()[k]) {
                    for (i = 0; i < depotInTriplets.size(); i++) {
                        if (depotInTriplets.get(i).first == k) {
                            genes[k][depotInTriplets.get(i).second][k] = -1;
                            forRemove.add(depotInTriplets.get(i));
                            if (k != indexIn.length - 1)
                                for(j = k + 1; j < fileIO.getDepots(); j++)
                                    indexIn[j]--;
                        }
                    }
                    for (i = 0; i < forRemove.size(); i++)
                        depotInTriplets.remove(forRemove.get(i));
                }
                forRemove.clear();
            }

        }
    }

    private Triplet getRandomTriplet(ArrayList<Triplet> list1, ArrayList<Triplet> list2, ArrayList<Triplet> list3, Random random){
        int index = random.nextInt(list1.size() + list2.size() + list3.size());
        if(index >= list1.size() + list2.size() || list1.size() + list2.size() == 0){
            return list3.get(index - (list1.size() + list2.size()));
        }
        else if(index >= list1.size()){
            return list2.get(index - list1.size());
        }
        else return list1.get(index);
    }

    private void removeColumn(int column){
        ArrayList<Triplet> listForRemove = new ArrayList<>();

        for(Triplet triplet1: depotOutTriplets){
            if(triplet1.third == column){
                genes[triplet1.first][triplet1.second][triplet1.third] = -1;
                listForRemove.add(triplet1);
                if(triplet1.first != fileIO.getDepots() - 1)
                    for(k = triplet1.first + 1; k < fileIO.getDepots(); k++)
                        indexOut[k] --;
            }
        }
        for(i = 0; i < listForRemove.size(); i++)
            depotOutTriplets.remove(listForRemove.get(i));
        listForRemove.clear();

        for(Triplet triplet1: tripsTriplets){
            if(triplet1.third == column){
                listForRemove.add(triplet1);
                for(k = 0; k < fileIO.getDepots(); k++)
                    genes[k][triplet1.second][triplet1.third] = -1;
            }
        }
        for(i = 0; i < listForRemove.size(); i++)
            tripsTriplets.remove(listForRemove.get(i));
        listForRemove.clear();
    }

    private void removeLine(int line){
        ArrayList<Triplet> listForRemove = new ArrayList<>();

        for(Triplet triplet1: depotInTriplets){
            if(triplet1.second == line){
                genes[triplet1.first][triplet1.second][triplet1.third] = -1;
                listForRemove.add(triplet1);
                if(triplet1.first != fileIO.getDepots() - 1) {
                    for(k = triplet1.first + 1; k < fileIO.getDepots(); k++)
                        indexIn[k]--;
                }
            }
        }
        for(i = 0; i < listForRemove.size(); i++)
            depotInTriplets.remove(listForRemove.get(i));
        listForRemove.clear();

        for(Triplet triplet1: tripsTriplets){
            if(triplet1.second == line){
                listForRemove.add(triplet1);
                for(k = 0; k < fileIO.getDepots(); k++)
                    genes[k][triplet1.second][triplet1.third] = -1;
            }
        }
        for(i = 0; i < listForRemove.size(); i++)
            tripsTriplets.remove(listForRemove.get(i));
        listForRemove.clear();
    }

    private void calcTotalCost(){
        for(k = 0; k < fileIO.getDepots(); k++){
            for(j = fileIO.getDepots(); j < fileIO.getDepots() + fileIO.getTrips(); j++){
                if(genes[k][k][j] == 1)
                    totalCost += cost[k][j];
            }
            for(i = fileIO.getDepots(); i < fileIO.getDepots() + fileIO.getTrips(); i++){
                if(genes[k][i][k] == 1)
                    totalCost += cost[i][k];
            }
        }
        for(i = fileIO.getDepots(); i < fileIO.getDepots() + fileIO.getTrips(); i++){
            for(j = fileIO.getDepots(); j < fileIO.getDepots() + fileIO.getTrips(); j++){
                if(genes[0][i][j] == 1)
                    totalCost += cost[i][j];
            }
        }
    }

    public int[][][] getGenes(){
        return this.genes;
    }

    public int getTotalCost(){
        return this.totalCost;
    }







//    private void shuffle(int individualIndex, double rate){
//
////        System.out.println(population.getIndividuals()[individualIndex].routesMatrix);
//
//        ArrayList<ArrayList<Triplet>> routesMatrix = population.getIndividuals()[individualIndex].routesMatrix;
//        ArrayList<Triplet> positionsToBeReseted = new ArrayList<>();
//        int aux;
//
//        for(ArrayList<Triplet> route: routesMatrix){
//            if( rate >= random.nextDouble() && route.size() > 2){
//
//                for(int j = 0; j < route.size() - 2; j++){
//
//                    if(checkIfShufflePossible(j, route))
//                        if(checkIfCostIsSmaller(j, route)){
//                            aux = route.get(j).third;
//                            route.get(j).third = route.get(j + 1).third;
//                            route.get(j + 1).second = route.get(j + 1).third;
//                            route.get(j + 1).third = aux;
//                            route.get(j + 2).second = aux;
//                        }
//                }
//            }
//        }
//    }
//
//    private boolean checkIfShufflePossible(int index, ArrayList<Triplet> route){
//
//        if(route.size() == 3){
//            return representationOutMatrix.get(route.get(1).third).contains(route.get(1).second);
//        }
//        else if(index == 0){
//            if(representationOutMatrix.get(route.get(1).third).contains(route.get(1).second))
//                return representationOutMatrix.get(route.get(1).second).contains(route.get(2).third);
//        }
//        else if(index == route.size() - 2){
//            if(representationOutMatrix.get(route.get(index).second).contains(route.get(index + 1).third))
//                return representationOutMatrix.get(route.get(index + 1).third).contains(route.get(index + 1).second);
//        }
//        else {
//            if(representationOutMatrix.get(route.get(index).second).contains(route.get(index + 1).third))
//                if(representationOutMatrix.get(route.get(index + 1).third).contains(route.get(index + 1).second))
//                    return representationOutMatrix.get(route.get(index + 1).second).contains(route.get(index + 2).third);
//        }
//
//        return false;
//    }
//
//    private boolean checkIfCostIsSmaller(int index, ArrayList<Triplet> route){
//        int costNow, costAfter;
//
//        if(route.size() == 3){
//            costNow = costMatrix[route.get(0).second][route.get(0).third + fileIO.getDepots()] +
//                    costMatrix[route.get(1).second + fileIO.getDepots()][route.get(1).third + fileIO.getDepots()] +
//                    costMatrix[route.get(2).second + fileIO.getDepots()][route.get(2).third];
//            costAfter = costMatrix[route.get(0).second][route.get(1).third + fileIO.getDepots()] +
//                    costMatrix[route.get(1).third + fileIO.getDepots()][route.get(1).second + fileIO.getDepots()] +
//                    costMatrix[route.get(1).second + fileIO.getDepots()][route.get(2).third];
//        }
//        else if(index == 0){
//            costNow = costMatrix[route.get(0).second][route.get(0).third + fileIO.getDepots()] +
//                    costMatrix[route.get(1).second + fileIO.getDepots()][route.get(1).third + fileIO.getDepots()] +
//                    costMatrix[route.get(2).second + fileIO.getDepots()][route.get(2).third + fileIO.getDepots()];
//            costAfter = costMatrix[route.get(0).second][route.get(1).third + fileIO.getDepots()] +
//                    costMatrix[route.get(1).third + fileIO.getDepots()][route.get(1).second + fileIO.getDepots()] +
//                    costMatrix[route.get(1).second + fileIO.getDepots()][route.get(2).third + fileIO.getDepots()];
//        }
//        else if(index == route.size() - 2){
//            costNow = costMatrix[route.get(index).second + fileIO.getDepots()][route.get(index).third + fileIO.getDepots()] +
//                    costMatrix[route.get(index + 1).second + fileIO.getDepots()][route.get(index + 1).third + fileIO.getDepots()] +
//                    costMatrix[route.get(index + 2).second + fileIO.getDepots()][route.get(index + 2).third];
//            costAfter = costMatrix[route.get(index).second + fileIO.getDepots()][route.get(index + 1).third + fileIO.getDepots()] +
//                    costMatrix[route.get(index + 1).third + fileIO.getDepots()][route.get(index + 1).second + fileIO.getDepots()] +
//                    costMatrix[route.get(index + 1).second + fileIO.getDepots()][route.get(index + 2).third];
//        }
//        else {
//            costNow = costMatrix[route.get(index).second + fileIO.getDepots()][route.get(index).third + fileIO.getDepots()] +
//                    costMatrix[route.get(index + 1).second + fileIO.getDepots()][route.get(index + 1).third + fileIO.getDepots()] +
//                    costMatrix[route.get(index + 2).second + fileIO.getDepots()][route.get(index + 2).third + fileIO.getDepots()];
//            costAfter = costMatrix[route.get(index).second + fileIO.getDepots()][route.get(index + 1).third + fileIO.getDepots()] +
//                    costMatrix[route.get(index + 1).third + fileIO.getDepots()][route.get(index + 1).second + fileIO.getDepots()] +
//                    costMatrix[route.get(index + 1).second + fileIO.getDepots()][route.get(index + 2).third + fileIO.getDepots()];
//        }
//
//        return costAfter < costNow;
//    }



    //    public  void calculateTotalCost(){
//        for (int k = 0; k < fileIO.getDepots(); k++) {
//            for (int i = fileIO.getDepots(); i < fileIO.getDepots() + fileIO.getTrips(); i++) {
//                if (genes[k][i][k])
//                    totalCost += costMatrix[i][k];
//                if (genes[k][k][i])
//                    totalCost += costMatrix[k][i];
//                for(int j = fileIO.getDepots(); j < fileIO.getDepots() + fileIO.getTrips(); j++){
//                    if(genes[k][i][j])
//                        totalCost += costMatrix[i][j];
//                }
//            }
//        }
//    }
}