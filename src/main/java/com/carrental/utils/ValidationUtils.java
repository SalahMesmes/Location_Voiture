package com.carrental.utils;

import javafx.scene.control.Alert;
import java.time.LocalDate;

public class ValidationUtils {

  
    public static boolean estNumeroTelephoneValide(String telephone) {
        if (telephone == null || telephone.trim().isEmpty()) {
            return false;
        }
        
        // Supprime les espaces, tirets et points
        String numeroNettoye = telephone.trim();
        numeroNettoye = numeroNettoye.replace(" ", "");
        numeroNettoye = numeroNettoye.replace("-", "");
        numeroNettoye = numeroNettoye.replace(".", "");
        
        // Vérifie que le numéro contient exactement 10 caractères
        if (numeroNettoye.length() != 10) {
            return false;
        }
        
        // Vérifie que tous les caractères sont des chiffres
        for (int i = 0; i < numeroNettoye.length(); i++) {
            char caractere = numeroNettoye.charAt(i);
            if (caractere < '0' || caractere > '9') {
                return false;
            }
        }
        
        return true;
    }

 
    public static boolean estNumeroPermisValide(String numeroPermis) {
        if (numeroPermis == null || numeroPermis.trim().isEmpty()) {
            return false;
        }
        
        String numeroNettoye = numeroPermis.trim();
        
        // Vérifie chaque caractère
        for (int i = 0; i < numeroNettoye.length(); i++) {
            char caractere = numeroNettoye.charAt(i);
            
            // Vérifie si c'est une lettre (A-Z ou a-z)
            boolean estLettre = (caractere >= 'A' && caractere <= 'Z') || 
                               (caractere >= 'a' && caractere <= 'z');
            
            // Vérifie si c'est un chiffre (0-9)
            boolean estChiffre = (caractere >= '0' && caractere <= '9');
            
            // Si ce n'est ni une lettre ni un chiffre, c'est invalide
            if (!estLettre && !estChiffre) {
                return false;
            }
        }
        
        return true;
    }

    public static boolean estEmailValide(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String emailNettoye = email.trim();
        
        // Vérifie qu'il y a exactement un @
        int nombreArobase = 0;
        int positionArobase = -1;
        for (int i = 0; i < emailNettoye.length(); i++) {
            if (emailNettoye.charAt(i) == '@') {
                nombreArobase++;
                positionArobase = i;
            }
        }
        
        // Il doit y avoir exactement un @
        if (nombreArobase != 1) {
            return false;
        }
        
        // Il doit y avoir au moins un caractère avant le @
        if (positionArobase == 0) {
            return false;
        }
        
        // Il doit y avoir  un caractère après le @
        if (positionArobase == emailNettoye.length() - 1) {
            return false;
        }
        
        // Vérifie qun point après le @
        String partieApresArobase = emailNettoye.substring(positionArobase + 1);
        if (!partieApresArobase.contains(".")) {
            return false;
        }
        
        // Vérifie que le point n'est pas le dernier caractère
        if (partieApresArobase.endsWith(".")) {
            return false;
        }
        
        // Vérifie que les caractères sont valides 
        for (int i = 0; i < emailNettoye.length(); i++) {
            char caractere = emailNettoye.charAt(i);
            
            // Caractères autorisés : lettres, chiffres, @, +, _, ., -
            boolean estLettre = (caractere >= 'A' && caractere <= 'Z') || 
                               (caractere >= 'a' && caractere <= 'z');
            boolean estChiffre = (caractere >= '0' && caractere <= '9');
            boolean estCaractereSpecial = caractere == '@' || caractere == '+' || 
                                        caractere == '_' || caractere == '.' || 
                                        caractere == '-';
            
            if (!estLettre && !estChiffre && !estCaractereSpecial) {
                return false;
            }
        }
        
        return true;
    }

  
    public static boolean estPermisValide(LocalDate dateExpiration) {
        if (dateExpiration == null) {
            return false;
        }
        
        LocalDate aujourdhui = LocalDate.now();
        return dateExpiration.isAfter(aujourdhui);
    }

    
    public static boolean nestPasVide(String texte) {
        return texte != null && !texte.trim().isEmpty();
    }

    
    public static void afficherAlerte(String titre, String message, Alert.AlertType type) {
        Alert alerte = new Alert(type);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(message);
        alerte.showAndWait();
    }

    
    public static void afficherErreur(String message) {
        afficherAlerte("Erreur", message, Alert.AlertType.ERROR);
    }

   
    public static void afficherSucces(String message) {
        afficherAlerte("Succès", message, Alert.AlertType.INFORMATION);
    }
 
    
    public static boolean isValidPhoneNumber(String phone) {
        return estNumeroTelephoneValide(phone);
    }
    
    public static boolean isValidLicenseNumber(String license) {
        return estNumeroPermisValide(license);
    }
    
    public static boolean isValidEmail(String email) {
        return estEmailValide(email);
    }
    
    public static boolean isLicenseValid(LocalDate expiry) {
        return estPermisValide(expiry);
    }
    
    public static boolean isNotEmpty(String text) {
        return nestPasVide(text);
    }
    
    public static void showError(String message) {
        afficherErreur(message);
    }
    
    public static void showSuccess(String message) {
        afficherSucces(message);
    }
}





