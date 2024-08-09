package com.warehouse.warehouse.controller;

import com.warehouse.warehouse.database.DatabaseConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DepartamentoPesquisarController {

    @FXML private Label titleLabel;
    @FXML private TextField searchNomeField, searchIdField;
    @FXML private ListView<String> departamentoList;

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        titleLabel.setText("Pesquisar Departamento");
        departamentoList.setItems(FXCollections.observableArrayList());
    }

    @FXML
    private void handleSearch() {
        ObservableList<String> departamentos = FXCollections.observableArrayList();
        StringBuilder sql = new StringBuilder("SELECT id, nome, descricao FROM departamento WHERE 1=1");

        if (!searchNomeField.getText().isEmpty()) {
            sql.append(" AND LOWER(nome) LIKE ?");
        }
        if (!searchIdField.getText().isEmpty()) {
            sql.append(" AND CAST(id AS CHAR) LIKE ?");
        }
        sql.append(" ORDER BY departamento.id");

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int index = 1;
            if (!searchNomeField.getText().isEmpty()) {
                String searchText = "%" + searchNomeField.getText().toLowerCase() + "%";
                stmt.setString(index++, searchText);
            }
            if (!searchIdField.getText().isEmpty()) {
                stmt.setString(index, "%" + searchIdField.getText() + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String displayText = String.format("ID: %d - Nome: %s - Descrição: %s", rs.getInt("id"), rs.getString("nome"), rs.getString("descricao"));
                departamentos.add(displayText);
            }

            departamentoList.setItems(departamentos);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleEditSelected() {
        String selectedDepartamentoInfo = departamentoList.getSelectionModel().getSelectedItem();

        if (selectedDepartamentoInfo != null && mainController != null) {
            long departamentoId = Long.parseLong(selectedDepartamentoInfo.split(" - ")[0].split(": ")[1]);
            mainController.loadViewDepartamento("DepartamentoEditarView", departamentoId);
        }
    }

    @FXML
    private void handleDeleteSelected() {
        String selectedDepartamentoInfo = departamentoList.getSelectionModel().getSelectedItem();

        if (selectedDepartamentoInfo != null) {
            long departamentoId = Long.parseLong(selectedDepartamentoInfo.split(" - ")[0].split(": ")[1]);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Confirmação");
            dialog.setContentText("Tem certeza de que deseja excluir o departamento selecionado?");

            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getButtonTypes().addAll(new ButtonType("Sim", ButtonBar.ButtonData.YES), new ButtonType("Não", ButtonBar.ButtonData.NO));

            dialog.showAndWait().ifPresent(response -> {
                if (response.getButtonData() == ButtonBar.ButtonData.YES) {
                    String sql = "DELETE FROM departamento WHERE id = ?";
                    try (Connection conn = DatabaseConnector.getConnection();
                         PreparedStatement stmt = conn.prepareStatement(sql)) {

                        stmt.setLong(1, departamentoId);
                        stmt.executeUpdate();
                        handleSearch();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Erro ao excluir o departamento.");
                        errorAlert.show();
                    }
                }
            });
        }
    }
}
