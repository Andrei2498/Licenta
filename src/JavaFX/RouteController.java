package JavaFX;


import Genetic.Individual;
import Genetic.Triplet;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class RouteController {

    @FXML
    private ScrollPane routePane;

    private Individual individual;

    public void start(Individual individual){
        this.individual = individual;
    }

    public void run(){
        VBox vBox = new VBox();
        Text text;
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<ArrayList<Triplet>> routesMatrix = individual.getRoutesMatrix();
        routesMatrix.sort(new Comparator<ArrayList<Triplet>>() {
            @Override
            public int compare(ArrayList<Triplet> o1, ArrayList<Triplet> o2) {
                return o2.size() - o1.size();
            }
        });
        for(int i = 0; i < routesMatrix.size(); i++) {
            for (int j = 0; j < routesMatrix.get(i).size(); j++) {
                if(j == 0){
                    stringBuilder.append(i + 1).append(")  ").append("D").append(routesMatrix.get(i).get(j).first + 1).append("  ");
                    stringBuilder.append("--->  " + "T").append(routesMatrix.get(i).get(j).third + 1).append("  ");
                }
                else if(j == routesMatrix.get(i).size() - 1){
                    stringBuilder.append("--->  " + "D").append(routesMatrix.get(i).get(j).first + 1);
                }
                else {
                    stringBuilder.append("--->  " + "T").append(routesMatrix.get(i).get(j).third + 1).append("  ");
                }
            }
            text = new Text(stringBuilder.toString());
            text.setStyle("-fx-font-weight: bold;\n -fx-font-size: 20");
            stringBuilder.setLength(0);
            vBox.getChildren().add(text);
        }
//        vBox.getChildren().addAll(new Button("button1"), new Button("button2"), new Button("button3"));
        routePane.setContent(vBox);
    }
}
