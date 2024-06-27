package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class ProductPage extends GridPane {

    private ProductDAO productDAO;
    private ProductGroupDAO groupDAO;
    private ListView<String> productList;
    private TextField nameField, descriptionField, manufacturerField, searchField;
    private Spinner<Integer> quantitySpinner;
    private Spinner<Double> priceSpinner;
    private ComboBox<ProductGroup> groupComboBox;
    private TextArea statisticsArea;

    public ProductPage() {
        try {
            productDAO = new ProductDAO();
            groupDAO = new ProductGroupDAO();
            initialize();
        } catch (SQLException e) {
            e.printStackTrace();
            // Optionally: handle the exception or notify the user
        }
    }

    private void initialize() {
        setPadding(new Insets(10));

        // Product List View
        productList = new ListView<>();
        refreshProductList();
        productList.setPrefWidth(200); // Set preferred width for the product list
        productList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    Product product = productDAO.getProductByName(newValue);
                    if (product != null) {
                        displayProductDetails(product);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    // Optionally: handle the exception or notify the user
                }
            } else {
                clearFields();
            }
        });

        // Product Details Pane
        GridPane productDetailsPane = new GridPane();
        productDetailsPane.setPadding(new Insets(10));
        productDetailsPane.setHgap(10);
        productDetailsPane.setVgap(5);

        Label nameLabel = new Label("Name:");
        nameField = new TextField();
        Label descriptionLabel = new Label("Description:");
        descriptionField = new TextField();
        Label manufacturerLabel = new Label("Manufacturer:");
        manufacturerField = new TextField();
        Label quantityLabel = new Label("Quantity:");
        quantitySpinner = new Spinner<>(0, Integer.MAX_VALUE, 0);
        quantitySpinner.setEditable(true); // Allow text input
        Label priceLabel = new Label("Price per Unit:");
        priceSpinner = new Spinner<>(0.0, Double.MAX_VALUE, 0.0, 0.1);
        priceSpinner.setEditable(true); // Allow text input

        Label groupLabel = new Label("Group:");
        groupComboBox = new ComboBox<>();
        refreshGroupComboBox(); // Refresh groups from database
        groupComboBox.setPromptText("Select Group");

        Button addButton = new Button("Add Product");
        addButton.setOnAction(event -> showAddProductDialog());

        Button updateButton = new Button("Update Product");
        updateButton.setOnAction(event -> updateProduct());

        Button deleteButton = new Button("Delete Product");
        deleteButton.setOnAction(event -> deleteProduct());

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> {
            refreshProductList();
            refreshGroupComboBox();
            clearFields();
        });

        Button statsButton = new Button("Show Statistics");
        statsButton.setOnAction(event -> showStatisticsWindow());

        // Search Field
        searchField = new TextField();
        searchField.setPromptText("Search by Name");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    searchProductByName(newValue);
                } catch (SQLException e) {
                    e.printStackTrace();
                    // Optionally: handle the exception or notify the user
                }
            } else {
                refreshProductList();
            }
        });

        productDetailsPane.addRow(0, nameLabel, nameField);
        productDetailsPane.addRow(1, descriptionLabel, descriptionField);
        productDetailsPane.addRow(2, manufacturerLabel, manufacturerField);
        productDetailsPane.addRow(3, quantityLabel, quantitySpinner);
        productDetailsPane.addRow(4, priceLabel, priceSpinner);
        productDetailsPane.addRow(5, groupLabel, groupComboBox);
        productDetailsPane.add(addButton, 1, 6);
        productDetailsPane.add(updateButton, 2, 6);
        productDetailsPane.add(deleteButton, 3, 6);
        productDetailsPane.add(refreshButton, 4, 6);
        productDetailsPane.add(statsButton, 5, 6);
        productDetailsPane.addRow(7, new Label("Search by Name:"), searchField);

        VBox rootPane = new VBox();
        rootPane.setSpacing(10);
        rootPane.getChildren().addAll(productList, productDetailsPane);

        // Statistics Pane
        statisticsArea = new TextArea();
        statisticsArea.setEditable(false);
        VBox.setMargin(statisticsArea, new Insets(10));
        rootPane.getChildren().add(statisticsArea);

        // Set root pane as the content of the ProductPage
        this.getChildren().addAll(rootPane);
    }

    private void refreshProductList() {
        productList.getItems().clear();
        try {
            List<Product> products = productDAO.getAllProducts();
            ObservableList<String> items = FXCollections.observableArrayList();
            for (Product product : products) {
                items.add(product.getName());
            }
            productList.setItems(items);
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Database Error", "Failed to fetch products: " + e.getMessage());
        }
    }

    private void refreshGroupComboBox() {
        try {
            List<ProductGroup> groups = groupDAO.getAllGroups();
            groupComboBox.setItems(FXCollections.observableArrayList(groups));
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Database Error", "Failed to fetch product groups: " + e.getMessage());
        }
    }

    private void clearFields() {
        nameField.clear();
        descriptionField.clear();
        manufacturerField.clear();
        quantitySpinner.getValueFactory().setValue(0);
        priceSpinner.getValueFactory().setValue(0.0);
        groupComboBox.getSelectionModel().clearSelection();
    }

    private void displayProductDetails(Product product) {
        nameField.setText(product.getName());
        descriptionField.setText(product.getDescription());
        manufacturerField.setText(product.getManufacturer());
        quantitySpinner.getValueFactory().setValue(product.getQuantity());
        priceSpinner.getValueFactory().setValue(product.getPricePerUnit());

        // Select the corresponding group in ComboBox
        int groupId = product.getGroupId();
        for (ProductGroup group : groupComboBox.getItems()) {
            if (group.getId() == groupId) {
                groupComboBox.getSelectionModel().select(group);
                break;
            }
        }
    }

    private void updateProduct() {
        String name = nameField.getText();
        String description = descriptionField.getText();
        String manufacturer = manufacturerField.getText();
        int quantity = quantitySpinner.getValue();
        double price = priceSpinner.getValue();
        ProductGroup selectedGroup = groupComboBox.getValue();

        if (selectedGroup != null) {
            Product updatedProduct = new Product(name, description, manufacturer, quantity, price);
            updatedProduct.setGroupId(selectedGroup.getId());
            try {
                productDAO.updateProduct(updatedProduct);
                refreshProductList();
                clearFields();
                showInformationDialog("Product Updated", "Product updated successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorDialog("Update Error", "Failed to update product: " + e.getMessage());
            }
        } else {
            showErrorDialog("Update Error", "Please select a group for the product.");
        }
    }

    private void deleteProduct() {
        String selectedProductName = productList.getSelectionModel().getSelectedItem();
        if (selectedProductName != null) {
            try {
                productDAO.deleteProduct(selectedProductName);
                refreshProductList();
                clearFields();
                showInformationDialog("Product Deleted", "Product deleted successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorDialog("Delete Error", "Failed to delete product: " + e.getMessage());
            }
        } else {
            showErrorDialog("Delete Error", "Please select a product to delete.");
        }
    }

    private void showAddProductDialog() {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Add Product");
        dialog.setHeaderText("Enter Product Details");

        // Set the button types
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        // Create and configure the grid for input fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Product Name");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        TextField manufacturerField = new TextField();
        manufacturerField.setPromptText("Manufacturer");

        Spinner<Integer> quantitySpinner = new Spinner<>(0, Integer.MAX_VALUE, 0);
        quantitySpinner.setEditable(true); // Allow text input

        Spinner<Double> priceSpinner = new Spinner<>(0.0, Double.MAX_VALUE, 0.0, 0.1);
        priceSpinner.setEditable(true); // Allow text input

        Label groupLabel = new Label("Group:");
        groupComboBox = new ComboBox<>();
        refreshGroupComboBox(); // Refresh groups from database

        grid.addRow(0, new Label("Name:"), nameField);
        grid.addRow(1, new Label("Description:"), descriptionField);
        grid.addRow(2, new Label("Manufacturer:"), manufacturerField);
        grid.addRow(3, new Label("Quantity:"), quantitySpinner);
        grid.addRow(4, new Label("Price per Unit:"), priceSpinner);
        grid.addRow(5, groupLabel, groupComboBox);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a product when the add button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                String name = nameField.getText();
                String description = descriptionField.getText();
                String manufacturer = manufacturerField.getText();
                int quantity = quantitySpinner.getValue();
                double price = priceSpinner.getValue();
                ProductGroup selectedGroup = groupComboBox.getValue();

                if (selectedGroup != null) {
                    return new Product(name, description, manufacturer, quantity, price, selectedGroup.getId());
                } else {
                    showErrorDialog("Validation Error", "Please select a group for the product.");
                    return null; // null indicates invalid input
                }
            }
            return null; // null indicates cancel or close
        });

        dialog.showAndWait().ifPresent(newProduct -> {
            try {
                productDAO.addProduct(newProduct);
                refreshProductList();
                showInformationDialog("Product Added", "Product added successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorDialog("Add Error", "Failed to add product: " + e.getMessage());
            }
        });
}

    private void searchProductByName(String name) throws SQLException {
        List<Product> products = productDAO.searchProductsByName(name);
        ObservableList<String> items = FXCollections.observableArrayList();
        for (Product product : products) {
            items.add(product.getName());
        }
        productList.setItems(items);
    }

    private void showStatisticsWindow() {
        try {
            StringBuilder stats = new StringBuilder();
            List<Product> products = productDAO.getAllProducts();
            List<ProductGroup> groups = groupDAO.getAllGroups();

            // List all products sorted by quantity
            stats.append("Products sorted by quantity:\n");
            products.sort((p1, p2) -> Integer.compare(p2.getQuantity(), p1.getQuantity())); // Descending order
            for (Product product : products) {
                stats.append(product.getName()).append(": ").append(product.getQuantity()).append("\n");
            }
            stats.append("\n");

            // List all products by group with detailed information
            stats.append("Products by group:\n");
            for (ProductGroup group : groups) {
                stats.append("Group: ").append(group.getName()).append("\n");
                List<Product> groupProducts = productDAO.getProductsByGroupId(group.getId());
                for (Product product : groupProducts) {
                    stats.append("   ").append(product.getName()).append(": ")
                            .append(product.getDescription()).append(", ")
                            .append(product.getManufacturer()).append(", ")
                            .append(product.getQuantity()).append(", ")
                            .append(product.getPricePerUnit()).append("\n");
                }
                stats.append("\n");
            }

            // Total value of product in stock (quantity * price)
            double totalValue = products.stream()
                    .mapToDouble(product -> product.getQuantity() * product.getPricePerUnit())
                    .sum();
            stats.append("Total value of all products in stock: $").append(String.format("%.2f", totalValue)).append("\n");

            // Total value of products in each group
            stats.append("Total value of products in each group:\n");
            for (ProductGroup group : groups) {
                double groupTotalValue = productDAO.getProductsByGroupId(group.getId()).stream()
                        .mapToDouble(product -> product.getQuantity() * product.getPricePerUnit())
                        .sum();
                stats.append("   ").append(group.getName()).append(": $").append(String.format("%.2f", groupTotalValue)).append("\n");
            }

            statisticsArea.setText(stats.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Statistics Error", "Failed to fetch statistics: " + e.getMessage());
        }
    }

    private void showInformationDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showErrorDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
