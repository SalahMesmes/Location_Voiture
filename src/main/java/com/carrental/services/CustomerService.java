package com.carrental.services;

import com.carrental.database.DatabaseManager;
import com.carrental.models.Customer;
import java.sql.*;
import java.time.LocalDate;

public class CustomerService {
    private static CustomerService instance;
    private final DatabaseManager dbManager;

    private CustomerService() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public static CustomerService getInstance() {
        if (instance == null) {
            instance = new CustomerService();
        }
        return instance;
    }


    public Customer createCustomer(String firstName, String lastName, String phoneNumber,
                                   String licenseNumber, LocalDate licenseExpiry, 
                                   String address, String email) {
        String sql = """
            INSERT INTO clients (prenom, nom, numero_telephone, numero_permis, 
                                  date_expiration_permis, adresse, email)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, phoneNumber);
            pstmt.setString(4, licenseNumber);
            pstmt.setDate(5, Date.valueOf(licenseExpiry));
            // Gère le cas où l'adresse est vide ou null
            if (address == null || address.trim().isEmpty()) {
                pstmt.setNull(6, Types.VARCHAR);
            } else {
                pstmt.setString(6, address);
            }
            pstmt.setString(7, email);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int customerId = generatedKeys.getInt(1);
                        return getCustomerById(customerId);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du client : " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la création du client : " + e.getMessage(), e);
        }
        
        return null;
    }

    public Customer getCustomerById(int customerId) {
        String sql = "SELECT * FROM clients WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return resultSetToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du client : " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    private Customer resultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getInt("id"));
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
        return customer;
    }
}

