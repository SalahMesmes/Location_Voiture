package com.carrental.controllers;

import com.carrental.models.Rental;
import com.carrental.services.RentalService;
import com.carrental.utils.ValidationUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RentalsViewController {
    @FXML private VBox rentalsContainer;
    @FXML private Button closeButton;

    private RentalService rentalService;

    @FXML
    public void initialize() {
        rentalService = RentalService.getInstance();
        loadActiveRentals();
    }

    private void loadActiveRentals() {
        rentalsContainer.getChildren().clear();
        
        var activeRentals = rentalService.getActiveRentals();
        
        if (activeRentals.isEmpty()) {
            Label noRentalsLabel = new Label("Aucune location en cours");
            noRentalsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
            rentalsContainer.getChildren().add(noRentalsLabel);
            return;
        }
        
        for (Rental rental : activeRentals) {
            VBox rentalCard = createRentalCard(rental);
            rentalsContainer.getChildren().add(rentalCard);
        }
    }

    private VBox createRentalCard(Rental rental) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15; " +
                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        
        // Informations de la location
        Label carLabel = new Label(" " + rental.getCar().getBrand() + " " + rental.getCar().getModel());
        carLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label customerLabel = new Label(" Client : " + rental.getCustomer().getFirstName() +
                                        " " + rental.getCustomer().getLastName());
        customerLabel.setStyle("-fx-font-size: 14px;");
        
        Label periodLabel = new Label(" Période : " + rental.getStartDate() + " au " + rental.getEndDate());
        periodLabel.setStyle("-fx-font-size: 14px;");
        
        Label priceLabel = new Label(" Prix total : " + String.format("%.2f€", rental.getTotalPrice()));
        priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2196F3; -fx-font-weight: bold;");
        
        // Bouton pour rendre la voiture
        Button returnButton = new Button("Rendre la voiture");
        returnButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; " +
                            "-fx-padding: 8 16; -fx-background-radius: 5;");
        returnButton.setOnAction(e -> returnRental(rental));
        
        card.getChildren().addAll(carLabel, customerLabel, periodLabel, priceLabel, returnButton);
        
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
            
            // Recharge la liste
            loadActiveRentals();
            
            // Rafraîchit la vue principale 
            refreshMainView();
        } else {
            ValidationUtils.showError("Erreur lors du retour de la voiture.");
        }
    }

    private void refreshMainView() {

    }


    @FXML
    public void onCloseButtonClick() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}

