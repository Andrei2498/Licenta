import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileIO {

    private int depots;
    private int trips;
    private int[] nrVehicles;
    private int[][] cost;

    public FileIO(String filename){
        setVariables(filename);
    }

    private void setVariables(String filename){

        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            String[] splitLine;

            // Parsing the first line and initializing the size variables.
            line = br.readLine();
            splitLine = line.trim().split("\\s+");
            depots = Integer.valueOf(splitLine[0]);
            trips = Integer.valueOf(splitLine[1]);
            nrVehicles = new int[depots];
            cost = new int[depots + trips][depots + trips];
            for (int i = 0; i < depots; i++) {
                nrVehicles[i] = Integer.valueOf(splitLine[2 + i]);
            }

            // Initialization of the cost matrix
            int current_line = 0; //Current line in the cost matrix
            while ((line = br.readLine()) != null) {
                splitLine = line.trim().split("\\s+");
                for (int j = 0; j < splitLine.length; j++) {
                    cost[current_line][j] = Integer.valueOf(splitLine[j]);
                }
                current_line += 1;
            }
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public int getDepots(){
        return depots;
    }

    public int getTrips(){
        return trips;
    }

    public int[] getNrVehicles(){
        return nrVehicles;
    }

    public int[][] getCost(){
        return cost;
    }

}
