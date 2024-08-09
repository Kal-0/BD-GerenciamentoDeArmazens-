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

public class FuncionarioEditarController {

    @FXML private TextField nomeField, cpfField, razaoSocialField, cnpjField;
    @FXML private TextField emailField, ruaField, numeroField, bairroField, cidadeField, cepField;
    @FXML private ComboBox<String> estadoComboBox, departamentoComboBox, gerenteComboBox;
    @FXML private VBox phoneContainer;
    @FXML private Label statusLabel;
    @FXML private RadioButton pfRadioButton, pjRadioButton, ativoRadioButton, inativoRadioButton;
    @FXML private ToggleGroup typeToggleGroup, statusToggleGroup;
    @FXML private DatePicker dataContratacaoPicker;
    @FXML private TextField salarioField;

    private long selectedFuncionarioId;

    private final List<String> estados = Arrays.asList(
            "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA",
            "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN",
            "RS", "RO", "RR", "SC", "SP", "SE", "TO"
    );

    public void setSelectedFuncionarioId(long funcionarioId) {
        this.selectedFuncionarioId = funcionarioId;
        loadFuncionarioData();
    }

    @FXML
    private void initialize() {
        typeToggleGroup = new ToggleGroup();
        pfRadioButton.setToggleGroup(typeToggleGroup);
        pjRadioButton.setToggleGroup(typeToggleGroup);
        typeToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> updateFieldAccess(((RadioButton) newValue).getText()));
        estadoComboBox.getItems().addAll(estados);
        statusToggleGroup = new ToggleGroup();
        ativoRadioButton.setToggleGroup(statusToggleGroup);
        inativoRadioButton.setToggleGroup(statusToggleGroup);

        loadDepartamentoData();
        loadGerenteData();

