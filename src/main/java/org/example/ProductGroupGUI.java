package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class ProductGroupGUI extends Application {

    private ProductGroupDAO groupDAO;

    @Override
    public void init() throws Exception {
        try {
            groupDAO = new ProductGroupDAO();
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
            showErrorAlert("Database Connection Error", "Unable to connect to the database:\n" + e.getMessage());
            throw e; // Rethrow the exception to terminate initialization
        }
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10));

        // Product Group List View
        ListView<String> groupList = new ListView<>();
        refreshGroupList(groupList);

        // Product Group Details Pane
        GridPane groupDetailsPane = new GridPane();
        groupDetailsPane.setPadding(new Insets(10));
        groupDetailsPane.setHgap(10);
        groupDetailsPane.setVgap(5);

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        Label descriptionLabel = new Label("Description:");
        TextField descriptionField = new TextField();

        Button addButton = new Button("Add Group");
        addButton.setOnAction(event -> {
            String name = nameField.getText();
            String description = descriptionField.getText();

            ProductGroup newGroup = new ProductGroup(name, description);
            try {
                groupDAO.addProductGroup(newGroup);
                refreshGroupList(groupList);
                clearFields(nameField, descriptionField);
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorAlert("Error adding group", e.getMessage());
            }
        });

        groupDetailsPane.addRow(0, nameLabel, nameField);
        groupDetailsPane.addRow(1, descriptionLabel, descriptionField);
        groupDetailsPane.add(addButton, 1, 2);

        borderPane.setLeft(groupList);
        borderPane.setCenter(groupDetailsPane);

        Scene scene = new Scene(borderPane, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Product Group Management");
        primaryStage.show();
    }

    private void refreshGroupList(ListView<String> groupList) {
        groupList.getItems().clear();
        try {
            List<ProductGroup> groups = groupDAO.getAllGroups(); // Corrected method name to getAllGroups()
            for (ProductGroup group : groups) {
                groupList.getItems().add(group.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Error refreshing group list", e.getMessage());
        }
    }

    private void clearFields(TextField nameField, TextField descriptionField) {
        nameField.clear();
        descriptionField.clear();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
