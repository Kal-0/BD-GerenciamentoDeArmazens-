package com.warehouse.warehouse.controller;

import com.warehouse.warehouse.database.DatabaseConnector;
import com.warehouse.warehouse.util.FieldValidation;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.Node;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class FuncionarioCriarController {

    // PF or PJ?
    @FXML private RadioButton pfRadioButton;
    @FXML private RadioButton pjRadioButton;
    @FXML private ToggleGroup typeToggleGroup;

    // Pessoa
    @FXML private TextField emailField;
    @FXML private TextField nomeField;
    @FXML private TextField cpfField;
    @FXML private TextField razaoSocialField;
    @FXML private TextField cnpjField;

    // Telefone
    @FXML private VBox phoneContainer;

    // Endereço
    @FXML private TextField ruaField;
    @FXML private TextField numeroField;
    @FXML private TextField bairroField;
    @FXML private TextField cidadeField;
    @FXML private ComboBox<String> estadoComboBox;
    @FXML private TextField cepField;

    // Funcionario
    @FXML private DatePicker dataContratacaoPicker;
    @FXML private TextField salarioField;
    @FXML private RadioButton ativoRadioButton;
    @FXML private RadioButton inativoRadioButton;
    @FXML private ToggleGroup statusToggleGroup;
    @FXML private ComboBox<String> departamentoComboBox;
    @FXML private ComboBox<String> gerenteComboBox;

    // Salvar
    @FXML private Label statusLabel;

    private final List<String> estados = Arrays.asList(
            "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA",
            "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN",
            "RS", "RO", "RR", "SC", "SP", "SE", "TO"
    );

    @FXML
    private void initialize() {
        typeToggleGroup = new ToggleGroup();
        pfRadioButton.setToggleGroup(typeToggleGroup);
        pjRadioButton.setToggleGroup(typeToggleGroup);
        pfRadioButton.setSelected(true); // pf default

        updateFieldAccess(pfRadioButton.getText());

        // Listener
        typeToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateFieldAccess(((RadioButton) newValue).getText());
            }
        });

        estadoComboBox.setItems(FXCollections.observableArrayList(estados));

        // Status
        statusToggleGroup = new ToggleGroup();
        ativoRadioButton.setToggleGroup(statusToggleGroup);
        inativoRadioButton.setToggleGroup(statusToggleGroup);
        ativoRadioButton.setSelected(true); // Ativo default

        populateDepartamentoComboBox();
        populateGerenteComboBox();

        addFieldValidators();

        if (phoneContainer.getChildren().isEmpty()) {
            addPhoneField();
        }
    }

    private void updateFieldAccess(String type) {
        boolean isPF = type.equals("Pessoa Física");
        nomeField.setDisable(!isPF);
        cpfField.setDisable(!isPF);

        razaoSocialField.setDisable(isPF);
        cnpjField.setDisable(isPF);

        if (isPF) {
            razaoSocialField.clear();
            cnpjField.clear();
        } else {
            nomeField.clear();
            cpfField.clear();
        }
    }

    private void addFieldValidators() {
        // Max length
        FieldValidation.setTextFieldLimit(emailField, 100);
        FieldValidation.setTextFieldLimit(nomeField, 100);
        FieldValidation.setTextFieldLimit(cpfField, 14);
        FieldValidation.setTextFieldLimit(razaoSocialField, 100);
        FieldValidation.setTextFieldLimit(cnpjField, 18);
        FieldValidation.setTextFieldLimit(ruaField, 50);
        FieldValidation.setTextFieldLimit(numeroField, 10);
        FieldValidation.setTextFieldLimit(bairroField, 50);
        FieldValidation.setTextFieldLimit(cidadeField, 50);
        FieldValidation.setTextFieldLimit(cepField, 9);

        // Numeric
        FieldValidation.setNumericField(cpfField);
        FieldValidation.setNumericField(cnpjField);
        FieldValidation.setNumericField(numeroField);
        FieldValidation.setNumericField(cepField);

        // Decimal salario
        FieldValidation.setDecimalField(salarioField, 10, 2);

        for (Node node : phoneContainer.getChildren()) {
            if (node instanceof TextField) {
                FieldValidation.setTextFieldLimit((TextField) node, 20);
                FieldValidation.setNumericField((TextField) node);
            }
        }
    }

    @FXML
    private void addPhoneField() {
        TextField newPhoneField = new TextField();
        newPhoneField.setMaxWidth(300);
        newPhoneField.setPromptText("Telefone");
        FieldValidation.setTextFieldLimit(newPhoneField, 20);
        FieldValidation.setNumericField(newPhoneField);
        phoneContainer.getChildren().add(newPhoneField);
    }

    @FXML
    private void removePhoneField() {
        int count = phoneContainer.getChildren().size();
        if (count > 1) {
            phoneContainer.getChildren().remove(count - 1);
        }
    }

    @FXML
    private void saveFuncionario() {
        if (!validateFields()) {
            return;
        }

        String email = emailField.getText().trim();
        RadioButton selectedRadioButton = (RadioButton) typeToggleGroup.getSelectedToggle();
        String tipo = selectedRadioButton.getText().equals("Pessoa Física") ? "PF" : "PJ";

        LocalDate dataContratacao = dataContratacaoPicker.getValue();
        String salario = salarioField.getText().trim();
        String status = ativoRadioButton.isSelected() ? "Ativo" : "Inativo";
        String departamento = departamentoComboBox.getValue();
        String gerente = gerenteComboBox.getValue();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false); // transaction

            // Insert into pessoa table
            stmt = conn.prepareStatement(
                    "INSERT INTO pessoa (email, tipo, nome, cpf, razao_social, cnpj) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, email.isEmpty() ? null : email);
            stmt.setString(2, tipo);
            stmt.setString(3, tipo.equals("PF") ? nomeField.getText().trim() : null);
            stmt.setString(4, tipo.equals("PF") ? cpfField.getText().trim() : null);
            stmt.setString(5, tipo.equals("PJ") ? razaoSocialField.getText().trim() : null);
            stmt.setString(6, tipo.equals("PJ") ? cnpjField.getText().trim() : null);
            stmt.executeUpdate();

            generatedKeys = stmt.getGeneratedKeys();
            long pessoaId = 0;
            if (generatedKeys.next()) {
                pessoaId = generatedKeys.getLong(1);

                // Insert into funcionario table
                stmt.close();
                stmt = conn.prepareStatement(
                        "INSERT INTO funcionario (data_contratacao, salario, status, fk_pessoa_id, fk_departamento_id, gerente_fk_funcionario_id) VALUES (?, ?, ?, ?, ?, ?)");
                stmt.setDate(1, dataContratacao != null ? Date.valueOf(dataContratacao) : null);
                stmt.setBigDecimal(2, salario.isEmpty() ? null : new BigDecimal(salario));
                stmt.setString(3, status);
                stmt.setLong(4, pessoaId);
                stmt.setObject(5, departamento == null || departamento.equals("Nenhum") ? null : getDepartamentoId(departamento));
                stmt.setObject(6, gerente == null || gerente.equals("Nenhum") ? null : getFuncionarioId(gerente));
                stmt.executeUpdate();

                // Insert telefones
                stmt.close();
                for (Node node : phoneContainer.getChildren()) {
                    if (node instanceof TextField) {
                        String phone = ((TextField) node).getText();
                        if (!phone.isEmpty()) {
                            stmt = conn.prepareStatement("INSERT INTO telefone (telefone, fk_pessoa_id) VALUES (?, ?)");
                            stmt.setString(1, phone);
                            stmt.setLong(2, pessoaId);
                            stmt.executeUpdate();
                        }
                    }
                }

                // Insert endereco
                stmt.close();
                stmt = conn.prepareStatement(
                        "INSERT INTO endereco (rua, numero, bairro, cidade, estado, cep, fk_pessoa_id) VALUES (?, ?, ?, ?, ?, ?, ?)");
                stmt.setString(1, ruaField.getText().isEmpty() ? null : ruaField.getText().trim());
                stmt.setString(2, numeroField.getText().isEmpty() ? null : numeroField.getText().trim());
                stmt.setString(3, bairroField.getText().isEmpty() ? null : bairroField.getText().trim());
                stmt.setString(4, cidadeField.getText().isEmpty() ? null : cidadeField.getText().trim());
                stmt.setString(5, estadoComboBox.getValue());
                stmt.setString(6, cepField.getText().isEmpty() ? null : cepField.getText().trim());
                stmt.setLong(7, pessoaId);
                stmt.executeUpdate();
            } else {
                throw new SQLException("Falha na criação do Funcionário: não obteve ID.");
            }

            conn.commit();
            statusLabel.setText("Funcionário criado com sucesso.");
            limparCampos();
        } catch (SQLException ex) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException se2) {
                se2.printStackTrace();
            }
            ex.printStackTrace();
            statusLabel.setText("Erro ao conectar com o banco de dados.");
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException ex) { /* Ignored */ }
            if (stmt != null) try { stmt.close(); } catch (SQLException ex) { /* Ignored */ }
            if (conn != null) try { conn.close(); } catch (SQLException ex) { /* Ignored */ }
        }
    }

    private void limparCampos() {
        emailField.clear();
        nomeField.clear();
        cpfField.clear();
        razaoSocialField.clear();
        cnpjField.clear();
        for (Node node : phoneContainer.getChildren()) {
            if (node instanceof TextField) {
                ((TextField) node).clear();
            }
        }
        ruaField.clear();
        numeroField.clear();
        bairroField.clear();
        cidadeField.clear();
        estadoComboBox.getSelectionModel().clearSelection();
        cepField.clear();
        dataContratacaoPicker.setValue(null);
        salarioField.clear();
        statusToggleGroup.selectToggle(ativoRadioButton);
        departamentoComboBox.getSelectionModel().clearSelection();
        gerenteComboBox.getSelectionModel().clearSelection();
        typeToggleGroup.selectToggle(pfRadioButton);}

    private void populateDepartamentoComboBox() {
        departamentoComboBox.getItems().clear();
        departamentoComboBox.getItems().add("Nenhum");
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT nome FROM departamento")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                departamentoComboBox.getItems().add(rs.getString("nome"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateGerenteComboBox() {
        gerenteComboBox.getItems().clear();
        gerenteComboBox.getItems().add("Nenhum");
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT p.nome, p.razao_social FROM pessoa p " +
                             "JOIN funcionario f ON p.id = f.fk_pessoa_id " +
                             "WHERE f.status = 'Ativo'")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String nome = rs.getString("nome");
                String razaoSocial = rs.getString("razao_social");
                if (nome != null && !nome.isEmpty()) {
                    gerenteComboBox.getItems().add(nome);
                } else if (razaoSocial != null && !razaoSocial.isEmpty()) {
                    gerenteComboBox.getItems().add(razaoSocial);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getDepartamentoId(String nome) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id FROM departamento WHERE nome = ?")) {
            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new SQLException("Departamento não encontrado: " + nome);
            }
        }
    }

    private int getFuncionarioId(String identifier) throws SQLException {
        if (identifier == null || identifier.isEmpty()) {
            return 0; // No gerente
        }
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT p.id FROM funcionario f JOIN pessoa p ON f.fk_pessoa_id = p.id WHERE p.nome = ? OR p.razao_social = ?")) {
            stmt.setString(1, identifier);
            stmt.setString(2, identifier);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new SQLException("Funcionário não encontrado: " + identifier);
            }
        }
    }

    private boolean validateFields() {
        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        String nome = nomeField.getText() != null ? nomeField.getText().trim() : "";
        String cpf = cpfField.getText() != null ? cpfField.getText().trim() : "";
        String razaoSocial = razaoSocialField.getText() != null ? razaoSocialField.getText().trim() : "";
        String cnpj = cnpjField.getText() != null ? cnpjField.getText().trim() : "";
        String salario = salarioField.getText().trim();

        if ((pfRadioButton.isSelected() && (nome.isEmpty() || cpf.isEmpty())) ||
                (pjRadioButton.isSelected() && (razaoSocial.isEmpty() || cnpj.isEmpty()))) {
            statusLabel.setText("Por favor, preencha os campos obrigatórios.");
            return false;
        }

        if (!email.isEmpty() && !FieldValidation.isUniqueField("email", email, 0)) {
            statusLabel.setText("Email já está em uso.");
            return false;
        }

        if (pfRadioButton.isSelected() && !cpf.isEmpty() && !FieldValidation.isUniqueField("cpf", cpf, 0)) {
            statusLabel.setText("CPF já está em uso.");
            return false;
        }

        if (pjRadioButton.isSelected() && !cnpj.isEmpty() && !FieldValidation.isUniqueField("cnpj", cnpj, 0)) {
            statusLabel.setText("CNPJ já está em uso.");
            return false;
        }

        if (!salario.isEmpty() && !salario.matches("\\d+(\\.\\d{1,2})?")) {
            statusLabel.setText("Por favor, insira um valor de salário válido.");
            return false;
        }

        return true;
    }


}
