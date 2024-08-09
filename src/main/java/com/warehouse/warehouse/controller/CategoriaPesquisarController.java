package com.warehouse.warehouse.controller;

import com.warehouse.warehouse.database.DatabaseConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaPesquisarController {

    @FXML private Label titleLabel;
    @FXML private TextField searchNameField, searchIdField;
    @FXML private ListView<String> categoryList;
    @FXML private Label statusLabel;

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }



    @FXML
    private void initialize() {
        titleLabel.setText("Pesquisar Categoria");
        categoryList.setItems(FXCollections.observableArrayList());
    }

    @FXML
    private void handleSearch() {
        ObservableList<String> categories = FXCollections.observableArrayList();
        StringBuilder sql = new StringBuilder("SELECT id, nome, descricao FROM categoria WHERE 1=1");

        if (!searchNameField.getText().isEmpty()) {
            sql.append(" AND LOWER(nome) LIKE ?");
        }
        if (!searchIdField.getText().isEmpty()) {
            sql.append(" AND CAST(id AS CHAR) LIKE ?");
        }

        sql.append(" ORDER BY id");

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int index = 1;
            if (!searchNameField.getText().isEmpty()) {
                stmt.setString(index++, "%" + searchNameField.getText().toLowerCase() + "%");
            }
            if (!searchIdField.getText().isEmpty()) {
                stmt.setString(index++, "%" + searchIdField.getText() + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String displayText = String.format("ID: %d - Nome: %s - Descrição: %s", rs.getInt("id"), rs.getString("nome"), rs.getString("descricao"));
                categories.add(displayText);
            }

            categoryList.setItems(categories);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleEditSelected() {
        String selectedCategoryInfo = categoryList.getSelectionModel().getSelectedItem();

        if (selectedCategoryInfo != null && mainController != null) {
            long categoryId = Long.parseLong(selectedCategoryInfo.split(" - ")[0].split(": ")[1]);
            mainController.loadViewId("CategoriaEditarView", categoryId);
        }
    }
}
