package com.carrental.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Tests de validation des données")
public class ValidationUtilsTest {



    @Test
    @DisplayName("Numéro de téléphone valide avec 10 chiffres")
    public void testNumeroTelephoneValide_10Chiffres() {
     
        String telephone = "0612345678";
        
        boolean resultat = ValidationUtils.estNumeroTelephoneValide(telephone);
        
        assertTrue(resultat, "Un numéro de 10 chiffres devrait être valide");
    }

    @Test
    @DisplayName("Numéro de téléphone valide avec espaces")
    public void testNumeroTelephoneValide_AvecEspaces() {
        String telephone = "06 12 34 56 78";
        boolean resultat = ValidationUtils.estNumeroTelephoneValide(telephone);
        assertTrue(resultat, "Les espaces devraient être ignorés");
    }

    @Test
    @DisplayName("Numéro de téléphone valide avec tirets")
    public void testNumeroTelephoneValide_AvecTirets() {
        String telephone = "06-12-34-56-78";
        boolean resultat = ValidationUtils.estNumeroTelephoneValide(telephone);
        assertTrue(resultat, "Les tirets devraient être ignorés");
    }

    @Test
    @DisplayName("Numéro de téléphone valide avec points")
    public void testNumeroTelephoneValide_AvecPoints() {
        String telephone = "06.12.34.56.78";
        boolean resultat = ValidationUtils.estNumeroTelephoneValide(telephone);
        assertTrue(resultat, "Les points devraient être ignorés");
    }

    @Test
    @DisplayName("Numéro de téléphone invalide - trop court")
    public void testNumeroTelephoneInvalide_TropCourt() {
        String telephone = "061234567"; 
        boolean resultat = ValidationUtils.estNumeroTelephoneValide(telephone);
        assertFalse(resultat, "Un numéro avec moins de 10 chiffres devrait être invalide");
    }

    @Test
    @DisplayName("Numéro de téléphone invalide - trop long")
    public void testNumeroTelephoneInvalide_TropLong() {
        String telephone = "06123456789"; 
        boolean resultat = ValidationUtils.estNumeroTelephoneValide(telephone);
        assertFalse(resultat, "Un numéro avec plus de 10 chiffres devrait être invalide");
    }

    @Test
    @DisplayName("Numéro de téléphone invalide - contient des lettres")
    public void testNumeroTelephoneInvalide_AvecLettres() {
        String telephone = "06ABC45678";
        boolean resultat = ValidationUtils.estNumeroTelephoneValide(telephone);
        assertFalse(resultat, "Un numéro contenant des lettres devrait être invalide");
    }

    @Test
    @DisplayName("Numéro de téléphone invalide - null")
    public void testNumeroTelephoneInvalide_Null() {
        String telephone = null;
        boolean resultat = ValidationUtils.estNumeroTelephoneValide(telephone);
        assertFalse(resultat, "Un numéro null devrait être invalide");
    }

    @Test
    @DisplayName("Numéro de téléphone invalide - vide")
    public void testNumeroTelephoneInvalide_Vide() {
        String telephone = "";
        boolean resultat = ValidationUtils.estNumeroTelephoneValide(telephone);
        assertFalse(resultat, "Un numéro vide devrait être invalide");
    }


    @Test
    @DisplayName("Email valide - format standard")
    public void testEmailValide_FormatStandard() {
        String email = "test@example.com";
        boolean resultat = ValidationUtils.estEmailValide(email);
        assertTrue(resultat, "Un email avec format standard devrait être valide");
    }

    @Test
    @DisplayName("Email valide - avec sous-domaine")
    public void testEmailValide_AvecSousDomaine() {
        String email = "user@mail.example.com";
        boolean resultat = ValidationUtils.estEmailValide(email);
        assertTrue(resultat, "Un email avec sous-domaine devrait être valide");
    }

    @Test
    @DisplayName("Email valide - avec caractères spéciaux")
    public void testEmailValide_AvecCaracteresSpeciaux() {
        String email = "test.user+tag@example.com";
        boolean resultat = ValidationUtils.estEmailValide(email);
        assertTrue(resultat, "Un email avec caractères spéciaux autorisés devrait être valide");
    }

    @Test
    @DisplayName("Email invalide - sans @")
    public void testEmailInvalide_SansArobase() {
        String email = "testexample.com";
        boolean resultat = ValidationUtils.estEmailValide(email);
        assertFalse(resultat, "Un email sans @ devrait être invalide");
    }

    @Test
    @DisplayName("Email invalide - sans point après @")
    public void testEmailInvalide_SansPoint() {
        String email = "test@examplecom";
        boolean resultat = ValidationUtils.estEmailValide(email);
        assertFalse(resultat, "Un email sans point après @ devrait être invalide");
    }

    @Test
    @DisplayName("Email invalide - @ au début")
    public void testEmailInvalide_ArobaseAuDebut() {
        String email = "@example.com";
        boolean resultat = ValidationUtils.estEmailValide(email);
        assertFalse(resultat, "Un email avec @ au début devrait être invalide");
    }

    @Test
    @DisplayName("Email invalide - @ à la fin")
    public void testEmailInvalide_ArobaseALaFin() {
        String email = "test@";
        boolean resultat = ValidationUtils.estEmailValide(email);
        assertFalse(resultat, "Un email avec @ à la fin devrait être invalide");
    }

    @Test
    @DisplayName("Email invalide - null")
    public void testEmailInvalide_Null() {
        String email = null;
        boolean resultat = ValidationUtils.estEmailValide(email);
        assertFalse(resultat, "Un email null devrait être invalide");
    }

