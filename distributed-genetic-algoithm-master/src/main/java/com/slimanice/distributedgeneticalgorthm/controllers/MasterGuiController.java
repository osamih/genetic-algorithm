package com.slimanice.distributedgeneticalgorthm.controllers;

import com.slimanice.distributedgeneticalgorthm.agents.MasterAgent;
import com.slimanice.distributedgeneticalgorthm.containers.MainContainer;
import com.slimanice.distributedgeneticalgorthm.containers.SimpleContainer;
import com.slimanice.distributedgeneticalgorthm.utils.Solution;
import jade.core.AID;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MasterGuiController implements Initializable {

    @FXML
    TextField targetField;
    @FXML
    TextField nbrIslands;
    @FXML
    TextField maxGenField;
    @FXML
    TextField populationSizeField;
    @FXML
    Label detailsLabel;
    @FXML
    Slider mutationRateSlider;
    @FXML
    Button startButton;
    @FXML
    Button resetBtn;
    private MasterAgent masterAgent;
    @FXML
    TableView<Solution> tableView;
    @FXML
    TableColumn<Solution, String> islandColumn;
    @FXML
    TableColumn<Solution, String> solutionColumn;
    @FXML
    TableColumn<Solution, String> fitnessColumn;

    ObservableList<Solution> solutions = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set the table columns
        tableView.setItems(solutions);
        islandColumn.setCellValueFactory(new PropertyValueFactory<>("localName"));
        solutionColumn.setCellValueFactory(new PropertyValueFactory<>("solution"));
        fitnessColumn.setCellValueFactory(new PropertyValueFactory<>("fitness"));

        // Start the master container
        MainContainer.main(null);
        // Start button event handler
        startButton.setOnAction(actionEvent -> {
            // Empty the list view
            tableView.getItems().clear();
            if (targetField.getText().isEmpty() || maxGenField.getText().isEmpty() || populationSizeField.getText().isEmpty() || nbrIslands.getText().isEmpty()) {
                // Alert user to fill all fields
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Please fill all fields!");
                alert.showAndWait();
            }
            else {
                // Get target string
                String target = targetField.getText();
                // Get max generation
                int maxGen = Integer.parseInt(maxGenField.getText());
                // Get population size
                int populationSize = Integer.parseInt(populationSizeField.getText());
                // Get number of islands
                int numberOfIslands = Integer.parseInt(nbrIslands.getText());
                double mutationRate = mutationRateSlider.getValue();

                // Start the algorithm
                // Start the island container
                SimpleContainer.masterGuiController = this;
                SimpleContainer.main(null);
                // Set details
                detailsLabel.setText("Initializing islands...");
                detailsLabel.setStyle("-fx-text-fill: rgba(52,26,128,0.73)");
            }
        });

        // Rest button event handler
        resetBtn.setOnAction(actionEvent -> {
            // Empty the table view
            tableView.getItems().clear();
            // Reset all fields
            targetField.setText("");
            maxGenField.setText("");
            populationSizeField.setText("");
            nbrIslands.setText("");
            mutationRateSlider.setValue(0.5);
            detailsLabel.setText("");
            // Terminate the MasterAgent and the IslandAgents
            masterAgent.doDelete();
        });
    }

    public double getMutationRate() {
        return mutationRateSlider.getValue();
    }

    public int getMaxGen() {
        return Integer.parseInt(maxGenField.getText());
    }

    public int getNbrIslands() {
        return Integer.parseInt(nbrIslands.getText());
    }

    public int getPopulationSize() {
        return Integer.parseInt(populationSizeField.getText());
    }

    public String getTarget() {
        return targetField.getText();
    }
    
    public void setMasterAgent(MasterAgent masterAgent) {
        this.masterAgent = masterAgent;
    }

    public void displayResults(List<Solution> solutionList) {
        Platform.runLater(() -> {
            // Clear the table view
            tableView.getItems().clear();
            // Display the solutions
            for (Solution solution : solutionList) {
                solutions.add(solution);
            }
            // Set details
            detailsLabel.setText("Algorithm finished!");
            detailsLabel.setStyle("-fx-text-fill: rgb(54,220,105)");
        });
    }

    public void displayIsland(AID aid) {
        Platform.runLater(() -> {
            // Add the island to the table view
            solutions.add(new Solution(aid, "Initializing...", -1));
        });
    }
}
