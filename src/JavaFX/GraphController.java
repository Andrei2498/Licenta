package JavaFX;

import Genetic.Individual;
import Genetic.Triplet;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;

public class GraphController {

    private Individual individual;

    private int numberOfLevels;

    @FXML
    private AnchorPane graphPane;

    public GraphController(){
        super();
    }

    public void start(Individual individual){
        this.individual = individual;
    }

    public void run(){
        double x;
        double y;
        double radius = 3.5;
        int[] indexList;
        int[] tripsPerLayer;
        double[] spaceOnLayer;
        ArrayList<ArrayList<Triplet>> list = individual.getRoutesMatrix();
        int[] depotPosition = new int[individual.fileIO.getDepots()];
        int numberOfLevels = 0;
        int[] sizeList = new int[10];

        for(int i = 0; i < list.size(); i++) {
            sizeList[list.get(i).size()]++;
            if(list.get(i).size() > numberOfLevels){
                numberOfLevels = list.get(i).size();
            }
        }
        numberOfLevels --;

        spaceOnLayer = new double[numberOfLevels + 1];
        indexList = new int[numberOfLevels + 1];
        tripsPerLayer = new int[numberOfLevels];
        for(int i = 2; i <= numberOfLevels + 1; i++)
            for(int j = i; j <= numberOfLevels + 1; j++)
                tripsPerLayer[i - 2] += sizeList[j];

        for(int i = 0; i < tripsPerLayer.length; i++) {
            spaceOnLayer[i] = 65 + (1835 - tripsPerLayer[i] * 10.5) / 2;
        }

        /*Drawing level text*/
        Text text = new Text(25, 50, "D:");
        text.setStyle("-fx-font-weight: bold;\n -fx-font-size: 20");
        graphPane.getChildren().add(text);
        text = new Text(25, 950,"D:");
        text.setStyle("-fx-font-weight: bold;\n -fx-font-size: 20");
        graphPane.getChildren().add(text);
        int levelSpace = 950/(numberOfLevels + 1);
        for(int i = 1; i <= numberOfLevels; i++){
            text = new Text(25, 25 + levelSpace * i, "T" + i);
            text.setStyle("-fx-font-weight: bold;\n -fx-font-size: 20");
            graphPane.getChildren().add(text);
        }

        /*Drawing depots*/
        int depotSpace = (1850 - individual.fileIO.getDepots() * 40)/(individual.fileIO.getDepots() + 1);
        for(int i = 1; i <= individual.fileIO.getDepots(); i++){
            depotPosition[i - 1] = 65 + depotSpace * i;
            Circle circle = new Circle(65 + depotSpace * i, 50,20);
            graphPane.getChildren().add(circle);
            circle = new Circle(65 + depotSpace * i, 950, 20);
            graphPane.getChildren().add(circle);
        }

        /*Drawing trips*/
        double auxX = 0;
        double auxY = 0;

//        ArrayList<ArrayList<Triplet>> auxList = new ArrayList<>();
//        for(int i = 0; i < 10; i ++)
//            auxList.add(list.get(i));
        for(ArrayList<Triplet> route: list) {
            for (int i = 0; i < route.size(); i++) {
                x = spaceOnLayer[i] + (indexList[i] + 1) * 10.5;
                y = 25 + levelSpace * (i + 1);
                if (i == 0) {
                    indexList[i] ++;
                    Circle circle = new Circle(x, y, radius);
                    graphPane.getChildren().add(circle);
                    Line line = new Line(depotPosition[route.get(0).first], 50, x, y);
                    graphPane.getChildren().add(line);
                    auxX = x;
                    auxY = y;
                } else if (i == route.size() - 1) {
                    Line line = new Line(depotPosition[route.get(route.size() - 1).third], 950, auxX, auxY);
                    graphPane.getChildren().add(line);
                } else {
                    indexList[i] ++;
                    Circle circle = new Circle(x, y, radius);
                    graphPane.getChildren().add(circle);
                    Line line = new Line(x, y, auxX, auxY);
                    graphPane.getChildren().add(line);
                    auxX = x;
                    auxY = y;
                }

            }
        }
    }

    class Graph extends Task{

        @Override
        protected String call() throws Exception{
//            System.out.println("Sunt aici");
//            ArrayList<ArrayList<Triplet>> list = individual.getRoutesMatrix();
//            for(int j = 0; j < sizeList.length; j++)
//                System.out.println(j+ ": " + sizeList[j]);
//
//            System.out.println("Number of lvls: " + numberOfLevels);
            return null;
        }
    }

}
