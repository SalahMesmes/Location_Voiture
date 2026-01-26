package com.carrental.models;

import java.time.LocalDate;
import javafx.beans.property.*;

public class Customer {
    private final IntegerProperty id;
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty phoneNumber;
    private final StringProperty licenseNumber;
    private final ObjectProperty<LocalDate> licenseExpiry;
    private final StringProperty address;
    private final StringProperty email;

    public Customer() {
        this.id = new SimpleIntegerProperty();
        this.firstName = new SimpleStringProperty();
        this.lastName = new SimpleStringProperty();
        this.phoneNumber = new SimpleStringProperty();
        this.licenseNumber = new SimpleStringProperty();
        this.licenseExpiry = new SimpleObjectProperty<>();
        this.address = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
    }

    public Customer(int id, String firstName, String lastName, String phoneNumber, 
                   String licenseNumber, LocalDate licenseExpiry, String address, String email) {
        this.id = new SimpleIntegerProperty(id);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.licenseNumber = new SimpleStringProperty(licenseNumber);
        this.licenseExpiry = new SimpleObjectProperty<>(licenseExpiry);
        this.address = new SimpleStringProperty(address);
        this.email = new SimpleStringProperty(email);
    }

    // Getters 
    public IntegerProperty idProperty() { return id; }
    public StringProperty firstNameProperty() { return firstName; }
    public StringProperty lastNameProperty() { return lastName; }
    public StringProperty phoneNumberProperty() { return phoneNumber; }
    public StringProperty licenseNumberProperty() { return licenseNumber; }
    public ObjectProperty<LocalDate> licenseExpiryProperty() { return licenseExpiry; }
    public StringProperty addressProperty() { return address; }
    public StringProperty emailProperty() { return email; }

    public int getId() { return id.get(); }
    public String getFirstName() { return firstName.get(); }
    public String getLastName() { return lastName.get(); }
    public String getPhoneNumber() { return phoneNumber.get(); }
    public String getLicenseNumber() { return licenseNumber.get(); }
    public LocalDate getLicenseExpiry() { return licenseExpiry.get(); }
    public String getAddress() { return address.get(); }
    public String getEmail() { return email.get(); }

    // Setters
    public void setId(int id) { this.id.set(id); }
    public void setFirstName(String firstName) { this.firstName.set(firstName); }
    public void setLastName(String lastName) { this.lastName.set(lastName); }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber.set(phoneNumber); }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber.set(licenseNumber); }
    public void setLicenseExpiry(LocalDate licenseExpiry) { this.licenseExpiry.set(licenseExpiry); }
    public void setAddress(String address) { this.address.set(address); }
    public void setEmail(String email) { this.email.set(email); }

    @Override
    public String toString() {
        return String.format("%s %s - %s", firstName.get(), lastName.get(), phoneNumber.get());
    }
}





