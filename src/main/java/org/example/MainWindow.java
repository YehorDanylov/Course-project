package org.example;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

public class MainWindow extends BorderPane {

    public MainWindow() {
        initialize();
    }

    private void initialize() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Products Tab
        Tab productsTab = new Tab("Products");
        Node productPage = new ProductPage(); // Assuming ProductPage extends Node or a layout class like GridPane
        productsTab.setContent(productPage);

        // Product Groups Tab
        Tab groupsTab = new Tab("Product Groups");
        Node productGroupPage = new ProductGroupPage(); // Assuming ProductGroupPage extends Node or a layout class like VBox
        groupsTab.setContent(productGroupPage);

        tabPane.getTabs().addAll(productsTab, groupsTab);

        setCenter(tabPane);
        setPadding(new Insets(10));
    }
}
