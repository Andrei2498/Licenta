package JavaFX;

import Genetic.GeneticAlg;
import Genetic.Individual;
import Genetic.Triplet;
import Gurobi.GurobiMain;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class Controller {

    private boolean algorithmRunned = false;

    private Individual bestIndividualAsValue;

    private Individual bestIndividualInitially;

    private GeneticAlg geneticAlg;

    private int index;

    private int counter = 0;

    private int result;

    private int generalResult = 0;

    private double relocateRateVariable = 0.05;

    private double joinRateVariable = 0.05;

    /*-----------File variables ----------*/
    @FXML
    private TextField pathText;

    @FXML
    private TextField numberDepots;

    @FXML
    private TextField numberTrips;

    @FXML
    private Text errorMessage;

    /*-----------Genetic variables ---------*/
    @FXML
    private TextField joinRate;

    @FXML
    private TextField relocateRate;

    @FXML
    private TextField generationNumber;

    @FXML
    private TextField bestResultEver;

    @FXML
    private TextField bestNumberOfBuses;

    @FXML
    private TextField bestResultNow;

    @FXML
    private BarChart<String , Number> routeChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private AnchorPane mainPane;

    /*---------------Gurobi variables-------------*/
    @FXML
    private TextField resultGurobi;


    class Genetic extends Task{

        @Override
        protected String call() throws Exception {
            LocalDateTime now;
            now = LocalDateTime.now();
            geneticAlg = new GeneticAlg(pathText.getText(), 100);
            bestIndividualInitially = geneticAlg.getBestIndividualInCurrentGeneration();
            numberTrips.setText(String.valueOf(geneticAlg.getFileIO().getTrips()));
            numberDepots.setText(String.valueOf(geneticAlg.getFileIO().getDepots()));

            for(int i = 0; i < 500; i++) {
                index = i;
                geneticAlg.run(joinRateVariable, relocateRateVariable);
                result = geneticAlg.getBestIndividualInCurrentGeneration().getTotalCost();

                if(generalResult == 0)
                    generalResult = result;
                else  if(generalResult == result)
                    counter ++;
                else {
                    counter = 0;
                    generalResult = result;
                }

                if(counter == 10 && relocateRateVariable < 0.2 && joinRateVariable < 0.2){
                    joinRateVariable += 0.05;
                    relocateRateVariable += 0.05;
                    counter = 0;
                }

                if(counter == 10)
                    break;

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        joinRate.setText(String.valueOf(joinRateVariable));
                        relocateRate.setText(String.valueOf(relocateRateVariable));
                        generationNumber.setText(String.valueOf(index));
                        bestResultNow.setText(String.valueOf(result));
                        bestResultEver.setText(String.valueOf(geneticAlg.getBestIndividual().getTotalCost()));
                        bestNumberOfBuses.setText(String.valueOf(geneticAlg.getBestIndividual().getRoutesMatrix().size()));
                    }
                });

            }

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    populateChart();
                }
            });

            bestIndividualAsValue = geneticAlg.getBestIndividual();
            algorithmRunned = true;

            LocalDateTime end;
            end = LocalDateTime.now();
            float averageTime = 0;
            averageTime += Duration.between(now, end).toMillis()/1000f;
            System.out.println("Time: " + averageTime);
            return null;
        }
    }

    class Gurobi extends Task{

        @Override
        protected Object call() throws Exception {
            GurobiMain gurobiMain = new GurobiMain();
            gurobiMain.run(pathText.getText());

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    resultGurobi.setText(String.valueOf((int)(gurobiMain.objectiveValue)));
                }
            });

            return null;
        }
    }

    @FXML
    void onShowGraphClicked(ActionEvent event){
        if(algorithmRunned){
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("graphPage.fxml"));
                Parent root1 = fxmlLoader.load();

                GraphController graphController = fxmlLoader.getController();
                graphController.start(bestIndividualAsValue);

                Stage stage = new Stage();
                stage.setTitle("Genetic Graph");
                stage.setScene(new Scene(root1));
                stage.show();

                graphController.run();
            } catch (Exception e){
                e.printStackTrace();
                System.out.println("Can't load new window");
            }
        }
    }

    @FXML
    void onShowRoutesClicked(ActionEvent event){
        if(algorithmRunned){
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("routePage.fxml"));
                Parent root1 = fxmlLoader.load();

                RouteController routeController = fxmlLoader.getController();
                routeController.start(bestIndividualAsValue);

                Stage stage = new Stage();
                stage.setTitle("Route List");
                stage.setScene(new Scene(root1));
                stage.show();

                routeController.run();
            } catch (Exception e){
                e.printStackTrace();
                System.out.println("Can't load new window");
            }
        }
    }

    @FXML
    void onStartClicked(ActionEvent event) {
        if(!pathText.getText().equals("")) {
            clearFields();
            Thread thread = new Thread(new Genetic());
            thread.setDaemon(true);
            thread.start();
        }
    }

    private void clearFields(){
        numberTrips.clear();
        numberDepots.clear();
        generationNumber.clear();
        bestResultEver.clear();
        bestResultNow.clear();
        joinRate.clear();
        relocateRate.clear();
        bestNumberOfBuses.clear();
        routeChart.getData().clear();
    }

    private void populateChart(){
        xAxis.setLabel("Route size");
        yAxis.setLabel("Number");

        int[] initialList = getNumberOfTripsPerLevel(bestIndividualInitially);
        int[] finalList = getNumberOfTripsPerLevel(bestIndividualAsValue);
        ArrayList<String> lista = new ArrayList<>();

        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Initial Individual");
        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        series2.setName("Final Individual");
        for(int i = 0; i < initialList.length; i++){
            if(initialList[i] != 0 || finalList[i] != 0) {
                lista.add(String.valueOf(i));
                series1.getData().add(new XYChart.Data<>(String.valueOf(i), initialList[i]));
                series2.getData().add(new XYChart.Data<>(String.valueOf(i), finalList[i]));
            }
        }

        routeChart.getData().add(series1);
        routeChart.getData().add(series2);
        xAxis.setCategories(FXCollections.observableArrayList(lista));
    }

    private int[] getNumberOfTripsPerLevel(Individual individual){
        int[] sizeList = new int[10];
        for(int i = 0; i < individual.getRoutesMatrix().size(); i++) {
            sizeList[individual.getRoutesMatrix().get(i).size()]++;
        }
        return sizeList;
    }

    @FXML
    void onAddInstanceClicked(ActionEvent event){
        File file;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("data"));

        file = fileChooser.showOpenDialog(mainPane.getScene().getWindow());
        if(file != null){
            pathText.setText(file.getPath());
            errorMessage.setVisible(false);
        }
        else
            errorMessage.setVisible(true);
    }

    @FXML
    void onStartGurobiClicked(ActionEvent event){
        if(!pathText.getText().equals("")){
            Thread thread = new Thread(new Gurobi());
            thread.setDaemon(true);
            thread.start();
        }
    }
}
