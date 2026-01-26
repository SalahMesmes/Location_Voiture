package com.carrental.controllers;

import com.carrental.models.Car;
import com.carrental.models.Rental;
import com.carrental.services.CarService;
import com.carrental.services.RentalService;
import com.carrental.utils.ValidationUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.temporal.ChronoUnit;

public class AdminDashboardController {

    @FXML
    private TabPane mainTabs;

    @FXML
    private FlowPane carsContainer;

    @FXML
    private VBox historyContainer;

    @FXML
    private VBox activeRentalsContainer;

    private final CarService carService = new CarService();
    private final RentalService rentalService = RentalService.getInstance();

    @FXML
    public void initialize() {
        loadCars();
        loadRentalHistory();
        loadActiveRentals();
    }

    // Gestion des voitures
    private void loadCars() {
        carsContainer.getChildren().clear();

        javafx.collections.ObservableList<Car> cars = carService.getAllCars();
        for (Car car : cars) {
            carsContainer.getChildren().add(createCarAdminCard(car));
        }
    }

    private Node createCarAdminCard(Car car) {
        VBox card = new VBox(10);
        card.getStyleClass().add("car-card");
        card.setPrefWidth(280);
        card.setStyle("-fx-padding: 15;");

        Label title = new Label(car.getBrand() + " " + car.getModel());
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        Label type = new Label("Type: " + car.getType());
        Label year = new Label("Année: " + car.getYear());
        Label price = new Label(String.format("Prix: %.2f € / jour", car.getPricePerDay()));

        Label status = new Label(car.isAvailable() ? " Disponible" : " Indisponible");
        status.setStyle(car.isAvailable() ? "-fx-text-fill: green;" : "-fx-text-fill: red;");

        HBox buttonsBox = new HBox(10);
        
        Button editButton = new Button("Modifier");
        editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        editButton.setOnAction(e -> editCar(car));
        
        Button deleteButton = new Button("Supprimer");
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> deleteCar(car));

        buttonsBox.getChildren().addAll(editButton, deleteButton);

