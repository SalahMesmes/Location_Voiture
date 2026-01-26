package com.carrental.controllers;

import com.carrental.utils.ValidationUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminLoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admine123@";

    @FXML
    public void initialize() {
        usernameField.requestFocus();
    }

    @FXML
    private void onLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            ValidationUtils.showError("Veuillez remplir tous les champs.");
            return;
        }

        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            openAdminDashboard();
            closeWindow();
        } else {
            ValidationUtils.showError("Nom d'utilisateur ou mot de passe incorrect.");
            passwordField.clear();
        }
    }

    @FXML
    private void onCancel() {
        closeWindow();
    }

    private void openAdminDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/admin-dashboard.fxml")
            );
            Parent root = loader.load();

            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Dashboard Admin - Gestion de Location");
            stage.setScene(scene);
            stage.setMinWidth(1000);
            stage.setMinHeight(700);
            stage.show();
        } catch (IOException e) {
            ValidationUtils.showError("Erreur lors de l'ouverture du dashboard admin : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
}

