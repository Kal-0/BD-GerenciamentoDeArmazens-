package com.warehouse.warehouse.controller;

import com.warehouse.warehouse.database.DatabaseConnector;
import com.warehouse.warehouse.util.FieldValidation;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoCriarController {

    @FXML
    private TextField nomeField;
    @FXML
    private TextArea descricaoField;
    @FXML
    private TextField precoVendaField;
    @FXML
    private TextField precoAluguelField;
    @FXML
    private TextField quantidadeEstoqueField;
    @FXML
    private ComboBox<String> categoriaComboBox;
    @FXML
    private Label statusLabel;

    private List<Integer> categoriaIds;

    @FXML
    private void initialize() {
        carregarCategorias();
        addFieldValidators();
    }

    private void carregarCategorias() {
        categoriaIds = new ArrayList<>();
        List<String> categorias = new ArrayList<>();

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nome FROM categoria")) {

            while (rs.next()) {
                categoriaIds.add(rs.getInt("id"));
                categorias.add(rs.getString("nome"));
            }

            categoriaComboBox.setItems(FXCollections.observableArrayList(categorias));

        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Erro ao carregar categorias.");
        }
    }

    private void addFieldValidators() {
        // Max length
        FieldValidation.setTextFieldLimit(nomeField, 100);
        FieldValidation.setTextAreaLimit(descricaoField, 500);
        FieldValidation.setTextFieldLimit(precoVendaField, 10);
        FieldValidation.setTextFieldLimit(precoAluguelField, 10);
        FieldValidation.setTextFieldLimit(quantidadeEstoqueField, 10);

        // Numeric and Decimal fields
        FieldValidation.setDecimalField(precoVendaField, 10, 2);
        FieldValidation.setDecimalField(precoAluguelField, 10, 2);
        FieldValidation.setNumericField(quantidadeEstoqueField);
    }

    @FXML
    private void saveProduct() {
        String nome = nomeField.getText().trim();
        String descricao = descricaoField.getText().trim();
        String precoVenda = precoVendaField.getText().trim();
        String precoAluguel = precoAluguelField.getText().trim();
        String quantidadeEstoque = quantidadeEstoqueField.getText().trim();
        String categoria = categoriaComboBox.getValue();

        if (nome.isEmpty() || descricao.isEmpty() || precoVenda.isEmpty() || precoAluguel.isEmpty() || quantidadeEstoque.isEmpty() || categoria == null) {
            statusLabel.setText("Por favor, preencha todos os campos.");
            return;
        }

        int categoriaId = categoriaIds.get(categoriaComboBox.getSelectionModel().getSelectedIndex());

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO produto (nome, descricao, preco_venda, preco_aluguel, quantidade_estoque, fk_categoria_id) VALUES (?, ?, ?, ?, ?, ?)")) {

            stmt.setString(1, nome);
            stmt.setString(2, descricao);
            stmt.setBigDecimal(3, new BigDecimal(precoVenda));
            stmt.setBigDecimal(4, new BigDecimal(precoAluguel));
            stmt.setInt(5, Integer.parseInt(quantidadeEstoque));
            stmt.setInt(6, categoriaId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                statusLabel.setText("Produto salvo com sucesso!");
                limparCampos();
            } else {
                statusLabel.setText("Erro ao salvar o produto.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Erro ao conectar com o banco de dados.");
        }
    }

    private void limparCampos() {
        nomeField.clear();
        descricaoField.clear();
        precoVendaField.clear();
        precoAluguelField.clear();
        quantidadeEstoqueField.clear();
        categoriaComboBox.getSelectionModel().clearSelection();
    }
}
