package com.warehouse.warehouse.controller;

import com.warehouse.warehouse.database.DatabaseConnector;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class PedidoPesquisarController {

    @FXML private Label titleLabel;
    @FXML private TextField searchNumeroField, searchClienteField, searchFuncionarioField;
    @FXML private ListView<String> pedidoList;

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        titleLabel.setText("Pesquisar Pedido");
        pedidoList.setItems(FXCollections.observableArrayList());
    }

    @FXML
    private void handleSearch() {
        ObservableList<String> pedidos = FXCollections.observableArrayList();
        StringBuilder sql = new StringBuilder("SELECT pedido.id, cliente.nome AS cliente, funcionario.nome AS funcionario " +
                "FROM pedido " +
                "JOIN pessoa cliente ON pedido.fk_cliente_id = cliente.id " +
                "JOIN pessoa funcionario ON pedido.fk_funcionario_id = funcionario.id " +
                "WHERE 1=1");

        if (!searchNumeroField.getText().isEmpty()) {
            sql.append(" AND pedido.id LIKE ?");
        }
        if (!searchClienteField.getText().isEmpty()) {
            sql.append(" AND (LOWER(cliente.nome) LIKE ?)");
        }
        if (!searchFuncionarioField.getText().isEmpty()) {
            sql.append(" AND (LOWER(funcionario.nome) LIKE ?)");
        }

        sql.append(" ORDER BY pedido.id");

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int index = 1;
            if (!searchNumeroField.getText().isEmpty()) {
                stmt.setString(index++, "%" + searchNumeroField.getText() + "%");
            }
            if (!searchClienteField.getText().isEmpty()) {
                stmt.setString(index++, "%" + searchClienteField.getText().toLowerCase() + "%");
            }
            if (!searchFuncionarioField.getText().isEmpty()) {
                stmt.setString(index, "%" + searchFuncionarioField.getText().toLowerCase() + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String displayText = String.format("Pedido: %d - Cliente: %s - Funcionário: %s",
                        rs.getInt("id"), rs.getString("cliente"), rs.getString("funcionario"));
                pedidos.add(displayText);
            }

            pedidoList.setItems(pedidos);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleViewSelected() {
        String selectedPedidoInfo = pedidoList.getSelectionModel().getSelectedItem();

        if (selectedPedidoInfo != null && mainController != null) {
            long pedidoId = Long.parseLong(selectedPedidoInfo.split(" - ")[0].split(": ")[1]);
            mainController.loadViewPedido("PedidoVisualizarView", pedidoId);
        }
    }
}
