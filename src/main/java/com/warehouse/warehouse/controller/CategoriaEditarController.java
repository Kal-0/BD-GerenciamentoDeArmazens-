package com.warehouse.warehouse.controller;

import com.warehouse.warehouse.database.DatabaseConnector;
import com.warehouse.warehouse.util.FieldValidation;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoriaEditarController {

    @FXML
    private TextField nomeField;
    @FXML
    private TextArea descricaoField;
    @FXML
    private Label statusLabel;

    private long selectedCategoryId;

    public void setSelectedCategoryId(long categoryId) {
        this.selectedCategoryId = categoryId;
        loadCategoryData();
    }

    @FXML
    private void initialize() {
        addFieldValidators();
    }

    private void addFieldValidators() {
        // Max length
        FieldValidation.setTextFieldLimit(nomeField, 100);
        FieldValidation.setTextAreaLimit(descricaoField, 500);
    }

    private void loadCategoryData() {
        String sql = "SELECT * FROM categoria WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, selectedCategoryId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nomeField.setText(rs.getString("nome") != null ? rs.getString("nome") : "");
                descricaoField.setText(rs.getString("descricao") != null ? rs.getString("descricao") : "");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Erro ao carregar dados da categoria.");
        }
    }

    @FXML
    private void updateCategory() {
        String nome = nomeField.getText().trim();
        String descricao = descricaoField.getText().trim();

        if (nome.isEmpty() || descricao.isEmpty()) {
            statusLabel.setText("Por favor, preencha todos os campos.");
            return;
        }

        String sql = "UPDATE categoria SET nome=?, descricao=? WHERE id=?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setString(2, descricao);
            stmt.setLong(3, selectedCategoryId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                statusLabel.setText("Categoria atualizada com sucesso!");
            } else {
                statusLabel.setText("Erro ao atualizar a categoria.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Erro ao conectar com o banco de dados.");
        }
    }
}
