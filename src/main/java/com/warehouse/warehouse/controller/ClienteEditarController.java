package com.warehouse.warehouse.controller;

import com.warehouse.warehouse.database.DatabaseConnector;
import com.warehouse.warehouse.util.FieldValidation;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.Node;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class ClienteEditarController {

    @FXML private TextField nomeField, cpfField, razaoSocialField, cnpjField;
    @FXML private TextField emailField, ruaField, numeroField, bairroField, cidadeField, cepField;
    @FXML private ComboBox<String> estadoComboBox;
    @FXML private VBox phoneContainer;
    @FXML private Label statusLabel;
    @FXML private RadioButton pfRadioButton, pjRadioButton;
    @FXML private ToggleGroup typeToggleGroup;

    private long selectedClientId;

    private final List<String> estados = Arrays.asList(
            "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA",
            "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN",
            "RS", "RO", "RR", "SC", "SP", "SE", "TO"
    );

    public void setSelectedClientId(long clientId) {
        this.selectedClientId = clientId;
        loadClientData();
    }

    @FXML
    private void initialize() {
        typeToggleGroup = new ToggleGroup();
        pfRadioButton.setToggleGroup(typeToggleGroup);
        pjRadioButton.setToggleGroup(typeToggleGroup);
        typeToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> updateFieldAccess(((RadioButton) newValue).getText()));
        estadoComboBox.getItems().addAll(estados);

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

        for (Node node : phoneContainer.getChildren()) {
            if (node instanceof TextField) {
                FieldValidation.setTextFieldLimit((TextField) node, 20);
                FieldValidation.setNumericField((TextField) node);
            }
        }
    }

    private void loadClientData() {
        String sql = "SELECT * FROM pessoa LEFT JOIN endereco ON pessoa.id = endereco.fk_pessoa_id WHERE pessoa.id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, selectedClientId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                loadPersonData(rs);
                loadAddressData(rs);
                loadPhoneData(conn);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            statusLabel.setText("Erro ao carregar os dados do cliente.");
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
        stmt.setLong(1, selectedClientId);
        ResultSet rs = stmt.executeQuery();
        phoneContainer.getChildren().clear();
        while (rs.next()) {
            addPhoneFieldWithData(rs.getString("telefone"));
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
    private void updateClient() {
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
            stmtPerson.setLong(7, selectedClientId);
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
            stmtAddress.setLong(7, selectedClientId);
            stmtAddress.executeUpdate();

            // Update phone
            updatePhoneData(conn);

            conn.commit();
            statusLabel.setText("Cliente atualizado com sucesso.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            statusLabel.setText("Erro ao atualizar os dados do cliente.");
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

        if ((pfRadioButton.isSelected() && (nome.isEmpty() || cpf.isEmpty())) ||
                (pjRadioButton.isSelected() && (razaoSocial.isEmpty() || cnpj.isEmpty()))) {
            statusLabel.setText("Por favor, preencha os campos obrigatórios.");
            return false;
        }

        if (!email.isEmpty() && !FieldValidation.isUniqueField("email", email, selectedClientId)) {
            statusLabel.setText("Email já está em uso.");
            return false;
        }

        if (pfRadioButton.isSelected() && !cpf.isEmpty() && !FieldValidation.isUniqueField("cpf", cpf, selectedClientId)) {
            statusLabel.setText("CPF já está em uso.");
            return false;
        }

        if (pjRadioButton.isSelected() && !cnpj.isEmpty() && !FieldValidation.isUniqueField("cnpj", cnpj, selectedClientId)) {
            statusLabel.setText("CNPJ já está em uso.");
            return false;
        }

        return true;
    }

    private void updatePhoneData(Connection conn) throws SQLException {
        try (PreparedStatement stmtDelete = conn.prepareStatement("DELETE FROM telefone WHERE fk_pessoa_id = ?")) {
            stmtDelete.setLong(1, selectedClientId);
            stmtDelete.executeUpdate();
        }

        try (PreparedStatement stmtInsert = conn.prepareStatement("INSERT INTO telefone (telefone, fk_pessoa_id) VALUES (?, ?)")) {
            for (Node node : phoneContainer.getChildren()) {
                TextField tf = (TextField) node;
                if (!tf.getText().isEmpty()) {
                    stmtInsert.setString(1, tf.getText());
                    stmtInsert.setLong(2, selectedClientId);
                    stmtInsert.executeUpdate();
                }
            }
        }
    }
}