        addFieldValidators();
    }

    private void updateFieldAccess(String type) {
        boolean isPF = "Pessoa Física".equals(type);
        nomeField.setDisable(!isPF);
        cpfField.setDisable(!isPF);
        razaoSocialField.setDisable(isPF);
        cnpjField.setDisable(isPF);
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

    private void loadFuncionarioData() {
        String sql = "SELECT * FROM pessoa LEFT JOIN endereco ON pessoa.id = endereco.fk_pessoa_id " +
                "LEFT JOIN funcionario ON pessoa.id = funcionario.fk_pessoa_id WHERE pessoa.id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, selectedFuncionarioId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                loadPersonData(rs);
                loadAddressData(rs);
                loadPhoneData(conn);
                loadFuncionarioSpecificData(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            statusLabel.setText("Erro ao carregar os dados do funcionário.");
        }
    }

    private void loadPersonData(ResultSet rs) throws SQLException {
        emailField.setText(rs.getString("email"));
        if ("PF".equals(rs.getString("tipo"))) {
            pfRadioButton.setSelected(true);
            nomeField.setText(rs.getString("nome"));
            cpfField.setText(rs.getString("cpf"));
        } else {
            pjRadioButton.setSelected(true);
            razaoSocialField.setText(rs.getString("razao_social"));
            cnpjField.setText(rs.getString("cnpj"));
        }
    }

    private void loadAddressData(ResultSet rs) throws SQLException {
        ruaField.setText(rs.getString("rua"));
        numeroField.setText(rs.getString("numero"));
        bairroField.setText(rs.getString("bairro"));
        cidadeField.setText(rs.getString("cidade"));
        estadoComboBox.setValue(rs.getString("estado"));
        cepField.setText(rs.getString("cep"));
    }

    private void loadPhoneData(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT telefone FROM telefone WHERE fk_pessoa_id = ?");
        stmt.setLong(1, selectedFuncionarioId);
        ResultSet rs = stmt.executeQuery();
        phoneContainer.getChildren().clear();
        while (rs.next()) {
            addPhoneFieldWithData(rs.getString("telefone"));
        }
    }

    private void loadFuncionarioSpecificData(ResultSet rs) throws SQLException {
        Date dateContratacao = rs.getDate("data_contratacao");
        if (dateContratacao != null) {
            dataContratacaoPicker.setValue(dateContratacao.toLocalDate());
        }
        salarioField.setText(rs.getString("salario"));
        if ("Ativo".equals(rs.getString("status"))) {
            ativoRadioButton.setSelected(true);
        } else {
            inativoRadioButton.setSelected(true);
        }
        departamentoComboBox.setValue(rs.getString("fk_departamento_id"));
        gerenteComboBox.setValue(rs.getString("gerente_fk_funcionario_id"));
    }

    private void loadDepartamentoData() {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, nome FROM departamento")) {

            ResultSet rs = stmt.executeQuery();
            departamentoComboBox.getItems().add("Nenhum");
            while (rs.next()) {
                departamentoComboBox.getItems().add(rs.getString("id") + " - " + rs.getString("nome"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            statusLabel.setText("Erro ao carregar os departamentos.");
        }
    }

    private void loadGerenteData() {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT p.id AS fk_pessoa_id, p.tipo, p.nome, p.razao_social " +
                             "FROM funcionario f " +
                             "JOIN pessoa p ON f.fk_pessoa_id = p.id " +
                             "WHERE f.fk_departamento_id IS NOT NULL")) {

            ResultSet rs = stmt.executeQuery();
            gerenteComboBox.getItems().add("Nenhum");
            while (rs.next()) {
                String gerenteDisplayName;
                String tipo = rs.getString("tipo");
                if ("PF".equals(tipo)) {
                    gerenteDisplayName = rs.getString("fk_pessoa_id") + " - " + rs.getString("nome");
                } else {
                    gerenteDisplayName = rs.getString("fk_pessoa_id") + " - " + rs.getString("razao_social");
                }
                gerenteComboBox.getItems().add(gerenteDisplayName);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            statusLabel.setText("Erro ao carregar os gerentes.");
        }
    }

    @FXML
    private void addPhoneField() {
        addPhoneFieldWithData("");
    }

    @FXML
    private void addPhoneFieldWithData(String phoneNumber) {
        TextField newPhoneField = new TextField(phoneNumber);
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
    private void updateFuncionario() {
        if (!validateFields()) {
            return;
        }

        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        String nome = nomeField.getText() != null ? nomeField.getText().trim() : "";
        String cpf = cpfField.getText() != null ? cpfField.getText().trim() : "";
        String razaoSocial = razaoSocialField.getText() != null ? razaoSocialField.getText().trim() : "";
        String cnpj = cnpjField.getText() != null ? cnpjField.getText().trim() : "";
        String rua = ruaField.getText() != null ? ruaField.getText().trim() : "";
        String numero = numeroField.getText() != null ? numeroField.getText().trim() : "";
        String bairro = bairroField.getText() != null ? bairroField.getText().trim() : "";
        String cidade = cidadeField.getText() != null ? cidadeField.getText().trim() : "";
        String cep = cepField.getText() != null ? cepField.getText().trim() : "";
        String estado = estadoComboBox.getValue() != null ? estadoComboBox.getValue() : "";

        LocalDate dataContratacao = dataContratacaoPicker.getValue();
        String salario = salarioField.getText().trim();
        String status = ativoRadioButton.isSelected() ? "Ativo" : "Inativo";
        String departamento = departamentoComboBox.getValue();
        String gerente = gerenteComboBox.getValue();

        Connection conn = null;
        PreparedStatement stmtPerson = null;
        PreparedStatement stmtAddress = null;

        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false); // transaction

            // Update person
            stmtPerson = conn.prepareStatement(
                    "UPDATE pessoa SET email=?, nome=?, cpf=?, razao_social=?, cnpj=?, tipo=? WHERE id=?");
            stmtPerson.setString(1, email.isEmpty() ? null : email);
            stmtPerson.setString(2, pfRadioButton.isSelected() ? (nome.isEmpty() ? null : nome) : null);
            stmtPerson.setString(3, pfRadioButton.isSelected() ? (cpf.isEmpty() ? null : cpf) : null);
            stmtPerson.setString(4, pjRadioButton.isSelected() ? (razaoSocial.isEmpty() ? null : razaoSocial) : null);
            stmtPerson.setString(5, pjRadioButton.isSelected() ? (cnpj.isEmpty() ? null : cnpj) : null);
            stmtPerson.setString(6, pfRadioButton.isSelected() ? "PF" : "PJ");
            stmtPerson.setLong(7, selectedFuncionarioId);
            stmtPerson.executeUpdate();

            // Update address
            stmtAddress = conn.prepareStatement(
                    "UPDATE endereco SET rua=?, numero=?, bairro=?, cidade=?, estado=?, cep=? WHERE fk_pessoa_id=?");
            stmtAddress.setString(1, rua.isEmpty() ? null : rua);
            stmtAddress.setString(2, numero.isEmpty() ? null : numero);
            stmtAddress.setString(3, bairro.isEmpty() ? null : bairro);
            stmtAddress.setString(4, cidade.isEmpty() ? null : cidade);
            stmtAddress.setString(5, estado.isEmpty() ? null : estado);
            stmtAddress.setString(6, cep.isEmpty() ? null : cep);
            stmtAddress.setLong(7, selectedFuncionarioId);
            stmtAddress.executeUpdate();

            // Update funcionario data
            PreparedStatement stmtFuncionario = conn.prepareStatement(
                    "UPDATE funcionario SET data_contratacao=?, salario=?, status=?, fk_departamento_id=?, gerente_fk_funcionario_id=? WHERE fk_pessoa_id=?");
            stmtFuncionario.setDate(1, dataContratacao != null ? Date.valueOf(dataContratacao) : null);
            stmtFuncionario.setBigDecimal(2, salario.isEmpty() ? null : new BigDecimal(salario));
            stmtFuncionario.setString(3, status);

            if (departamento == null || departamento.equals("Nenhum")) {
                stmtFuncionario.setNull(4, java.sql.Types.INTEGER);
            } else {
                stmtFuncionario.setInt(4, Integer.parseInt(departamento.split(" - ")[0]));
            }

            if (gerente == null || gerente.equals("Nenhum")) {
                stmtFuncionario.setNull(5, java.sql.Types.INTEGER);
            } else {
                stmtFuncionario.setInt(5, Integer.parseInt(gerente.split(" - ")[0]));
            }

            stmtFuncionario.setLong(6, selectedFuncionarioId);
            stmtFuncionario.executeUpdate();

            // Update phone
            updatePhoneData(conn);

            conn.commit();
            statusLabel.setText("Funcionário atualizado com sucesso.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            statusLabel.setText("Erro ao atualizar os dados do funcionário.");
        } finally {
            try {
                if (stmtPerson != null) stmtPerson.close();
                if (stmtAddress != null) stmtAddress.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean validateFields() {
        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        String nome = nomeField.getText() != null ? nomeField.getText().trim() : "";
        String cpf = cpfField.getText() != null ? cpfField.getText().trim() : "";
        String razaoSocial = razaoSocialField.getText() != null ? razaoSocialField.getText().trim() : "";
        String cnpj = cnpjField.getText() != null ? cnpjField.getText().trim() : "";

        String salario = salarioField.getText() != null ? salarioField.getText().trim() : "";

        if ((pfRadioButton.isSelected() && (nome.isEmpty() || cpf.isEmpty())) ||
                (pjRadioButton.isSelected() && (razaoSocial.isEmpty() || cnpj.isEmpty()))) {
            statusLabel.setText("Por favor, preencha os campos obrigatórios.");
            return false;
        }

        if (!email.isEmpty() && !FieldValidation.isUniqueField("email", email, selectedFuncionarioId)) {
            statusLabel.setText("Email já está em uso.");
            return false;
        }

        if (pfRadioButton.isSelected() && !cpf.isEmpty() && !FieldValidation.isUniqueField("cpf", cpf, selectedFuncionarioId)) {
            statusLabel.setText("CPF já está em uso.");
            return false;
        }

        if (pjRadioButton.isSelected() && !cnpj.isEmpty() && !FieldValidation.isUniqueField("cnpj", cnpj, selectedFuncionarioId)) {
            statusLabel.setText("CNPJ já está em uso.");
            return false;
        }

        if (!salario.isEmpty() && !salario.matches("\\d+(\\.\\d{1,2})?")) {
            statusLabel.setText("Por favor, insira um valor de salário válido.");
            return false;
        }

        return true;
    }

    private void updatePhoneData(Connection conn) throws SQLException {
        try (PreparedStatement stmtDelete = conn.prepareStatement("DELETE FROM telefone WHERE fk_pessoa_id = ?")) {
            stmtDelete.setLong(1, selectedFuncionarioId);
            stmtDelete.executeUpdate();
        }

        try (PreparedStatement stmtInsert = conn.prepareStatement("INSERT INTO telefone (telefone, fk_pessoa_id) VALUES (?, ?)")) {
            for (Node node : phoneContainer.getChildren()) {
                TextField tf = (TextField) node;
                if (!tf.getText().isEmpty()) {
                    stmtInsert.setString(1, tf.getText());
                    stmtInsert.setLong(2, selectedFuncionarioId);
                    stmtInsert.executeUpdate();
                }
            }
        }
    }
}
