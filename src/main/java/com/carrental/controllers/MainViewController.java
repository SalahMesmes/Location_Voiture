package com.carrental.controllers;

import com.carrental.models.Car;
import com.carrental.services.CarService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class MainViewController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> typeFilter;

    @FXML
    private FlowPane carsContainer;

    private final CarService carService = new CarService();

    @FXML
    public void initialize() {
        typeFilter.getItems().addAll("Tous", "Berline", "Sport", "SUV", "4x4");
        typeFilter.setValue("Tous");

        loadCars();
    }

    // Chargement des voitures
    private void loadCars() {
        carsContainer.getChildren().clear();

        String keyword = searchField.getText();
        String type = typeFilter.getValue();

        List<Car> cars = carService.searchCars(keyword, type);

        for (Car car : cars) {
            carsContainer.getChildren().add(createCarCard(car));
        }
    }

    // Carte voiture
    private Node createCarCard(Car car) {
        VBox card = new VBox(10);
        card.getStyleClass().add("car-card");
        card.setPrefWidth(250);

        Label title = new Label(car.getBrand() + " " + car.getModel());
        title.getStyleClass().add("car-title");

        Label type = new Label(car.getType());
        Label price = new Label(String.format("%.2f € / jour", car.getPricePerDay()));

        Label status = new Label(car.isAvailable() ? "Disponible" : "Indisponible");
        status.getStyleClass().add(car.isAvailable() ? "status-available" : "status-unavailable");

        Button detailsButton = new Button("Voir détails");
        detailsButton.getStyleClass().add("primary-button");
        detailsButton.setOnAction(e -> showCarDetails(car));

        card.getChildren().addAll(title, type, price, status, detailsButton);
        return card;
    }
 
    @FXML
    private void onSearch() {
        loadCars();
    }

    @FXML
    private void onRefresh() {
        try {
            loadCars();
        } catch (Exception e) {
            showError("Erreur lors du rafraîchissement des véhicules.");
            e.printStackTrace();
        }
    }

    // Détails voiture
    private void showCarDetails(Car car) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/car-details-popup.fxml")
            );
            Parent root = loader.load();

            CarDetailsController controller = loader.getController();
            controller.setCar(car);

            Stage stage = new Stage();
            stage.setTitle("Détails du véhicule");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadCars(); // refresh après fermeture
        } catch (IOException e) {
            showError("Erreur lors de l'ouverture des détails.");
            e.printStackTrace();
        }
    }

    // Connexion Admin
    @FXML
    private void openAdminLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/admin-login.fxml")
            );
            Parent root = loader.load();

            Scene scene = new Scene(root, 400, 300);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Connexion Admin");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            loadCars(); // refresh après fermeture
        } catch (IOException e) {
            showError("Erreur lors de l'ouverture de la connexion admin.");
            e.printStackTrace();
        }
    }

    // Erreurs
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}