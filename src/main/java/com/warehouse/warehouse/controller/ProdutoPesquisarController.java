package com.warehouse.warehouse.controller;

import com.warehouse.warehouse.database.DatabaseConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoPesquisarController {

    @FXML private Label titleLabel;
    @FXML private TextField searchNameField, searchIdField, searchCategoryField;
    @FXML private ComboBox<String> categoriaComboBox;
    @FXML private ListView<String> productList;
    @FXML private Label statusLabel;

    private List<Integer> categoriaIds;

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        titleLabel.setText("Pesquisar Produto");
        carregarCategorias();
        productList.setItems(FXCollections.observableArrayList());
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


    @FXML
    private void handleSearch() {
        ObservableList<String> products = FXCollections.observableArrayList();
        StringBuilder sql = new StringBuilder("SELECT produto.id, produto.nome, produto.descricao, categoria.nome AS" +
                " categoria_nome FROM produto JOIN categoria ON produto.fk_categoria_id = categoria.id WHERE 1=1");

        if (!searchNameField.getText().isEmpty()) {
            sql.append(" AND LOWER(produto.nome) LIKE ?");
        }
        if (!searchIdField.getText().isEmpty()) {
            sql.append(" AND CAST(produto.id AS CHAR) LIKE ?");
        }
//        if (!searchCategoryField.getText().isEmpty()) {
//            sql.append(" AND LOWER(categoria.nome) LIKE ?");
//        }
        if (!categoriaComboBox.getSelectionModel().isEmpty()) {
            sql.append(" AND LOWER(categoria.nome) = ?");
        }

        sql.append(" ORDER BY produto.id");

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int index = 1;
            if (!searchNameField.getText().isEmpty()) {
                stmt.setString(index++, "%" + searchNameField.getText().toLowerCase() + "%");
            }
            if (!searchIdField.getText().isEmpty()) {
                stmt.setString(index++, "%" + searchIdField.getText() + "%");
            }
//            if (!searchCategoryField.getText().isEmpty()) {
//                stmt.setString(index, "%" + searchCategoryField.getText().toLowerCase() + "%");
//            }
            if (!categoriaComboBox.getSelectionModel().isEmpty()) {
                stmt.setString(index++, categoriaComboBox.getSelectionModel().getSelectedItem() );
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String displayText = String.format("ID: %d - Nome: %s - Categoria: %s", rs.getInt("id"), rs.getString("nome"), rs.getString("categoria_nome"));
                products.add(displayText);
            }

            productList.setItems(products);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleEditSelected() {
        String selectedProductInfo = productList.getSelectionModel().getSelectedItem();

        if (selectedProductInfo != null && mainController != null) {
            long productId = Long.parseLong(selectedProductInfo.split(" - ")[0].split(": ")[1]);
            mainController.loadViewId("ProdutoEditarView", productId);
        }
    }
}
