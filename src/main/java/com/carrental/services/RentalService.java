package com.carrental.services;

import com.carrental.database.DatabaseManager;
import com.carrental.models.Car;
import com.carrental.models.Customer;
import com.carrental.models.Rental;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


public class RentalService {
    private static RentalService instance;
    private final DatabaseManager dbManager;
    private final CarService carService;

    private RentalService() {
        this.dbManager = DatabaseManager.getInstance();
        this.carService = CarService.getInstance();
    }

    public static RentalService getInstance() {
        if (instance == null) {
            instance = new RentalService();
        }
        return instance;
    }

    public Rental createRental(Car car, Customer customer, LocalDate startDate, LocalDate endDate) {
        double totalPrice = calculateTotalPrice(car, startDate, endDate);
        
        String sql = """
            INSERT INTO locations (id_voiture, id_client, date_debut, date_fin, prix_total, statut, date_creation)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, car.getId());
            pstmt.setInt(2, customer.getId());
            pstmt.setDate(3, Date.valueOf(startDate));
            pstmt.setDate(4, Date.valueOf(endDate));
            pstmt.setDouble(5, totalPrice);
            pstmt.setString(6, Rental.RentalStatus.EN_COURS.name());
            pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // MySQL supporte getGeneratedKeys()
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int rentalId = generatedKeys.getInt(1);
                        return getRentalById(rentalId);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la location : " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    public boolean returnRental(int rentalId) {
        String sql = """
            UPDATE locations SET statut = ? WHERE id = ?
        """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, Rental.RentalStatus.TERMINÉE.name());
            pstmt.setInt(2, rentalId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Récupère la location pour obtenir l'ID de la voiture
                Rental rental = getRentalById(rentalId);
                if (rental != null && rental.getCar() != null) {
                    // Remet la voiture disponible
                    carService.updateCarAvailability(rental.getCar().getId(), true);
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du retour de la location : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    public double calculateTotalPrice(Car car, LocalDate startDate, LocalDate endDate) {
        if (car == null || startDate == null || endDate == null) {
            return 0.0;
        }
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1; // +1 pour inclure le jour de fin
        if (days < 1) {
            return 0.0;
        }
        return days * car.getPricePerDay();
    }

    public ObservableList<Rental> getRentalHistory() {
        ObservableList<Rental> rentals = FXCollections.observableArrayList();
        String sql = """
            SELECT r.id, r.id_voiture, r.id_client, r.date_debut, r.date_fin, r.prix_total, r.statut, r.date_creation,
                   c.prenom, c.nom, c.numero_telephone, c.numero_permis, c.date_expiration_permis, c.adresse, c.email,
                   car.marque, car.modele, car.type_vehicule, car.url_image, car.prix_par_jour, car.disponible, 
                   car.annee, car.type_carburant, car.nombre_places, car.transmission
            FROM locations r
            JOIN clients c ON r.id_client = c.id
            JOIN voitures car ON r.id_voiture = car.id
            ORDER BY r.date_creation DESC
        """;
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                rentals.add(resultSetToRental(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'historique : " + e.getMessage());
            e.printStackTrace();
        }
        
        return rentals;
    }

    public ObservableList<Rental> getActiveRentals() {
        ObservableList<Rental> rentals = FXCollections.observableArrayList();
        String sql = """
            SELECT r.id, r.id_voiture, r.id_client, r.date_debut, r.date_fin, r.prix_total, r.statut, r.date_creation,
                   c.prenom, c.nom, c.numero_telephone, c.numero_permis, c.date_expiration_permis, c.adresse, c.email,
                   car.marque, car.modele, car.type_vehicule, car.url_image, car.prix_par_jour, car.disponible, 
                   car.annee, car.type_carburant, car.nombre_places, car.transmission
            FROM locations r
            JOIN clients c ON r.id_client = c.id
            JOIN voitures car ON r.id_voiture = car.id
            WHERE r.statut = 'EN_COURS'
            ORDER BY r.date_creation DESC
        """;
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                rentals.add(resultSetToRental(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des locations actives : " + e.getMessage());
            e.printStackTrace();
        }
        
        return rentals;
    }


    public Rental getRentalById(int rentalId) {
        String sql = """
            SELECT r.id, r.id_voiture, r.id_client, r.date_debut, r.date_fin, r.prix_total, r.statut, r.date_creation,
                   c.prenom, c.nom, c.numero_telephone, c.numero_permis, c.date_expiration_permis, c.adresse, c.email,
                   car.marque, car.modele, car.type_vehicule, car.url_image, car.prix_par_jour, car.disponible, 
                   car.annee, car.type_carburant, car.nombre_places, car.transmission
            FROM locations r
            JOIN clients c ON r.id_client = c.id
            JOIN voitures car ON r.id_voiture = car.id
            WHERE r.id = ?
        """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, rentalId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return resultSetToRental(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la location : " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }


    private Rental resultSetToRental(ResultSet rs) throws SQLException {
        Rental rental = new Rental();
        rental.setId(rs.getInt("id"));
        
        Car car = new Car();
        car.setId(rs.getInt("id_voiture"));
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
        rental.setCar(car);
        
        // Crée l'objet Customer
        Customer customer = new Customer();
        customer.setId(rs.getInt("id_client"));
        customer.setFirstName(rs.getString("prenom"));
        customer.setLastName(rs.getString("nom"));
        customer.setPhoneNumber(rs.getString("numero_telephone"));
        customer.setLicenseNumber(rs.getString("numero_permis"));
        Date expiryDate = rs.getDate("date_expiration_permis");
        if (expiryDate != null) {
            customer.setLicenseExpiry(expiryDate.toLocalDate());
        }
        customer.setAddress(rs.getString("adresse"));
        customer.setEmail(rs.getString("email"));
        rental.setCustomer(customer);
        
        // Dates et prix
        Date startDate = rs.getDate("date_debut");
        if (startDate != null) {
            rental.setStartDate(startDate.toLocalDate());
        }
        Date endDate = rs.getDate("date_fin");
        if (endDate != null) {
            rental.setEndDate(endDate.toLocalDate());
        }
        rental.setTotalPrice(rs.getDouble("prix_total"));
        
        // Status
        String statusStr = rs.getString("statut");
        if (statusStr != null) {
            try {
                rental.setStatus(Rental.RentalStatus.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                rental.setStatus(Rental.RentalStatus.EN_COURS);
            }
        }
        
        // temps de creation
        Timestamp createdAt = rs.getTimestamp("date_creation");
        if (createdAt != null) {
            rental.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return rental;
    }
}
