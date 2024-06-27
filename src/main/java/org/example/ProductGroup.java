package org.example;

public class ProductGroup {
    private int id;
    private String name;
    private String description;

    // Конструктор за замовчуванням
    public ProductGroup() {
    }

    // Конструктор з трьома параметрами
    public ProductGroup(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // Новий конструктор з двома параметрами
    public ProductGroup(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Геттери та сеттери
    public int getId() {
        return id;
    }

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

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name;
    }
}
