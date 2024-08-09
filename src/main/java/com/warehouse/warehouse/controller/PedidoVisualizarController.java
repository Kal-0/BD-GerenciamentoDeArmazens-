package com.warehouse.warehouse.controller;

import com.warehouse.warehouse.database.DatabaseConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PedidoVisualizarController {

    @FXML
    private Label pedidoIdLabel;
    @FXML
    private Label clienteLabel;
    @FXML
    private Label funcionarioLabel;
    @FXML
    private Label valorTotalLabel;
    @FXML
    private Label descontoLabel;
    @FXML
    private Label dataExpedicaoLabel;
    @FXML
    private ListView<String> carrinhoListView;

    private long pedidoId;

    public void setSelectedPedidoId(long pedidoId) {
        this.pedidoId = pedidoId;
        loadPedidoDetails();
    }

    private void loadPedidoDetails() {
        String pedidoSql = "SELECT pedido.id, cliente.nome AS cliente, funcionario.nome AS funcionario, pedido.valor_total, pedido.desconto, pedido.data_expedicao " +
                "FROM pedido " +
                "JOIN pessoa cliente ON pedido.fk_cliente_id = cliente.id " +
                "JOIN pessoa funcionario ON pedido.fk_funcionario_id = funcionario.id " +
                "WHERE pedido.id = ?";

        String carrinhoSql = "SELECT produto.nome, carrinho.quantidade " +
                "FROM carrinho " +
                "JOIN produto ON carrinho.fk_produto_id = produto.id " +
                "WHERE carrinho.fk_pedido_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pedidoStmt = conn.prepareStatement(pedidoSql);
             PreparedStatement carrinhoStmt = conn.prepareStatement(carrinhoSql)) {

            pedidoStmt.setLong(1, this.pedidoId);
            ResultSet pedidoRs = pedidoStmt.executeQuery();
            if (pedidoRs.next()) {
                pedidoIdLabel.setText(String.valueOf(pedidoRs.getLong("id")));
                clienteLabel.setText(pedidoRs.getString("cliente"));
                funcionarioLabel.setText(pedidoRs.getString("funcionario"));
                valorTotalLabel.setText(String.format("R$ %.2f", pedidoRs.getDouble("valor_total")));
                descontoLabel.setText(String.format("R$ %.2f", pedidoRs.getDouble("desconto")));
                if (pedidoRs.getDate("data_expedicao") != null) {
                    dataExpedicaoLabel.setText(pedidoRs.getDate("data_expedicao").toString());
                } else {
                    dataExpedicaoLabel.setText("N/A");
                }
            }

            carrinhoStmt.setLong(1, this.pedidoId);
            ResultSet carrinhoRs = carrinhoStmt.executeQuery();
            ObservableList<String> carrinhoItems = FXCollections.observableArrayList();
            while (carrinhoRs.next()) {
                String item = String.format("%s - Quantidade: %d", carrinhoRs.getString("nome"), carrinhoRs.getInt("quantidade"));
                carrinhoItems.add(item);
            }
            carrinhoListView.setItems(carrinhoItems);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
