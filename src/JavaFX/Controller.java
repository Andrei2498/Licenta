package JavaFX;

import Genetic.GeneticAlg;
import Genetic.Individual;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;

public class Controller {

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

    @FXML
    private LineChart<Number, Number> resultsGraph;

    @FXML
    public void initialize(){
//        NumberAxis y = new NumberAxis(2500000, 1300000, 100000);
//        y.setLabel("Value");

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>(1, 15000000));
        series.getData().add(new XYChart.Data<>(2, 17000000));
        series.getData().add(new XYChart.Data<>(3, 11000000));
        series.getData().add(new XYChart.Data<>(4, 19000000));
        series.getData().add(new XYChart.Data<>(5, 25000000));
        resultsGraph.getData().add(series);

    }

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

                for(int i = 0; i < numberOfGenerations; i++) {
                    generationNumber.setText(String.valueOf(i));

                    geneticAlg.run(join, relocate);
                    Individual individual = geneticAlg.getBestIndividualInCurrentGeneration();
                    Individual bestIndividual = geneticAlg.getBestIndividual();
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

                    if(counter == 25 && relocate < 0.5 && join < 0.5){
                        join += 0.1;
                        relocate += 0.1;
                        counter = 0;
                        joinRate.setText(String.valueOf(join));
                        relocateRate.setText(String.valueOf(relocate));
                    }
                    if(counter == 50)
                        break;
                }
            }
            else errorMessage.setVisible(true);

            return null;
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
