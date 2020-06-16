package JavaFX;

import Genetic.GeneticAlg;
import Genetic.Individual;
import Genetic.Triplet;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

public class Controller {

    private boolean algorithmRunned = false;

    private Individual bestIndividualAsValue;

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
    private TextField bestResultNow;

    @FXML
    private AnchorPane mainPane;
//
//    @FXML
//    private LineChart<Number, Number> resultsGraph;
//
//    @FXML
//    public void initialize(){
////        NumberAxis y = new NumberAxis(2500000, 1300000, 100000);
////        y.setLabel("Value");
//
//        XYChart.Series<Number, Number> series = new XYChart.Series<>();
//        series.getData().add(new XYChart.Data<>(1, 15000000));
//        series.getData().add(new XYChart.Data<>(2, 17000000));
//        series.getData().add(new XYChart.Data<>(3, 11000000));
//        series.getData().add(new XYChart.Data<>(4, 19000000));
//        series.getData().add(new XYChart.Data<>(5, 25000000));
//        resultsGraph.getData().add(series);
//
//    }

    class Genetic extends Task{

        @Override
        protected String call() throws Exception {
            if(!pathText.getText().equals("")){
                errorMessage.setVisible(false);

                int counter = 0;
                int numberOfGenerations = 500;
                int currentGenerationBestCost = 0;
                double join = 0.1;
                double relocate = 0.1;
                joinRate.setText(String.valueOf(join));
                relocateRate.setText(String.valueOf(relocate));

                GeneticAlg geneticAlg = new GeneticAlg(pathText.getText(), 100);
                numberDepots.setText(String.valueOf(geneticAlg.getFileIO().getDepots()));
                numberTrips.setText(String.valueOf(geneticAlg.getFileIO().getTrips()));

                Individual individual;
                Individual bestIndividual;

                for(int i = 0; i < numberOfGenerations; i++) {
                    try {
                        generationNumber.setText(String.valueOf(i));
                    } catch (Exception e){
                        System.out.println("sunt aici");
                        System.out.println(e.getMessage());
                    }
                    geneticAlg.run(join, relocate);
                    individual = geneticAlg.getBestIndividualInCurrentGeneration();
                    bestIndividual = geneticAlg.getBestIndividual();
                    bestIndividualAsValue = bestIndividual;
                    bestResultNow.setText(String.valueOf(individual.getTotalCost()));
                    bestResultEver.setText(String.valueOf(bestIndividual.getTotalCost()));

                    if(currentGenerationBestCost == 0)
                        currentGenerationBestCost = individual.getTotalCost();
                    else if(currentGenerationBestCost == individual.getTotalCost())
                        counter++;
                    else {
                        counter = 0;
                        currentGenerationBestCost = individual.getTotalCost();
                    }

                    if(counter == 10 && relocate < 0.5 && join < 0.5){
                        join += 0.1;
                        relocate += 0.1;
                        counter = 0;
                        joinRate.setText(String.valueOf(join));
                        relocateRate.setText(String.valueOf(relocate));
                    }
                    if(counter == 10)
                        break;
                }

            }
            else errorMessage.setVisible(true);

            algorithmRunned = true;

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
    void onStartClicked(ActionEvent event) {
        Genetic genetic = new Genetic();
        new Thread(genetic).start();
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
}
