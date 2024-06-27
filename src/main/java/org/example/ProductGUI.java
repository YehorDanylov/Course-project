package org.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class ProductGUI extends Application {

    private ProductDAO productDAO;
    private ProductGroupDAO productGroupDAO;
    private ComboBox<ProductGroup> groupComboBox;

    @Override
    public void init() throws Exception {
        super.init();
        try {
            productDAO = new ProductDAO();
            productGroupDAO = new ProductGroupDAO();
        } catch (SQLException e) {
            System.err.println("Unable to connect to the database: " + e.getMessage());
            // Optionally: handle the exception or notify the user
            throw e; // re-throw the exception to prevent the application from starting
        }
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10));

        // Product List View
        ListView<String> productList = new ListView<>();
        refreshProductList(productList);

        // Product Details Pane
        GridPane productDetailsPane = new GridPane();
        productDetailsPane.setPadding(new Insets(10));
        productDetailsPane.setHgap(10);
        productDetailsPane.setVgap(5);

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        Label descriptionLabel = new Label("Description:");
        TextField descriptionField = new TextField();
        Label manufacturerLabel = new Label("Manufacturer:");
        TextField manufacturerField = new TextField();
        Label quantityLabel = new Label("Quantity:");
        Spinner<Integer> quantitySpinner = new Spinner<>(0, Integer.MAX_VALUE, 0);
        Label priceLabel = new Label("Price per Unit:");
        Spinner<Double> priceSpinner = new Spinner<>(0.0, Double.MAX_VALUE, 0.0, 0.1);

        Label groupLabel = new Label("Group:");
        groupComboBox = new ComboBox<>();
        refreshGroupComboBox();

        Button addButton = new Button("Add Product");
        addButton.setOnAction(event -> {
            String name = nameField.getText();
            String description = descriptionField.getText();
            String manufacturer = manufacturerField.getText();
            int quantity = quantitySpinner.getValue();
            double price = priceSpinner.getValue();
            ProductGroup selectedGroup = groupComboBox.getSelectionModel().getSelectedItem();

            if (selectedGroup != null) {
                Product newProduct = new Product(name, description, manufacturer, quantity, price);
                newProduct.setGroupId(selectedGroup.getId()); // Set group ID here
                try {
                    productDAO.addProduct(newProduct); // Pass only the Product object
                    refreshProductList(productList);
                    clearFields(nameField, descriptionField, manufacturerField, quantitySpinner, priceSpinner, groupComboBox);
                } catch (SQLException e) {
                    e.printStackTrace();
                    // Optionally: handle the exception or notify the user
                }
            } else {
                // Optionally: notify the user to select a group
                System.err.println("Please select a group for the product.");
            }
        });


        productDetailsPane.addRow(0, nameLabel, nameField);
        productDetailsPane.addRow(1, descriptionLabel, descriptionField);
        productDetailsPane.addRow(2, manufacturerLabel, manufacturerField);
        productDetailsPane.addRow(3, quantityLabel, quantitySpinner);
        productDetailsPane.addRow(4, priceLabel, priceSpinner);
        productDetailsPane.addRow(5, groupLabel, groupComboBox);
        productDetailsPane.add(addButton, 1, 6);

        borderPane.setLeft(productList);
        borderPane.setCenter(productDetailsPane);

        Scene scene = new Scene(borderPane, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Product Management");
        primaryStage.show();
    }

    private void refreshProductList(ListView<String> productList) {
        productList.getItems().clear();
        try {
            List<Product> products = productDAO.getAllProducts();
            for (Product product : products) {
                productList.getItems().add(product.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Optionally: handle the exception or notify the user
        }
    }

    private void refreshGroupComboBox() {
        groupComboBox.getItems().clear();
        try {
            List<ProductGroup> groups = productGroupDAO.getAllGroups();
            ObservableList<ProductGroup> observableGroups = FXCollections.observableArrayList(groups);
            groupComboBox.setItems(observableGroups);
        } catch (SQLException e) {
            e.printStackTrace();
            // Optionally: handle the exception or notify the user
        }
    }

    private void clearFields(TextField nameField, TextField descriptionField, TextField manufacturerField,
                             Spinner<Integer> quantitySpinner, Spinner<Double> priceSpinner, ComboBox<ProductGroup> groupComboBox) {
        nameField.clear();
        descriptionField.clear();
        manufacturerField.clear();
        quantitySpinner.getValueFactory().setValue(0);
        priceSpinner.getValueFactory().setValue(0.0);
        groupComboBox.getSelectionModel().clearSelection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
