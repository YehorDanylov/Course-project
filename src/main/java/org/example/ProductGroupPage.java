package org.example;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.TextInputDialog;

import java.sql.SQLException;
import java.util.Optional;

public class ProductGroupPage extends VBox {

    private ProductGroupDAO groupDAO;
    private ProductDAO productDAO; // Assuming you have a ProductDAO class

    private ListView<String> groupList;

    public ProductGroupPage() {
        try {
            groupDAO = new ProductGroupDAO();
            productDAO = new ProductDAO(); // Initialize your ProductDAO if it exists
            initialize();
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Connection Error", "Unable to connect to the database:\n" + e.getMessage());
        }
    }

    private void initialize() {
        setPadding(new Insets(10));
        groupList = new ListView<>();
        refreshGroupList();

        Button addButton = new Button("Add Group");
        addButton.setOnAction(event -> addNewGroup());

        Button editButton = new Button("Edit Group");
        editButton.setOnAction(event -> editSelectedGroup());

        Button deleteButton = new Button("Delete Group");
        deleteButton.setOnAction(event -> deleteSelectedGroup());

        HBox buttonBox = new HBox(10, addButton, editButton, deleteButton);
        getChildren().addAll(groupList, buttonBox);
    }

    private void refreshGroupList() {
        groupList.getItems().clear();
        try {
            for (ProductGroup group : groupDAO.getAllGroups()) { // Corrected method name to getAllGroups()
                groupList.getItems().add(group.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Error refreshing group list", e.getMessage());
        }
    }

    private void addNewGroup() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Group");
        dialog.setHeaderText("Enter the details for the new group");
        dialog.setContentText("Group Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            ProductGroup newGroup = new ProductGroup(name, "");
            try {
                if (!groupDAO.isProductGroupNameUnique(name)) {
                    showErrorAlert("Error adding group", "Product group with the same name already exists.");
                    return;
                }
                groupDAO.addProductGroup(newGroup);
                refreshGroupList();
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorAlert("Error adding group", e.getMessage());
            }
        });
    }

    private void editSelectedGroup() {
        String selectedGroupName = groupList.getSelectionModel().getSelectedItem();
        if (selectedGroupName != null) {
            TextInputDialog dialog = new TextInputDialog(selectedGroupName);
            dialog.setTitle("Edit Group Name");
            dialog.setHeaderText("Enter the new name for the group");
            dialog.setContentText("Group Name:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(newName -> {
                try {
                    ProductGroup selectedGroup = groupDAO.getProductGroupByName(selectedGroupName);
                    if (selectedGroup != null) {
                        selectedGroup.setName(newName);
                        groupDAO.updateProductGroup(selectedGroup);
                        refreshGroupList();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showErrorAlert("Error editing group", e.getMessage());
                }
            });
        } else {
            showErrorAlert("No Group Selected", "Please select a group to edit.");
        }
    }

    private void deleteSelectedGroup() {
        String selectedGroupName = groupList.getSelectionModel().getSelectedItem();
        if (selectedGroupName != null) {
            try {
                ProductGroup group = groupDAO.getProductGroupByName(selectedGroupName);
                if (group != null) {
                    productDAO.deleteProductsByGroupId(group.getId()); // Assuming this method exists in ProductDAO
                    groupDAO.deleteProductGroup(group.getId());
                    refreshGroupList();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorAlert("Error deleting group", e.getMessage());
            }
        }
    }


    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
