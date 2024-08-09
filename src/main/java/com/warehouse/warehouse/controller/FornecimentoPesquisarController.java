package com.warehouse.warehouse.controller;

import com.warehouse.warehouse.database.DatabaseConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FornecimentoPesquisarController {

    @FXML
    private Label titleLabel;
    @FXML
    private TextField nomeProdutoTextField;
    @FXML
    private TextField nomeFornecedorTextField;
    @FXML
    private TextField searchIdField;
    @FXML
    private ListView<String> fornecedorList;
    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {
        titleLabel.setText("Pesquisar Fornecimento");
        fornecedorList.setItems(FXCollections.observableArrayList());
    }

    @FXML
    private void handleSearch() {
        String searchId = searchIdField.getText().trim();
        String nomeProduto = nomeProdutoTextField.getText().trim();
        String nomeFornecedor = nomeFornecedorTextField.getText().trim();

        if (!searchId.isEmpty()) {
            try {
                int id = Integer.parseInt(searchId);
                if (id < 0) {
                    statusLabel.setText("ID inválido. Insira um ID positivo.");
                    fornecedorList.getItems().clear();
                    return;
                }

                if (!doesIdExist(id)) {
                    statusLabel.setText("ID não encontrado.");
                    fornecedorList.getItems().clear();
                    return;
                }

            } catch (NumberFormatException e) {
                statusLabel.setText("ID inválido. Insira um número válido.");
                fornecedorList.getItems().clear();
                return;
            }
        }

        ObservableList<String> fornecimentos = FXCollections.observableArrayList();
        StringBuilder sql = new StringBuilder("SELECT f.id, p.nome AS produto_nome, " +
                "COALESCE(ps.nome, ps.razao_social) AS fornecedor_nome, f.preco_compra, f.quantidade " +
                "FROM fornece f " +
                "JOIN produto p ON f.fk_produto_id = p.id " +
                "LEFT JOIN fornecedor fo ON f.fk_fornecedor_id = fo.fk_pessoa_id " +
                "LEFT JOIN pessoa ps ON fo.fk_pessoa_id = ps.id " +
                "WHERE 1=1");

        List<Object> parameters = new ArrayList<>();

        if (!nomeProduto.isEmpty()) {
            sql.append(" AND LOWER(p.nome) LIKE ?");
            parameters.add("%" + nomeProduto.toLowerCase() + "%");
        }
        if (!nomeFornecedor.isEmpty()) {
            sql.append(" AND (LOWER(ps.nome) LIKE ? OR LOWER(ps.razao_social) LIKE ?)");
            parameters.add("%" + nomeFornecedor.toLowerCase() + "%");
            parameters.add("%" + nomeFornecedor.toLowerCase() + "%");
        }
        if (!searchId.isEmpty()) {
            sql.append(" AND f.id = ?");
            parameters.add(Integer.parseInt(searchId));
        }
        sql.append(" ORDER BY f.id");

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int index = 1;
            for (Object param : parameters) {
                stmt.setObject(index++, param);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String displayText = String.format("ID: %d - Produto: %s - Fornecedor: %s - Preço: %.2f - Quantidade: %d",
                        rs.getInt("id"), rs.getString("produto_nome"), rs.getString("fornecedor_nome"), rs.getBigDecimal("preco_compra"), rs.getInt("quantidade"));
                fornecimentos.add(displayText);
            }

            if (fornecimentos.isEmpty()) {
                statusLabel.setText("Nenhum fornecimento registrado.");
                fornecedorList.getItems().clear();
            } else {
                fornecedorList.setItems(fornecimentos);
                statusLabel.setText("");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            statusLabel.setText("Erro ao realizar a pesquisa.");
            fornecedorList.getItems().clear();
        }
    }

    private boolean doesIdExist(int id) {
        String sql = "SELECT COUNT(*) FROM fornece WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