        card.getChildren().addAll(title, type, year, price, status, buttonsBox);
        return card;
    }

    @FXML
    private void onAddCar() {
        openCarForm(null, "Ajouter une voiture");
    }

    private void editCar(Car car) {
        if (car == null) {
            ValidationUtils.showError("Aucune voiture sélectionnée.");
            return;
        }
        openCarForm(car, "Modifier une voiture");
    }

  
    private void openCarForm(Car car, String windowTitle) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/admin-add-car.fxml")
            );
            Parent root = loader.load();

            AdminAddCarController controller = loader.getController();
            if (controller == null) {
                ValidationUtils.showError("Erreur : le contrôleur n'a pas été initialisé.");
                return;
            }
            
            controller.setAdminController(this);
        
            if (car != null) {
                controller.setCarToEdit(car);
            } else {
                controller.setCarToEdit(null);
            }

            Scene scene = new Scene(root, 600, 700);
            try {
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            } catch (Exception e) {
                System.out.println("ATTENTION : CSS non trouvé, mais on continue...");
            }

            Stage stage = new Stage();
            stage.setTitle(windowTitle);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            String errorMsg = "Erreur lors de l'ouverture du formulaire : " + e.getMessage();
            System.err.println(errorMsg);
            e.printStackTrace();
            ValidationUtils.showError(errorMsg);
        }
    }

    private void deleteCar(Car car) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de suppression");
        confirmAlert.setHeaderText("Supprimer la voiture");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer " + car.getBrand() + " " + car.getModel() + " ?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = carService.deleteCar(car.getId());
                if (success) {
                    ValidationUtils.showSuccess("Voiture supprimée avec succès !");
                    loadCars();
                } else {
                    ValidationUtils.showError("Erreur lors de la suppression de la voiture.");
                }
            }
        });
    }

    public void refreshCars() {
        loadCars();
    }

    // Historique des locations
    private void loadRentalHistory() {
        historyContainer.getChildren().clear();

        javafx.collections.ObservableList<Rental> rentals = rentalService.getRentalHistory();
        
        if (rentals.isEmpty()) {
            Label noData = new Label("Aucune location dans l'historique.");
            noData.setStyle("-fx-font-size: 14; -fx-text-fill: gray;");
            historyContainer.getChildren().add(noData);
            return;
        }

        for (Rental rental : rentals) {
            historyContainer.getChildren().add(createHistoryCard(rental));
        }
    }

    private Node createHistoryCard(Rental rental) {
        VBox card = new VBox(10);
        card.getStyleClass().add("car-card");
        card.setStyle("-fx-padding: 15; -fx-pref-width: 1000;");

        HBox headerBox = new HBox(15);
        Label customerLabel = new Label(
            " Client: " + rental.getCustomer().getFirstName() + " " + rental.getCustomer().getLastName()
        );
        customerLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        Label emailLabel = new Label("Email: " + rental.getCustomer().getEmail());
        Label phoneLabel = new Label("Tel: " + rental.getCustomer().getPhoneNumber());

        headerBox.getChildren().addAll(customerLabel, emailLabel, phoneLabel);

        Label carLabel = new Label(
            "Voiture: " + rental.getCar().getBrand() + " " + rental.getCar().getModel() +
            " (" + rental.getCar().getType() + ")"
        );
        carLabel.setStyle("-fx-font-size: 14;");

        long days = ChronoUnit.DAYS.between(rental.getStartDate(), rental.getEndDate()) + 1;
        Label periodLabel = new Label(
            "Période: " + rental.getStartDate() + " au " + rental.getEndDate() +
            " (" + days + " jour" + (days > 1 ? "s" : "") + ")"
        );
        periodLabel.setStyle("-fx-font-size: 14;");

        Label priceLabel = new Label(
            " Prix total: " + String.format("%.2f €", rental.getTotalPrice())
        );
        priceLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        Label statusLabel = new Label(
            "Status: " + (rental.getStatus() == Rental.RentalStatus.EN_COURS ? "En cours" : "Terminée")
        );
        statusLabel.setStyle("-fx-font-size: 14;");

        card.getChildren().addAll(headerBox, carLabel, periodLabel, priceLabel, statusLabel);
        return card;
    }

    // Locations en cours
    private void loadActiveRentals() {
        activeRentalsContainer.getChildren().clear();

        javafx.collections.ObservableList<Rental> rentals = rentalService.getActiveRentals();
        
        if (rentals.isEmpty()) {
            Label noData = new Label("Aucune location en cours.");
            noData.setStyle("-fx-font-size: 14; -fx-text-fill: gray;");
            activeRentalsContainer.getChildren().add(noData);
            return;
        }

        for (Rental rental : rentals) {
            activeRentalsContainer.getChildren().add(createActiveRentalCard(rental));
        }
    }

    private Node createActiveRentalCard(Rental rental) {
        VBox card = new VBox(10);
        card.getStyleClass().add("car-card");
        card.setStyle("-fx-padding: 15; -fx-pref-width: 1000;");

        HBox headerBox = new HBox(15);
        Label customerLabel = new Label(
            " Client: " + rental.getCustomer().getFirstName() + " " + rental.getCustomer().getLastName()
        );
        customerLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        Label emailLabel = new Label(" " + rental.getCustomer().getEmail());
        Label phoneLabel = new Label("Tel" + rental.getCustomer().getPhoneNumber());

        headerBox.getChildren().addAll(customerLabel, emailLabel, phoneLabel);

        Label carLabel = new Label(
            " Voiture: " + rental.getCar().getBrand() + " " + rental.getCar().getModel() +
            " (" + rental.getCar().getType() + ")"
        );
        carLabel.setStyle("-fx-font-size: 14;");

        long days = ChronoUnit.DAYS.between(rental.getStartDate(), rental.getEndDate()) + 1;
        Label periodLabel = new Label(
            " Période: " + rental.getStartDate() + " au " + rental.getEndDate() +
            " (" + days + " jour" + (days > 1 ? "s" : "") + ")"
        );
        periodLabel.setStyle("-fx-font-size: 14;");

        Label priceLabel = new Label(
            " Prix total: " + String.format("%.2f €", rental.getTotalPrice())
        );
        priceLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        Button returnButton = new Button("Rendre la voiture");
        returnButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        returnButton.setOnAction(e -> returnRental(rental));

        card.getChildren().addAll(headerBox, carLabel, periodLabel, priceLabel, returnButton);
        return card;
    }

    private void returnRental(Rental rental) {
        boolean success = rentalService.returnRental(rental.getId());
        if (success) {
            ValidationUtils.showSuccess(
                " Voiture rendue avec succès !\n\n" +
                "La voiture " + rental.getCar().getBrand() + " " + rental.getCar().getModel() +
                " est maintenant disponible à la location."
            );
            loadActiveRentals();
            loadCars(); // Rafraîchit aussi la liste des voitures
        } else {
            ValidationUtils.showError("Erreur lors du retour de la voiture.");
        }
    }

    // Déconnexion
    @FXML
    private void onLogout() {
        Stage stage = (Stage) mainTabs.getScene().getWindow();
        stage.close();
    }
}

