package com.warehouse.warehouse.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.event.ActionEvent;

public class MainController {

    public StackPane contentArea;

    public void loadInitialDashboard() {
        loadView("DashboardView");
    }

    public void novoPedido(ActionEvent event) { loadView("PedidoNovoView");}

    public void pesquisarPedido(ActionEvent event) { loadView("PedidoPesquisarView");}

    public void criarProduto(ActionEvent event) {
        loadView("ProdutoCriarView");
    }

    public void pesquisarProduto(ActionEvent event) {
        loadView("ProdutoPesquisarView");
    }

    public void categoriaPesquisar(ActionEvent event) {
        loadView("CategoriaPesquisarView");
    }

    public void categoriaCriar(ActionEvent event) {
        loadView("CategoriaCriarView");
    }

    public void criarCliente(ActionEvent event) {
        loadView("ClienteCriarView");
    }

    public void pesquisarCliente(ActionEvent event) {
        loadView("ClientePesquisarView");
    }

    public void criarFuncionario(ActionEvent event) {
        loadView("FuncionarioCriarView");
    }

    public void pesquisarFuncionario(ActionEvent event) {
        loadView("FuncionarioPesquisarView");
    }

    public void departamentoCriar(ActionEvent event) { loadView("DepartamentoCriarView");}

    public void departamentoPesquisar(ActionEvent event) { loadView("DepartamentoPesquisarView");}

    public void criarFornecedor(ActionEvent event) {
        loadView("FornecedorCriarView");
    }

    public void pesquisarFornecedor(ActionEvent event) {
        loadView("FornecedorPesquisarView");
    }

    public void fornecimentoPesquisar(ActionEvent event) {
        loadView("FornecimentoPesquisarView");
    }

    public void fornecimentoCriar(ActionEvent event) {
        loadView("FornecimentoCriarView");
    }

    public void loadDashboard(ActionEvent event) { loadView("DashboardView"); }

    public void pedidoCriar(ActionEvent event) {
        loadView("PedidoCriarView");
    }

    public void visualizarPedido(ActionEvent event) {
        loadView("PedidoVisualizarView");
    }

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/warehouse/warehouse/" + fxmlFile + ".fxml"));
            Node view = loader.load();
            Object controller = loader.getController();
            if (controller instanceof ClientePesquisarController) {
                ((ClientePesquisarController) controller).setMainController(this);
            } else if (controller instanceof FornecedorPesquisarController) {
                ((FornecedorPesquisarController) controller).setMainController(this);
            } else if (controller instanceof DepartamentoPesquisarController) {
                ((DepartamentoPesquisarController) controller).setMainController(this);
            } else if (controller instanceof FuncionarioPesquisarController) {
                ((FuncionarioPesquisarController) controller).setMainController(this);
            }
            else if (controller instanceof ProdutoPesquisarController) {
                ((ProdutoPesquisarController) controller).setMainController(this);
            }
            else if (controller instanceof CategoriaPesquisarController) {
                ((CategoriaPesquisarController) controller).setMainController(this);
            }
            else if (controller instanceof PedidoPesquisarController) {
                ((PedidoPesquisarController) controller).setMainController(this);
            }
            contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading " + fxmlFile + ": " + e.getMessage());
        }
    }
    public void loadViewCliente(String fxmlFile, long clientId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/warehouse/warehouse/" + fxmlFile + ".fxml"));
            Node view = loader.load();
            if (fxmlFile.equals("ClienteEditarView")) {
                ClienteEditarController controller = loader.getController();
                controller.setSelectedClientId(clientId);
            }
            contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void loadViewFornecedor(String fxmlFile, long fornecedorId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/warehouse/warehouse/" + fxmlFile + ".fxml"));
            Node view = loader.load();
            if (fxmlFile.equals("FornecedorEditarView")) {
                FornecedorEditarController controller = loader.getController();
                controller.setSelectedFornecedorId(fornecedorId);
            }
            contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadViewDepartamento(String fxmlFile, long departamentoId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/warehouse/warehouse/" + fxmlFile + ".fxml"));
            Node view = loader.load();
            if (fxmlFile.equals("DepartamentoEditarView")) {
                DepartamentoEditarController controller = loader.getController();
                controller.setSelectedDepartamentoId(departamentoId);
            }
            contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void loadViewFuncionario(String fxmlFile, long funcionarioId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/warehouse/warehouse/" + fxmlFile + ".fxml"));
            Node view = loader.load();
            if (fxmlFile.equals("FuncionarioEditarView")) {
                FuncionarioEditarController controller = loader.getController();
                controller.setSelectedFuncionarioId(funcionarioId);
            }
            contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadViewId(String fxmlFile, long tableId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/warehouse/warehouse/" + fxmlFile + ".fxml"));
            Node view = loader.load();

            if (fxmlFile.equals("ClienteEditarView")) {
                ClienteEditarController controller = loader.getController();
                controller.setSelectedClientId(tableId);
            }
            else if (fxmlFile.equals("FornecedorEditarView")) {
                FornecedorEditarController controller = loader.getController();
                controller.setSelectedFornecedorId(tableId);
            }
            else if (fxmlFile.equals("FuncionarioEditarView")) {
                FuncionarioEditarController controller = loader.getController();
                controller.setSelectedFuncionarioId(tableId);
            }
            else if (fxmlFile.equals("DepartamentoEditarView")) {
                DepartamentoEditarController controller = loader.getController();
                controller.setSelectedDepartamentoId(tableId);
            }
            else if (fxmlFile.equals("ProdutoEditarView")) {
                ProdutoEditarController controller = loader.getController();
                controller.setSelectedProductId(tableId);
            }
            else if (fxmlFile.equals("CategoriaEditarView")) {
                CategoriaEditarController controller = loader.getController();
                controller.setSelectedCategoryId(tableId);
            }
            contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadViewPedido(String fxmlFile, long pedidoId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/warehouse/warehouse/" + fxmlFile + ".fxml"));
            Node view = loader.load();
            if (fxmlFile.equals("PedidoVisualizarView")) {
                PedidoVisualizarController controller = loader.getController();
                controller.setSelectedPedidoId(pedidoId);
            }
            contentArea.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

