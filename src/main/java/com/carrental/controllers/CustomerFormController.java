package com.carrental.controllers;

import com.carrental.models.Car;
import com.carrental.models.Customer;
import com.carrental.models.Rental;
import com.carrental.services.CarService;
import com.carrental.services.CustomerService;
import com.carrental.services.RentalService;
import com.carrental.utils.ValidationUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class CustomerFormController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private TextField licenseField;
    @FXML private TextArea addressField;
    @FXML private TextField emailField;
    @FXML private DatePicker licenseExpiryPicker;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Label totalPriceLabel;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    private Car selectedCar;
    private CarService carService;
    private CustomerService customerService;
    private RentalService rentalService;

    @FXML
    public void initialize() {
        carService = CarService.getInstance();
        customerService = CustomerService.getInstance();
        rentalService = RentalService.getInstance();
        
        // n'accepter que les dates futures
        LocalDate today = LocalDate.now();
        licenseExpiryPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(today.plusDays(1)));
            }
        });
        
        startDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(today));
            }
        });
        
        endDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate startDate = startDatePicker.getValue();
                if (startDate != null) {
                    setDisable(empty || date.isBefore(startDate.plusDays(1)));
                } else {
                    setDisable(empty || date.isBefore(today));
                }
            }
        });
        
        // Ajoute des listeners pour calculer le prix automatiquement
        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            calculateAndDisplayPrice();
            endDatePicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    LocalDate startDate = startDatePicker.getValue();
                    if (startDate != null) {
                        setDisable(empty || date.isBefore(startDate.plusDays(1)));
                    } else {
                        setDisable(empty || date.isBefore(today));
                    }
                }
            });
        });
        
        endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            calculateAndDisplayPrice();
        });
    }

    public void setSelectedCar(Car car) {
        this.selectedCar = car;
        calculateAndDisplayPrice();
    }

    public void calculateAndDisplayPrice() {
        if (selectedCar == null) {
            totalPriceLabel.setText("0.00€");
            return;
        }
        
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        
        if (startDate != null && endDate != null && !endDate.isBefore(startDate)) {
            double totalPrice = rentalService.calculateTotalPrice(selectedCar, startDate, endDate);
            totalPriceLabel.setText(String.format("%.2f€", totalPrice));
            totalPriceLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        } else {
            totalPriceLabel.setText("Sélectionnez les dates");
            totalPriceLabel.setStyle("-fx-text-fill: #666;");
        }
    }

    @FXML
    public void onConfirmButtonClick() {
        // Valide tous les champs
        if (!validateForm()) {
            return;
        }
         // Crée le client dans la base de données
        try {          
            String address = addressField.getText();
            if (address != null) {
                address = address.trim();
                if (address.isEmpty()) {
                    address = null;
                }
            }
            
            Customer customer = customerService.createCustomer(
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                phoneField.getText().trim(),
                licenseField.getText().trim(),
                licenseExpiryPicker.getValue(),
                address,
                emailField.getText().trim()
            );
            
            if (customer == null) {
                ValidationUtils.showError("Erreur lors de la création du client. Veuillez vérifier vos informations.");
                return;
            }
            
            // Crée la location dans la base de données
            Rental rental = rentalService.createRental(
                selectedCar,
                customer,
                startDatePicker.getValue(),
                endDatePicker.getValue()
            );
            
            if (rental == null) {
                ValidationUtils.showError("Erreur lors de la création de la location.");
                return;
            }
            
            // Marque la voiture comme non disponible dans la base de données
            carService.updateCarAvailability(selectedCar.getId(), false);
            
            // Affiche un message de succès avec popup
            String successMessage = String.format(
                " Réservation confirmée avec succès !\n\n" +
                " Voiture : %s %s\n" +
                " Client : %s %s\n" +
                " Période : %s au %s\n" +
                " Prix total : %.2f€\n\n" +
                "La voiture a été enregistrée dans la base de données.",
                selectedCar.getBrand(), selectedCar.getModel(),
                customer.getFirstName(), customer.getLastName(),
                startDatePicker.getValue(), endDatePicker.getValue(),
                rental.getTotalPrice()
            );
            
            ValidationUtils.showSuccess(successMessage);
            
            // Ferme le formulaire
            Stage stage = (Stage) confirmButton.getScene().getWindow();
            stage.close();
            
            // Rafraîchit la liste des voitures 
            refreshMainView();
            
        } catch (RuntimeException e) {
            e.printStackTrace();
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("Erreur lors de la création du client")) {
                ValidationUtils.showError(errorMessage);
            } else {
                ValidationUtils.showError("Erreur lors de la création de la réservation : " + errorMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ValidationUtils.showError("Erreur lors de la création de la réservation : " + e.getMessage());
        }
    }

    private boolean validateForm() {
        // Validation prénom
        if (!ValidationUtils.isNotEmpty(firstNameField.getText())) {
            ValidationUtils.showError("Le prénom est obligatoire.");
            firstNameField.requestFocus();
            return false;
        }
        
        // Validation nom
        if (!ValidationUtils.isNotEmpty(lastNameField.getText())) {
            ValidationUtils.showError("Le nom est obligatoire.");
            lastNameField.requestFocus();
            return false;
        }
        
        // Validation téléphone
        if (!ValidationUtils.isValidPhoneNumber(phoneField.getText())) {
            ValidationUtils.showError("Le numéro de téléphone doit contenir 10 chiffres.");
            phoneField.requestFocus();
            return false;
        }
        
        // Validation email
        if (!ValidationUtils.isValidEmail(emailField.getText())) {
            ValidationUtils.showError("L'adresse email n'est pas valide.");
            emailField.requestFocus();
            return false;
        }
        
        // Validation numéro de permis
        if (!ValidationUtils.isValidLicenseNumber(licenseField.getText())) {
            ValidationUtils.showError("Le numéro de permis est obligatoire et doit être alphanumérique.");
            licenseField.requestFocus();
            return false;
        }
        
        // Validation date d'expiration du permis
        if (licenseExpiryPicker.getValue() == null) {
            ValidationUtils.showError("La date d'expiration du permis est obligatoire.");
            licenseExpiryPicker.requestFocus();
            return false;
        }
        
        if (!ValidationUtils.isLicenseValid(licenseExpiryPicker.getValue())) {
            ValidationUtils.showError("Le permis de conduire doit être valide (date d'expiration > aujourd'hui).");
            licenseExpiryPicker.requestFocus();
            return false;
        }
        
        // Validation dates de location
        if (startDatePicker.getValue() == null) {
            ValidationUtils.showError("La date de début de location est obligatoire.");
            startDatePicker.requestFocus();
            return false;
        }
        
        if (endDatePicker.getValue() == null) {
            ValidationUtils.showError("La date de fin de location est obligatoire.");
            endDatePicker.requestFocus();
            return false;
        }
        
        if (endDatePicker.getValue().isBefore(startDatePicker.getValue())) {
            ValidationUtils.showError("La date de fin doit être après la date de début.");
            endDatePicker.requestFocus();
            return false;
        }
        
        // Vérifie que la voiture est toujours disponible
        if (selectedCar == null || !selectedCar.isAvailable()) {
            ValidationUtils.showError("Cette voiture n'est plus disponible.");
            return false;
        }
        
        return true;
    }

    @FXML
    public void onCancelButtonClick() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void refreshMainView() {
        javafx.application.Platform.runLater(() -> {
        });
    }
}


