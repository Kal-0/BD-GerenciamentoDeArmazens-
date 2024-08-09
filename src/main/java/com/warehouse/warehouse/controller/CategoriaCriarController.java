package com.warehouse.warehouse.controller;

import com.warehouse.warehouse.database.DatabaseConnector;
import com.warehouse.warehouse.util.FieldValidation;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CategoriaCriarController {

    @FXML
    private TextField nomeField;
    @FXML
    private TextArea descricaoField;
    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {
        addFieldValidators();
    }

    private void addFieldValidators() {
        // Max length
        FieldValidation.setTextFieldLimit(nomeField, 100);
        FieldValidation.setTextAreaLimit(descricaoField, 500);
    }

    @FXML
    private void saveCategory() {
        String nome = nomeField.getText().trim();
        String descricao = descricaoField.getText().trim();

        if (nome.isEmpty() || descricao.isEmpty()) {
            statusLabel.setText("Por favor, preencha todos os campos.");
            return;
        }

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO categoria (nome, descricao) VALUES (?, ?)")) {

            stmt.setString(1, nome);
            stmt.setString(2, descricao);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                statusLabel.setText("Categoria salva com sucesso!");
                limparCampos();
            } else {
                statusLabel.setText("Erro ao salvar a categoria.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Erro ao conectar com o banco de dados.");
        }
    }

    private void limparCampos() {
        nomeField.clear();
        descricaoField.clear();
    }
}
