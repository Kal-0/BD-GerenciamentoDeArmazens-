package com.warehouse.warehouse;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;
import com.warehouse.warehouse.controller.MainController;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Load your icon image
        Image icon = new Image(getClass().getResourceAsStream("/com/warehouse/warehouse/images/icon.png"));
        // Set the icon for the primary stage
        primaryStage.getIcons().add(icon);

        // Load the main view FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/warehouse/warehouse/MainView.fxml"));
        Parent root = loader.load();

        // Get the controller and load the initial dashboard view
        MainController mainController = loader.getController();
        mainController.loadInitialDashboard();

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setHeight(screenBounds.getHeight() * 0.8); // Set height to 80% of screen height

        Scene scene = new Scene(root);
        primaryStage.setTitle("Warehouse");

        primaryStage.setMinWidth(800);  // Set minimum width
        primaryStage.setMinHeight(600); // Set minimum height

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}

