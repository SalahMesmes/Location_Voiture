package com.carrental.models;

import javafx.beans.property.*;

public class Car {
    private final IntegerProperty id;
    private final StringProperty brand;
    private final StringProperty model;
    private final StringProperty type;
    private final StringProperty imageUrl;
    private final DoubleProperty pricePerDay;
    private final BooleanProperty available;
    private final IntegerProperty year;
    private final StringProperty fuelType;
    private final IntegerProperty numberOfSeats;
    private final StringProperty transmission;

    public Car() {
        this.id = new SimpleIntegerProperty();
        this.brand = new SimpleStringProperty();
        this.model = new SimpleStringProperty();
        this.type = new SimpleStringProperty();
        this.imageUrl = new SimpleStringProperty();
        this.pricePerDay = new SimpleDoubleProperty();
        this.available = new SimpleBooleanProperty(true);
        this.year = new SimpleIntegerProperty();
        this.fuelType = new SimpleStringProperty();
        this.numberOfSeats = new SimpleIntegerProperty();
        this.transmission = new SimpleStringProperty();
    }

    public Car(int id, String brand, String model, String type, String imageUrl, 
               double pricePerDay, boolean available, int year, String fuelType, 
               int numberOfSeats, String transmission) {
        this.id = new SimpleIntegerProperty(id);
        this.brand = new SimpleStringProperty(brand);
        this.model = new SimpleStringProperty(model);
        this.type = new SimpleStringProperty(type);
        this.imageUrl = new SimpleStringProperty(imageUrl);
        this.pricePerDay = new SimpleDoubleProperty(pricePerDay);
        this.available = new SimpleBooleanProperty(available);
        this.year = new SimpleIntegerProperty(year);
        this.fuelType = new SimpleStringProperty(fuelType);
        this.numberOfSeats = new SimpleIntegerProperty(numberOfSeats);
        this.transmission = new SimpleStringProperty(transmission);
    }

    // Getters 
    public IntegerProperty idProperty() { return id; }
    public StringProperty brandProperty() { return brand; }
    public StringProperty modelProperty() { return model; }
    public StringProperty typeProperty() { return type; }
    public StringProperty imageUrlProperty() { return imageUrl; }
    public DoubleProperty pricePerDayProperty() { return pricePerDay; }
    public BooleanProperty availableProperty() { return available; }
    public IntegerProperty yearProperty() { return year; }
    public StringProperty fuelTypeProperty() { return fuelType; }
    public IntegerProperty numberOfSeatsProperty() { return numberOfSeats; }
    public StringProperty transmissionProperty() { return transmission; }

   
    public int getId() { return id.get(); }
    public String getBrand() { return brand.get(); }
    public String getModel() { return model.get(); }
    public String getType() { return type.get(); }
    public String getImageUrl() { return imageUrl.get(); }
    public double getPricePerDay() { return pricePerDay.get(); }
    public boolean isAvailable() { return available.get(); }
    public int getYear() { return year.get(); }
    public String getFuelType() { return fuelType.get(); }
    public int getNumberOfSeats() { return numberOfSeats.get(); }
    public String getTransmission() { return transmission.get(); }

    // Setters
    public void setId(int id) { this.id.set(id); }
    public void setBrand(String brand) { this.brand.set(brand); }
    public void setModel(String model) { this.model.set(model); }
    public void setType(String type) { this.type.set(type); }
    public void setImageUrl(String imageUrl) { this.imageUrl.set(imageUrl); }
    public void setPricePerDay(double pricePerDay) { this.pricePerDay.set(pricePerDay); }
    public void setAvailable(boolean available) { this.available.set(available); }
    public void setYear(int year) { this.year.set(year); }
    public void setFuelType(String fuelType) { this.fuelType.set(fuelType); }
    public void setNumberOfSeats(int numberOfSeats) { this.numberOfSeats.set(numberOfSeats); }
    public void setTransmission(String transmission) { this.transmission.set(transmission); }

    @Override
    public String toString() {
        return String.format("%s %s (%d) - %s - %.2f€/jour", 
            brand.get(), model.get(), year.get(), type.get(), pricePerDay.get());
    }
}