    @Test
    @DisplayName("Email invalide - vide")
    public void testEmailInvalide_Vide() {
        String email = "";
        boolean resultat = ValidationUtils.estEmailValide(email);
        assertFalse(resultat, "Un email vide devrait être invalide");
    }


    @Test
    @DisplayName("Numéro de permis valide - lettres et chiffres")
    public void testNumeroPermisValide_LettresEtChiffres() {
        String numeroPermis = "AB123CD";
        boolean resultat = ValidationUtils.estNumeroPermisValide(numeroPermis);
        assertTrue(resultat, "Un numéro de permis avec lettres et chiffres devrait être valide");
    }

    @Test
    @DisplayName("Numéro de permis valide - uniquement lettres")
    public void testNumeroPermisValide_UniquementLettres() {
        String numeroPermis = "ABCDEF";
        boolean resultat = ValidationUtils.estNumeroPermisValide(numeroPermis);
        assertTrue(resultat, "Un numéro de permis avec uniquement des lettres devrait être valide");
    }

    @Test
    @DisplayName("Numéro de permis valide - uniquement chiffres")
    public void testNumeroPermisValide_UniquementChiffres() {
        String numeroPermis = "123456";
        boolean resultat = ValidationUtils.estNumeroPermisValide(numeroPermis);
        assertTrue(resultat, "Un numéro de permis avec uniquement des chiffres devrait être valide");
    }

    @Test
    @DisplayName("Numéro de permis invalide - avec caractères spéciaux")
    public void testNumeroPermisInvalide_AvecCaracteresSpeciaux() {
        String numeroPermis = "AB-123";
        boolean resultat = ValidationUtils.estNumeroPermisValide(numeroPermis);
        assertFalse(resultat, "Un numéro de permis avec caractères spéciaux devrait être invalide");
    }

    @Test
    @DisplayName("Numéro de permis invalide - avec espaces")
    public void testNumeroPermisInvalide_AvecEspaces() {
        String numeroPermis = "AB 123";
        boolean resultat = ValidationUtils.estNumeroPermisValide(numeroPermis);
        assertFalse(resultat, "Un numéro de permis avec espaces devrait être invalide");
    }

    @Test
    @DisplayName("Numéro de permis invalide - null")
    public void testNumeroPermisInvalide_Null() {
        String numeroPermis = null;
        boolean resultat = ValidationUtils.estNumeroPermisValide(numeroPermis);
        assertFalse(resultat, "Un numéro de permis null devrait être invalide");
    }

    @Test
    @DisplayName("Numéro de permis invalide - vide")
    public void testNumeroPermisInvalide_Vide() {
        String numeroPermis = "";
        boolean resultat = ValidationUtils.estNumeroPermisValide(numeroPermis);
        assertFalse(resultat, "Un numéro de permis vide devrait être invalide");
    }


    @Test
    @DisplayName("Permis valide - date dans le futur")
    public void testPermisValide_DateFutur() {
        LocalDate dateExpiration = LocalDate.now().plusYears(2);
        boolean resultat = ValidationUtils.estPermisValide(dateExpiration);
        assertTrue(resultat, "Un permis avec date d'expiration dans le futur devrait être valide");
    }

    @Test
    @DisplayName("Permis invalide - date dans le passé")
    public void testPermisInvalide_DatePasse() {
        LocalDate dateExpiration = LocalDate.now().minusYears(1);
        boolean resultat = ValidationUtils.estPermisValide(dateExpiration);
        assertFalse(resultat, "Un permis avec date d'expiration dans le passé devrait être invalide");
    }

    @Test
    @DisplayName("Permis invalide - date aujourd'hui")
    public void testPermisInvalide_DateAujourdhui() {
        LocalDate dateExpiration = LocalDate.now();
        boolean resultat = ValidationUtils.estPermisValide(dateExpiration);
        assertFalse(resultat, "Un permis expirant aujourd'hui devrait être invalide");
    }

    @Test
    @DisplayName("Permis invalide - null")
    public void testPermisInvalide_Null() {
        LocalDate dateExpiration = null;
        boolean resultat = ValidationUtils.estPermisValide(dateExpiration);
        assertFalse(resultat, "Un permis avec date null devrait être invalide");
    }

    @Test
    @DisplayName("Texte non vide - avec contenu")
    public void testNestPasVide_AvecContenu() {
        String texte = "Bonjour";
        boolean resultat = ValidationUtils.nestPasVide(texte);
        assertTrue(resultat, "Un texte avec contenu ne devrait pas être vide");
    }

    @Test
    @DisplayName("Texte vide - chaîne vide")
    public void testNestPasVide_ChaineVide() {
        String texte = "";
        boolean resultat = ValidationUtils.nestPasVide(texte);
        assertFalse(resultat, "Une chaîne vide devrait être considérée comme vide");
    }

    @Test
    @DisplayName("Texte vide - uniquement espaces")
    public void testNestPasVide_UniquementEspaces() {
        String texte = "   ";
        boolean resultat = ValidationUtils.nestPasVide(texte);
        assertFalse(resultat, "Un texte avec uniquement des espaces devrait être considéré comme vide");
    }

    @Test
    @DisplayName("Texte vide - null")
    public void testNestPasVide_Null() {
        String texte = null;
        boolean resultat = ValidationUtils.nestPasVide(texte);
        assertFalse(resultat, "Un texte null devrait être considéré comme vide");
    }

    @Test
    @DisplayName("Texte non vide - avec espaces avant/après")
    public void testNestPasVide_AvecEspaces() {
        String texte = "  Bonjour  ";
        boolean resultat = ValidationUtils.nestPasVide(texte);
        assertTrue(resultat, "Un texte avec espaces mais du contenu ne devrait pas être vide");
    }
}
