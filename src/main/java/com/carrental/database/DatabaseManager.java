package com.carrental.database;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static DatabaseManager instance;

    // Parametres de connexion lus depuis database.properties 
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;

    // Connexion JDBC vers MySQL (reutilisee tant qu'elle est ouverte) 
    private Connection connection;

    // URLs par defaut pour MySQL en local  database.properties manquant ou invalide) 
    private static final String DB_NAME_LOCAL = "location_voitures";
    private static final String URL_3306 =
            "jdbc:mysql://localhost:3306/" + DB_NAME_LOCAL + "?useSSL=false&serverTimezone=Europe/Paris&allowPublicKeyRetrieval=true";
    private static final String URL_8889 =
            "jdbc:mysql://localhost:8889/" + DB_NAME_LOCAL + "?useSSL=false&serverTimezone=Europe/Paris&allowPublicKeyRetrieval=true";

    // Constructeur prive on ne peut pas faire "new DatabaseManager()" ailleurs 
    private DatabaseManager() {
        loadDatabaseConfig();   // Charge db.url, db.username, db.password depuis database.properties
        initializeDatabase();   // Cree les tables voitures, clients, locations si besoin
    }

    public static DatabaseManager getInstance() {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    //Lire le fichier database.properties et remplit dbUrl, dbUsername, dbPassword 
    private void loadDatabaseConfig() {
        dbUrl = URL_3306;
        dbUsername = "root";
        dbPassword = "";

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                System.err.println("database.properties introuvable dans le classpath (mets-le dans src/main/resources)");
                return;
            }

            Properties prop = new Properties();
            prop.load(input);

            String url = prop.getProperty("db.url");
            String user = prop.getProperty("db.username");
            String pass = prop.getProperty("db.password");

            if (url != null && !url.trim().isEmpty()) {
                dbUrl = url.trim();
            }

            if (user != null && !user.trim().isEmpty()) dbUsername = user.trim();
            if (pass != null) dbPassword = pass;

        } catch (Exception e) {
            System.err.println("Erreur lecture database.properties : " + e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) return connection;

        boolean isRemote = dbUrl != null && !dbUrl.contains("localhost");
        String[] urlsToTry = isRemote ? new String[]{dbUrl} : new String[]{dbUrl, URL_3306, URL_8889};

        String[] passwordsToTry = isRemote
                ? new String[]{dbPassword != null ? dbPassword : ""}
                : ((dbPassword != null && !dbPassword.isEmpty())
                    ? new String[]{dbPassword, "", "root"}
                    : new String[]{"", "root"});

        SQLException last = null;

        for (String url : urlsToTry) {
            for (String pwd : passwordsToTry) {
                System.out.println("Tentative MySQL:");
                System.out.println("   URL  : " + url);
                System.out.println("   User : " + dbUsername);
                System.out.println("   Pass : " + (pwd.isEmpty() ? "(vide)" : "(non-vide)"));

                try {
                    connection = DriverManager.getConnection(url, dbUsername, pwd);
                    System.out.println("Connexion MySQL OK !");
                    dbUrl = url;
                    dbPassword = pwd;
                    return connection;
                } catch (SQLException e) {
                    last = e;
                    System.err.println("Échec : " + e.getMessage() + " | SQLState=" + e.getSQLState() + " | Code=" + e.getErrorCode());
                }
            }
        }

        throw last != null ? last : new SQLException("Impossible de se connecter à MySQL .");
    }

    // Cree les tables si elles n'existent pas 
    private void initializeDatabase() {
        System.out.println("Initialisation de la base de données...");
        try (Connection conn = getConnection()) {

            String createCarsTable = """
                CREATE TABLE IF NOT EXISTS voitures (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    marque VARCHAR(100) NOT NULL,
                    modele VARCHAR(100) NOT NULL,
                    type_vehicule VARCHAR(50) NOT NULL,
                    url_image VARCHAR(255),
                    prix_par_jour DECIMAL(10,2) NOT NULL,
                    disponible TINYINT(1) NOT NULL DEFAULT 1,
                    annee INT NOT NULL,
                    type_carburant VARCHAR(50) NOT NULL,
                    nombre_places INT NOT NULL,
                    transmission VARCHAR(50) NOT NULL
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """;

            String createCustomersTable = """
                CREATE TABLE IF NOT EXISTS clients (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    prenom VARCHAR(100) NOT NULL,
                    nom VARCHAR(100) NOT NULL,
                    numero_telephone VARCHAR(20) NOT NULL,
                    numero_permis VARCHAR(50) NOT NULL,
                    date_expiration_permis DATE NOT NULL,
                    adresse TEXT,
                    email VARCHAR(100) NOT NULL
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """;

            String createRentalsTable = """
                CREATE TABLE IF NOT EXISTS locations (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    id_voiture INT NOT NULL,
                    id_client INT NOT NULL,
                    date_debut DATE NOT NULL,
                    date_fin DATE NOT NULL,
                    prix_total DECIMAL(10,2) NOT NULL,
                    statut VARCHAR(20) NOT NULL DEFAULT 'EN_COURS',
                    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (id_voiture) REFERENCES voitures(id) ON DELETE CASCADE,
                    FOREIGN KEY (id_client) REFERENCES clients(id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """;

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createCarsTable);
                stmt.execute(createCustomersTable);
                stmt.execute(createRentalsTable);
            }

            System.out.println("Tables OK");
        } catch (SQLException e) {
            System.err.println("Erreur init DB : " + e.getMessage());
        }
    }

    //Ferme la connexion a la base a la sortie de l'application
    public void closeConnection() {
        if (connection != null) {
            try { connection.close(); } catch (SQLException ignored) {}
        }
    }
}