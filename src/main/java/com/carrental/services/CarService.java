package com.carrental.services;

import com.carrental.database.DatabaseManager;
import com.carrental.models.Car;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class CarService {
    private static CarService instance;
    private final DatabaseManager dbManager;

    public CarService() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public static CarService getInstance() {
        if (instance == null) {
            instance = new CarService();
        }
        return instance;
    }

    public ObservableList<Car> getAllCars() {
        ObservableList<Car> cars = FXCollections.observableArrayList();
        String sql = "SELECT * FROM voitures ORDER BY marque, modele";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int count = 0;
            while (rs.next()) {
                cars.add(resultSetToCar(rs));
                count++;
            }
            
            if (count == 0) {
                System.out.println("ATTENTION : Aucune voiture trouvée dans la base de données.");
                System.out.println("ASTUCE : Vérifiez dans phpMyAdmin que la table 'voitures' contient des données.");
                System.out.println("ASTUCE : Ou exécutez le script script_sql.sql dans phpMyAdmin.");
            } else {
                System.out.println(" " + count + " voiture(s) chargée(s) depuis MySQL");
            }
        } catch (SQLException e) {
            System.err.println(" ERREUR lors de la récupération des voitures : " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("\nVérifiez :");
            System.err.println("1. MySQL est démarré");
            System.err.println("2. La base 'location_voitures' existe");
            System.err.println("3. La table 'voitures' existe");
            System.err.println("4. Les identifiants dans database.properties sont corrects");
            e.printStackTrace();
            
            // Affiche une alerte à l'utilisateur
            javafx.application.Platform.runLater(() -> {
                com.carrental.utils.ValidationUtils.showError(
                    "Erreur de connexion à la base de données.\n\n" +
                    "Vérifiez que MySQL est démarré et que la base 'location_voitures' existe.\n" +
                    "Consultez la console pour plus de détails."
                );
            });
        }
        
        return cars;
    }

    public ObservableList<Car> getAvailableCars() {
        ObservableList<Car> cars = FXCollections.observableArrayList();
        String sql = "SELECT * FROM voitures WHERE disponible = 1 ORDER BY marque, modele";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                cars.add(resultSetToCar(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des voitures disponibles : " + e.getMessage());
            e.printStackTrace();
        }
        
        return cars;
    }

    public ObservableList<Car> searchCarsByType(String type) {
        if (type == null || type.isEmpty() || type.equals("Tous")) {
            return getAllCars();
        }
        
        ObservableList<Car> cars = FXCollections.observableArrayList();
        String sql = "SELECT * FROM voitures WHERE type_vehicule = ? ORDER BY marque, modele";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, type);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    cars.add(resultSetToCar(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par type : " + e.getMessage());
            e.printStackTrace();
        }
        
        return cars;
    }

    public java.util.List<Car> searchCars(String searchText, String type) {
        ObservableList<Car> result;
        
        // Si pas de texte de recherche, on filtre juste par type
        if (searchText == null || searchText.trim().isEmpty()) {
            if (type == null || type.isEmpty() || type.equals("Tous")) {
                result = getAllCars();
            } else {
                result = searchCarsByType(type);
            }
        } else {
            // Recherche par texte ET type
            result = FXCollections.observableArrayList();
            String sql;
            if (type == null || type.isEmpty() || type.equals("Tous")) {
                sql = "SELECT * FROM voitures WHERE (LOWER(marque) LIKE ? OR LOWER(modele) LIKE ?) ORDER BY marque, modele";
            } else {
                sql = "SELECT * FROM voitures WHERE (LOWER(marque) LIKE ? OR LOWER(modele) LIKE ?) AND type_vehicule = ? ORDER BY marque, modele";
            }
            
            try (Connection conn = dbManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                String searchPattern = "%" + searchText.toLowerCase().trim() + "%";
                pstmt.setString(1, searchPattern);
                pstmt.setString(2, searchPattern);
                if (type != null && !type.isEmpty() && !type.equals("Tous")) {
                    pstmt.setString(3, type);
                }
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        result.add(resultSetToCar(rs));
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la recherche : " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return new java.util.ArrayList<>(result);
    }

    public void updateCarAvailability(int carId, boolean available) {
        String sql = "UPDATE voitures SET disponible = ? WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, available ? 1 : 0);
            pstmt.setInt(2, carId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la disponibilité : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Car getCarById(int carId) {
        String sql = "SELECT * FROM voitures WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, carId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return resultSetToCar(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la voiture : " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    public boolean addCar(Car car) {
        String sql = """
            INSERT INTO voitures (marque, modele, type_vehicule, url_image, prix_par_jour, disponible, annee, type_carburant, nombre_places, transmission)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, car.getBrand());
            pstmt.setString(2, car.getModel());
            pstmt.setString(3, car.getType());
            pstmt.setString(4, car.getImageUrl());
            pstmt.setDouble(5, car.getPricePerDay());
            pstmt.setInt(6, car.isAvailable() ? 1 : 0);
            pstmt.setInt(7, car.getYear());
            pstmt.setString(8, car.getFuelType());
            pstmt.setInt(9, car.getNumberOfSeats());
            pstmt.setString(10, car.getTransmission());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        car.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la voiture : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    public boolean updateCar(Car car) {
        String sql = """
            UPDATE voitures 
            SET marque = ?, modele = ?, type_vehicule = ?, url_image = ?, 
                prix_par_jour = ?, disponible = ?, annee = ?, type_carburant = ?, 
                nombre_places = ?, transmission = ?
            WHERE id = ?
        """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, car.getBrand());
            pstmt.setString(2, car.getModel());
            pstmt.setString(3, car.getType());
            pstmt.setString(4, car.getImageUrl());
            pstmt.setDouble(5, car.getPricePerDay());
            pstmt.setInt(6, car.isAvailable() ? 1 : 0);
            pstmt.setInt(7, car.getYear());
            pstmt.setString(8, car.getFuelType());
            pstmt.setInt(9, car.getNumberOfSeats());
            pstmt.setString(10, car.getTransmission());
            pstmt.setInt(11, car.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la voiture : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    public boolean deleteCar(int carId) {
        String sql = "DELETE FROM voitures WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, carId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la voiture : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    private Car resultSetToCar(ResultSet rs) throws SQLException {
        Car car = new Car();
        car.setId(rs.getInt("id"));
        car.setBrand(rs.getString("marque"));
        car.setModel(rs.getString("modele"));
        car.setType(rs.getString("type_vehicule"));
        car.setImageUrl(rs.getString("url_image"));
        car.setPricePerDay(rs.getDouble("prix_par_jour"));
        car.setAvailable(rs.getInt("disponible") == 1);
        car.setYear(rs.getInt("annee"));
        car.setFuelType(rs.getString("type_carburant"));
        car.setNumberOfSeats(rs.getInt("nombre_places"));
        car.setTransmission(rs.getString("transmission"));
        return car;
    }
}
