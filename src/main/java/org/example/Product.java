package org.example;

public class Product {
    private int id;
    private String name;
    private String description;
    private String manufacturer;
    private int quantity;
    private double pricePerUnit;
    private int groupId;

    // Constructors
    public Product() {
    }

    public Product(String name, String description, String manufacturer, int quantity, double pricePerUnit) {
        this.name = name;
        this.description = description;
        this.manufacturer = manufacturer;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
    }

    public Product(String name, String description, String manufacturer, int quantity, double pricePerUnit, int groupId) {
        this.name = name;
        this.description = description;
        this.manufacturer = manufacturer;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.groupId = groupId;
    }

    // Getters and setters

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }


    public String getManufacturer() {
        return manufacturer;
    }


    public int getQuantity() {
        return quantity;
    }


    public double getPricePerUnit() {
        return pricePerUnit;
    }


    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
