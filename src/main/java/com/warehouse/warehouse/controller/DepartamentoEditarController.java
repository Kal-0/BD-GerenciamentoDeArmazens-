package com.warehouse.warehouse.controller;

import com.warehouse.warehouse.database.DatabaseConnector;
import com.warehouse.warehouse.util.FieldValidation;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DepartamentoEditarController {

    @FXML
    private TextField nomeField;

    @FXML
    private TextArea descricaoField;

    @FXML
    private Label statusLabel;

    private long selectedDepartamentoId;

    @FXML
    private void initialize() {
        FieldValidation.setTextFieldLimit(nomeField, 100);
        FieldValidation.setTextAreaLimit(descricaoField, 500);
    }

    public void setSelectedDepartamentoId(long departamentoId) {
        this.selectedDepartamentoId = departamentoId;
        loadDepartamentoData();
    }

    private void loadDepartamentoData() {
        String sql = "SELECT * FROM departamento WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, selectedDepartamentoId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nomeField.setText(rs.getString("nome"));
                descricaoField.setText(rs.getString("descricao"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            statusLabel.setText("Erro ao carregar os dados do departamento.");
        }
    }

    @FXML
    private void updateDepartamento() {
        String nome = nomeField.getText().trim();
        String descricao = descricaoField.getText() != null ? descricaoField.getText().trim() : "";

        if (nome.isEmpty()) {
            statusLabel.setText("Por favor, preencha o campo nome.");
            return;
        }

        String sqlUpdate = "UPDATE departamento SET nome=?, descricao=? WHERE id=?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {

            stmt.setString(1, nome);
            stmt.setString(2, descricao.isEmpty() ? null : descricao);
            stmt.setLong(3, selectedDepartamentoId);
            stmt.executeUpdate();

            statusLabel.setText("Departamento atualizado com sucesso.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            statusLabel.setText("Erro ao atualizar os dados do departamento.");
        }
    }
}
