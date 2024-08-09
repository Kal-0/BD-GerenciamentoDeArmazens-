package com.warehouse.warehouse.controller;

import com.warehouse.warehouse.database.DatabaseConnector;
import com.warehouse.warehouse.util.FieldValidation;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DepartamentoCriarController {

    @FXML
    private TextField nomeField;

    @FXML
    private TextArea descricaoField;

    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {
        FieldValidation.setTextFieldLimit(nomeField, 100);
        FieldValidation.setTextAreaLimit(descricaoField, 500);
    }

    @FXML
    private void saveDepartamento(ActionEvent event) {
        String nome = nomeField.getText().trim();
        String descricao = descricaoField.getText() != null ? descricaoField.getText().trim() : "";

        if (nome.isEmpty()) {
            statusLabel.setText("Por favor, preencha o campo nome.");
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false); // transaction

            stmt = conn.prepareStatement("INSERT INTO departamento (nome, descricao) VALUES (?, ?)");
            stmt.setString(1, nome);
            stmt.setString(2, descricao.isEmpty() ? null : descricao);
            stmt.executeUpdate();

            conn.commit();
            statusLabel.setText("Departamento criado com sucesso.");
            limparCampos();

        } catch (SQLException ex) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException se2) {
                ex.printStackTrace();
            }
            ex.printStackTrace();
            statusLabel.setText("Erro ao conectar com o banco de dados.");
        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException ex) { /* Ignored */ }
            if (conn != null) try { conn.close(); } catch (SQLException ex) { /* Ignored */ }
        }
    }

    private void limparCampos() {
        nomeField.clear();
        descricaoField.clear();
    }
}