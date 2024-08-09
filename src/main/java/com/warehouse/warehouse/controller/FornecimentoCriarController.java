package com.warehouse.warehouse.controller;

import com.warehouse.warehouse.database.DatabaseConnector;
import com.warehouse.warehouse.util.FieldValidation;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.sql.*;

public class FornecimentoCriarController {

    @FXML
    private TextField precoVendaField;
    @FXML
    private TextField quantidadeEstoqueField;
    @FXML
    private ComboBox<String> nomeProdutoComboBox;
    @FXML
    private ComboBox<String> nomeFornecedorComboBox;
    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {

        populateNomeProdutoComboBox();

        populateNomeFornecedorComboBox();

        addFieldValidators();

    }

    private void populateNomeProdutoComboBox() {
        nomeProdutoComboBox.getItems().clear();
        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT p.nome FROM produto p");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String nome = rs.getString("nome");
                if (nome != null && !nome.isEmpty()) {
                    nomeProdutoComboBox.getItems().add(nome);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateNomeFornecedorComboBox() {
        nomeFornecedorComboBox.getItems().clear();
        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT p.nome, p.razao_social FROM pessoa p " +
                            "JOIN fornecedor f ON p.id = f.fk_pessoa_id");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String nome = rs.getString("nome");
                String razao_social = rs.getString("razao_social");
                if (nome != null && !nome.isEmpty()) {
                    nomeFornecedorComboBox.getItems().add(nome);
                } else if (razao_social != null && !razao_social.isEmpty()) {
                    nomeFornecedorComboBox.getItems().add(razao_social);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void saveFornecimento() {

        if(!validateFields()){
            return;
        }
        String fornecedor = nomeFornecedorComboBox.getValue();
        String produto = nomeProdutoComboBox.getValue();
        String precoCompra = precoVendaField.getText();
        String quantidadeFornecida = quantidadeEstoqueField.getText();

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false);
            int fornecedorId = getFornecedorId(fornecedor);
            int produtoId = getProdutoId(produto);

            stmt = conn.prepareStatement(
                    "INSERT INTO fornece (preco_compra, quantidade, fk_fornecedor_id, fk_produto_id) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            stmt.setBigDecimal(1, new BigDecimal(precoCompra));
            stmt.setInt(2, Integer.parseInt(quantidadeFornecida));
            stmt.setInt(3, fornecedorId);
            stmt.setInt(4, produtoId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                conn.commit();
                statusLabel.setText("Fornecimento salvo com sucesso!");
                limparCampos();
            } else {
                conn.rollback();
                statusLabel.setText("Erro ao salvar o fornecimento.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            statusLabel.setText("Erro ao conectar com o banco de dados.");
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Retorna ao modo de commit automático
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int getProdutoId(String identifier) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT p.id FROM produto p WHERE p.nome = ?")) {
            stmt.setString(1, identifier);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new SQLException("Produto não encontrado: " + identifier);
            }
        }
    }

    private int getFornecedorId(String identifier) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT f.fk_pessoa_id FROM fornecedor f" +
                             " JOIN pessoa p on p.id = f.fk_pessoa_id" +
                             " WHERE p.nome = ? OR p.razao_social = ?")) {
            stmt.setString(1, identifier);
            stmt.setString(2, identifier);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("fk_pessoa_id");
            } else {
                throw new SQLException("Fornecedor não encontrado: " + identifier);
            }
        }
    }


    private void addFieldValidators(){

        //Max length
        FieldValidation.setTextFieldLimit(quantidadeEstoqueField, 5);

        FieldValidation.setNumericField(quantidadeEstoqueField);

        FieldValidation.setDecimalField(precoVendaField, 10, 2);


    }

    private boolean validateFields() {
        String precoVenda = precoVendaField.getText() != null ? precoVendaField.getText().trim() : "";
        String quantidadeEstoque = quantidadeEstoqueField.getText() != null ? quantidadeEstoqueField.getText().trim() : "";
        String nomeProduto = nomeProdutoComboBox.getValue() != null ? nomeProdutoComboBox.getValue().trim() : "";
        String nomeFornecedor = nomeFornecedorComboBox.getValue() != null ? nomeFornecedorComboBox.getValue().trim() : "";

        // Verificação de campos obrigatórios
        if (nomeProduto.isEmpty() || nomeFornecedor.isEmpty() || precoVenda.isEmpty() || quantidadeEstoque.isEmpty()) {
            statusLabel.setText("Por favor, preencha todos os campos obrigatórios.");
            return false;
        }

        // Validação de formato do preço de venda
        if (!precoVenda.matches("\\d+(\\.\\d{1,2})?")) {
            statusLabel.setText("Por favor, insira um valor de preço de venda válido.");
            return false;
        }

        // Validação de formato da quantidade em estoque
        if (!quantidadeEstoque.matches("\\d+")) {
            statusLabel.setText("Por favor, insira um valor de quantidade em estoque válido.");
            return false;
        }

        // Se todas as validações passarem, retorna true
        return true;
    }



    private void limparCampos() {
        nomeProdutoComboBox.getSelectionModel().clearSelection();
        nomeFornecedorComboBox.getSelectionModel().clearSelection();
        precoVendaField.clear();
        quantidadeEstoqueField.clear();
    }
}
