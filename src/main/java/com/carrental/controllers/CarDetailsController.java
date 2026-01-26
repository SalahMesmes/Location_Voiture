package com.carrental.controllers;

import com.carrental.models.Car;
import com.carrental.utils.ValidationUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class CarDetailsController {

    @FXML private ImageView carImage;
    @FXML private Label brandLabel;
    @FXML private Label modelLabel;
    @FXML private Label typeLabel;
    @FXML private Label yearLabel;
    @FXML private Label fuelTypeLabel;
    @FXML private Label transmissionLabel;
    @FXML private Label seatsLabel;
    @FXML private Label pricePerDayLabel;
    @FXML private Button rentButton;
    @FXML private Button closeButton;

    private Car car;


    public void setCar(Car car) {
        setCarDetails(car);
    }

    public void setCarDetails(Car car) {
        this.car = car;

        if (car == null) return;

        brandLabel.setText(car.getBrand());
        modelLabel.setText(car.getModel());
        typeLabel.setText(car.getType());
        yearLabel.setText(String.valueOf(car.getYear()));
        fuelTypeLabel.setText(car.getFuelType());
        transmissionLabel.setText(car.getTransmission());
        seatsLabel.setText(car.getNumberOfSeats() + " places");
        pricePerDayLabel.setText(String.format("%.2f € / jour", car.getPricePerDay()));

        // Charger l'image de la voiture
        chargerImageVoiture(car.getImageUrl());

        if (!car.isAvailable()) {
            rentButton.setDisable(true);
            rentButton.setText("Non disponible");
        } else {
            rentButton.setDisable(false);
            rentButton.setText("Louer cette voiture");
        }
    }

    @FXML
    public void onRentButtonClick() {
        if (car == null || !car.isAvailable()) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/customer-form.fxml"));
            VBox root = loader.load();

            CustomerFormController controller = loader.getController();
            if (controller == null) {
                ValidationUtils.showError("Erreur : le contrôleur du formulaire n'a pas été initialisé.");
                return;
            }

            controller.setSelectedCar(car);

            Stage formStage = new Stage();
            formStage.setTitle("Formulaire de réservation");

            Scene scene = new Scene(root, 650, 600);

            try {
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            } catch (Exception ignored) {
            }

            formStage.setScene(scene);
            formStage.setResizable(true);
            formStage.setMinWidth(600);
            formStage.setMinHeight(500);
            formStage.show();

            // Ferme le popup de détails
            ((Stage) closeButton.getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
            ValidationUtils.showError("Erreur lors de l'ouverture du formulaire : " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            ValidationUtils.showError("Erreur inattendue : " + e.getMessage());
        }
    }

    @FXML
    public void onCloseButtonClick() {
        ((Stage) closeButton.getScene().getWindow()).close();
    }
    private void chargerImageVoiture(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            // Pas d'image, on laisse l'ImageView vide
            carImage.setImage(null);
            return;
        }

        try {
            Image image;
            String url = imageUrl.trim();

            // Vérifie si c'est une URL web 
            if (url.startsWith("http://") || url.startsWith("https://")) {
                image = new Image(url, true); 
            } else if (url.startsWith("/")) {
                image = new Image(getClass().getResourceAsStream(url));
            } else {
                if (url.startsWith("file://")) {
                    image = new Image(url);
                } else {
                    image = new Image(getClass().getResourceAsStream("/" + url));
                }
            }

            carImage.setImage(image);
        } catch (Exception e) {
            // Si le chargement échoue, on n'affiche pas d'image
            System.out.println("Impossible de charger l'image : " + imageUrl);
            System.out.println("Erreur : " + e.getMessage());
            carImage.setImage(null);
        }
    }
}