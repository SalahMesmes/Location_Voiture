package com.carrental.controllers;

import com.carrental.models.Car;
import com.carrental.services.CarService;
import com.carrental.utils.ValidationUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AdminAddCarController {

    @FXML
    private TextField brandField;
    @FXML
    private TextField modelField;
    @FXML
    private ComboBox<String> typeField;
    @FXML
    private TextField yearField;
    @FXML
    private TextField priceField;
    @FXML
    private ComboBox<String> fuelTypeField;
    @FXML
    private ComboBox<String> transmissionField;
    @FXML
    private TextField seatsField;
    @FXML
    private TextField imageUrlField;
    @FXML
    private CheckBox availableField;
    @FXML
    private Label titleLabel;
    @FXML
    private Button actionButton;

    private AdminDashboardController adminController;
    private final CarService carService = new CarService();
    private Car carToEdit; 

    @FXML
    public void initialize() {
        typeField.getItems().addAll("Berline", "Sport", "SUV", "4x4", "Compacte", "Utilitaire");
        fuelTypeField.getItems().addAll("Essence", "Diesel", "Hybride", "Électrique");
        transmissionField.getItems().addAll("Manuelle", "Automatique");
        
        typeField.setValue("Berline");
        fuelTypeField.setValue("Essence");
        transmissionField.setValue("Manuelle");
    }

    public void setAdminController(AdminDashboardController controller) {
        this.adminController = controller;
    }

    public void setCarToEdit(Car car) {
        this.carToEdit = car;
        
        if (car != null) {
    
            brandField.setText(car.getBrand());
            modelField.setText(car.getModel());
            typeField.setValue(car.getType());
            yearField.setText(String.valueOf(car.getYear()));
            priceField.setText(String.valueOf(car.getPricePerDay()));
            fuelTypeField.setValue(car.getFuelType());
            transmissionField.setValue(car.getTransmission());
            seatsField.setText(String.valueOf(car.getNumberOfSeats()));
            availableField.setSelected(car.isAvailable());
            
            if (car.getImageUrl() != null && !car.getImageUrl().isEmpty()) {
                imageUrlField.setText(car.getImageUrl());
            }
            
        
            if (titleLabel != null) {
                titleLabel.setText("Modifier une voiture");
            }
            if (actionButton != null) {
                actionButton.setText("Modifier");
            }
        } else {
            brandField.clear();
            modelField.clear();
            typeField.setValue("Berline");
            yearField.clear();
            priceField.clear();
            fuelTypeField.setValue("Essence");
            transmissionField.setValue("Manuelle");
            seatsField.clear();
            availableField.setSelected(true);
            imageUrlField.clear();
            
            if (titleLabel != null) {
                titleLabel.setText("Ajouter une nouvelle voiture");
            }
            if (actionButton != null) {
                actionButton.setText("Ajouter");
            }
        }
    }

    @FXML
    private void onAdd() {
        // Validation
        if (brandField.getText().trim().isEmpty()) {
            ValidationUtils.showError("La marque est obligatoire.");
            return;
        }
        if (modelField.getText().trim().isEmpty()) {
            ValidationUtils.showError("Le modèle est obligatoire.");
            return;
        }
        if (typeField.getValue() == null) {
            ValidationUtils.showError("Le type est obligatoire.");
            return;
        }
        if (yearField.getText().trim().isEmpty()) {
            ValidationUtils.showError("L'année est obligatoire.");
            return;
        }
        if (priceField.getText().trim().isEmpty()) {
            ValidationUtils.showError("Le prix est obligatoire.");
            return;
        }
        if (fuelTypeField.getValue() == null) {
            ValidationUtils.showError("Le type de carburant est obligatoire.");
            return;
        }
        if (transmissionField.getValue() == null) {
            ValidationUtils.showError("La transmission est obligatoire.");
            return;
        }
        if (seatsField.getText().trim().isEmpty()) {
            ValidationUtils.showError("Le nombre de places est obligatoire.");
            return;
        }

        try {
            int year = Integer.parseInt(yearField.getText().trim());
            if (year < 1900 || year > 2100) {
                ValidationUtils.showError("L'année doit être entre 1900 et 2100.");
                return;
            }

            double price = Double.parseDouble(priceField.getText().trim());
            if (price <= 0) {
                ValidationUtils.showError("Le prix doit être supérieur à 0.");
                return;
            }

            int seats = Integer.parseInt(seatsField.getText().trim());
            if (seats < 1 || seats > 20) {
                ValidationUtils.showError("Le nombre de places doit être entre 1 et 20.");
                return;
            }

            Car car;
            if (carToEdit != null) {
                car = carToEdit;
            } else {
                car = new Car();
            }

            car.setBrand(brandField.getText().trim());
            car.setModel(modelField.getText().trim());
            car.setType(typeField.getValue());
            car.setYear(year);
            car.setPricePerDay(price);
            car.setFuelType(fuelTypeField.getValue());
            car.setTransmission(transmissionField.getValue());
            car.setNumberOfSeats(seats);
            car.setAvailable(availableField.isSelected());
            
            // Gestion de l'image
            String imageUrl = imageUrlField.getText().trim();
            if (imageUrl.isEmpty()) {
                // Pas d'image spécifiée, on laisse vide
                car.setImageUrl("");
            } else {
                car.setImageUrl(imageUrl);
            }
            boolean success;
            if (carToEdit != null) {
                //  modification
                success = carService.updateCar(car);
                if (success) {
                    ValidationUtils.showSuccess("Voiture modifiée avec succès !");
                } else {
                    ValidationUtils.showError("Erreur lors de la modification de la voiture.");
                }
            } else {
                // mise ajout
                success = carService.addCar(car);
                if (success) {
                    ValidationUtils.showSuccess("Voiture ajoutée avec succès !");
                } else {
                    ValidationUtils.showError("Erreur lors de l'ajout de la voiture.");
                }
            }
            
            if (success && adminController != null) {
                adminController.refreshCars();
                closeWindow();
            }
        } catch (NumberFormatException e) {
            ValidationUtils.showError("Veuillez entrer des valeurs numériques valides pour l'année, le prix et le nombre de places.");
        } catch (Exception e) {
            ValidationUtils.showError("Erreur inattendue : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) brandField.getScene().getWindow();
        stage.close();
    }
}

