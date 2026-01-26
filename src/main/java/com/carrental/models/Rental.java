package com.carrental.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javafx.beans.property.*;

public class Rental {
    public enum RentalStatus {
        EN_COURS, TERMINÉE, ANNULÉE
    }

    private final IntegerProperty id;
    private final ObjectProperty<Car> car;
    private final ObjectProperty<Customer> customer;
    private final ObjectProperty<LocalDate> startDate;
    private final ObjectProperty<LocalDate> endDate;
    private final DoubleProperty totalPrice;
    private final ObjectProperty<RentalStatus> status;
    private final ObjectProperty<LocalDateTime> createdAt;

    public Rental() {
        this.id = new SimpleIntegerProperty();
        this.car = new SimpleObjectProperty<>();
        this.customer = new SimpleObjectProperty<>();
        this.startDate = new SimpleObjectProperty<>();
        this.endDate = new SimpleObjectProperty<>();
        this.totalPrice = new SimpleDoubleProperty();
        this.status = new SimpleObjectProperty<>(RentalStatus.EN_COURS);
        this.createdAt = new SimpleObjectProperty<>(LocalDateTime.now());
    }

    public Rental(int id, Car car, Customer customer, LocalDate startDate, 
                  LocalDate endDate, double totalPrice, RentalStatus status, LocalDateTime createdAt) {
        this.id = new SimpleIntegerProperty(id);
        this.car = new SimpleObjectProperty<>(car);
        this.customer = new SimpleObjectProperty<>(customer);
        this.startDate = new SimpleObjectProperty<>(startDate);
        this.endDate = new SimpleObjectProperty<>(endDate);
        this.totalPrice = new SimpleDoubleProperty(totalPrice);
        this.status = new SimpleObjectProperty<>(status);
        this.createdAt = new SimpleObjectProperty<>(createdAt);
    }

    // Getters pour les propriétés JavaFX
    public IntegerProperty idProperty() { return id; }
    public ObjectProperty<Car> carProperty() { return car; }
    public ObjectProperty<Customer> customerProperty() { return customer; }
    public ObjectProperty<LocalDate> startDateProperty() { return startDate; }
    public ObjectProperty<LocalDate> endDateProperty() { return endDate; }
    public DoubleProperty totalPriceProperty() { return totalPrice; }
    public ObjectProperty<RentalStatus> statusProperty() { return status; }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }

    // Getters standards
    public int getId() { return id.get(); }
    public Car getCar() { return car.get(); }
    public Customer getCustomer() { return customer.get(); }
    public LocalDate getStartDate() { return startDate.get(); }
    public LocalDate getEndDate() { return endDate.get(); }
    public double getTotalPrice() { return totalPrice.get(); }
    public RentalStatus getStatus() { return status.get(); }
    public LocalDateTime getCreatedAt() { return createdAt.get(); }

    // Setters
    public void setId(int id) { this.id.set(id); }
    public void setCar(Car car) { this.car.set(car); }
    public void setCustomer(Customer customer) { this.customer.set(customer); }
    public void setStartDate(LocalDate startDate) { this.startDate.set(startDate); }
    public void setEndDate(LocalDate endDate) { this.endDate.set(endDate); }
    public void setTotalPrice(double totalPrice) { this.totalPrice.set(totalPrice); }
    public void setStatus(RentalStatus status) { this.status.set(status); }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt.set(createdAt); }
}





