package com.warehouse.warehouse.controller;

import com.warehouse.warehouse.database.DatabaseConnector;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DashboardController {

    @FXML
    private Label totalClientesLabel;

    @FXML
    private Label totalFuncionariosLabel;

    @FXML
    private Label totalFornecedoresLabel;

    @FXML
    private Label totalProdutosLabel;

    @FXML
    private Label totalPedidosLabel;

    @FXML
    private ListView<String> bestSellingProductsList;

    @FXML
    private ListView<String> outOfStockProductsList;

    @FXML
    private Label totalVendasMesLabel;

    @FXML
    private Label totalPedidosMesLabel;

    @FXML
    private Label funcionarioMesLabel;

    @FXML
    private Label clienteMesLabel;

    @FXML
    private void initialize() {
        updateDashboard();
    }

    private void updateDashboard() {
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement()) {

            // Total Clientes
            String queryClientes = "SELECT COUNT(*) AS total FROM cliente";
            ResultSet rs = stmt.executeQuery(queryClientes);
            if (rs.next()) {
                totalClientesLabel.setText(String.valueOf(rs.getInt("total")));
            }

            // Total Funcionários
            String queryFuncionarios = "SELECT COUNT(*) AS total FROM funcionario";
            rs = stmt.executeQuery(queryFuncionarios);
            if (rs.next()) {
                totalFuncionariosLabel.setText(String.valueOf(rs.getInt("total")));
            }

            // Total Fornecedores
            String queryFornecedores = "SELECT COUNT(*) AS total FROM fornecedor";
            rs = stmt.executeQuery(queryFornecedores);
            if (rs.next()) {
                totalFornecedoresLabel.setText(String.valueOf(rs.getInt("total")));
            }

            // Total Produtos
            String queryProdutos = "SELECT COUNT(*) AS total FROM produto";
            rs = stmt.executeQuery(queryProdutos);
            if (rs.next()) {
                totalProdutosLabel.setText(String.valueOf(rs.getInt("total")));
            }

            // Total Pedidos
            String queryPedidos = "SELECT COUNT(*) AS total FROM pedido";
            rs = stmt.executeQuery(queryPedidos);
            if (rs.next()) {
                totalPedidosLabel.setText(String.valueOf(rs.getInt("total")));
            }

            // Produtos Mais Vendidos
            String queryBestSellingProducts = "SELECT nome, SUM(quantidade) AS total_vendido " +
                    "FROM produto p JOIN carrinho c ON p.id = c.fk_produto_id " +
                    "GROUP BY nome ORDER BY total_vendido DESC LIMIT 5";
            rs = stmt.executeQuery(queryBestSellingProducts);
            ObservableList<String> bestSellingProducts = FXCollections.observableArrayList();
            while (rs.next()) {
                bestSellingProducts.add(rs.getString("nome") + " - " + rs.getInt("total_vendido") + " vendas");
            }
            bestSellingProductsList.setItems(bestSellingProducts);

            // Produtos Esgotados
            String queryOutOfStockProducts = "SELECT nome FROM produto WHERE quantidade_estoque <= 0";
            rs = stmt.executeQuery(queryOutOfStockProducts);
            ObservableList<String> outOfStockProducts = FXCollections.observableArrayList();
            while (rs.next()) {
                outOfStockProducts.add(rs.getString("nome"));
            }
            outOfStockProductsList.setItems(outOfStockProducts);

            // Total Vendas do Mês
            String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            String queryTotalVendasMes = "SELECT SUM(valor_total) AS total_vendas_mes FROM pedido " +
                    "WHERE DATE_FORMAT(created_at, '%Y-%m') = '" + currentMonth + "'";
            rs = stmt.executeQuery(queryTotalVendasMes);
            if (rs.next()) {
                totalVendasMesLabel.setText("R$ " + String.format("%.2f", rs.getDouble("total_vendas_mes")));
            }

            // Total Pedidos do Mês
            String queryTotalPedidosMes = "SELECT COUNT(*) AS total_pedidos_mes FROM pedido " +
                    "WHERE DATE_FORMAT(created_at, '%Y-%m') = '" + currentMonth + "'";
            rs = stmt.executeQuery(queryTotalPedidosMes);
            if (rs.next()) {
                totalPedidosMesLabel.setText(String.valueOf(rs.getInt("total_pedidos_mes")));
            }

            // Funcionário do Mês
            String queryFuncionarioMes = "SELECT " +
                    "CASE WHEN p.tipo = 'PF' THEN p.nome ELSE p.razao_social END AS nome, " +
                    "SUM(pe.valor_total) AS total_vendas " +
                    "FROM pessoa p JOIN pedido pe ON p.id = pe.fk_funcionario_id " +
                    "WHERE DATE_FORMAT(pe.created_at, '%Y-%m') = '" + currentMonth + "' " +
                    "GROUP BY p.tipo, p.nome, p.razao_social " +
                    "ORDER BY total_vendas DESC LIMIT 1";
            rs = stmt.executeQuery(queryFuncionarioMes);
            if (rs.next()) {
                funcionarioMesLabel.setText(rs.getString("nome") + " - R$ " + String.format("%.2f", rs.getDouble("total_vendas")));
            } else {
                funcionarioMesLabel.setText("N/A");
            }

            // Cliente do Mês
            String queryClienteMes = "SELECT " +
                    "CASE WHEN p.tipo = 'PF' THEN p.nome ELSE p.razao_social END AS nome, " +
                    "COUNT(pe.id) AS total_pedidos " +
                    "FROM pessoa p JOIN pedido pe ON p.id = pe.fk_cliente_id " +
                    "WHERE DATE_FORMAT(pe.created_at, '%Y-%m') = '" + currentMonth + "' " +
                    "GROUP BY p.tipo, p.nome, p.razao_social " +
                    "ORDER BY total_pedidos DESC LIMIT 1";
            rs = stmt.executeQuery(queryClienteMes);
            if (rs.next()) {
                clienteMesLabel.setText(rs.getString("nome") + " - " + rs.getInt("total_pedidos") + " pedido(s)");
            } else {
                clienteMesLabel.setText("N/A");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
