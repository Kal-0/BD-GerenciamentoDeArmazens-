package com.warehouse.warehouse.util;

import com.warehouse.warehouse.database.DatabaseConnector;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class FieldValidation {

    public static void setTextFieldLimit(TextField textField, int maxLength) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > maxLength) {
                textField.setText(oldValue);
            }
        });
    }

    public static void setNumericField(TextField textField) {
        Pattern validEditingState = Pattern.compile("\\d*");
        TextFormatter<?> textFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText != null && validEditingState.matcher(newText).matches()) {
                return change;
            } else {
                return null;
            }
        });
        textField.setTextFormatter(textFormatter);
    }

    public static void setDecimalField(TextField textField, int precision, int scale) {
        Pattern validEditingState = Pattern.compile("^\\d{0," + (precision - scale) + "}(\\.\\d{0," + scale + "})?$");
        TextFormatter<?> textFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText != null && validEditingState.matcher(newText).matches()) {
                return change;
            } else {
                return null;
            }
        });
        textField.setTextFormatter(textFormatter);
    }

    public static void setTextAreaLimit(TextArea textArea, int maxLength) {
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > maxLength) {
                textArea.setText(oldValue);
            }
        });
    }

    public static boolean isUniqueField(String fieldName, String value, long idToExclude) {
        String query = String.format("SELECT COUNT(*) FROM pessoa WHERE %s = ? AND id != ?", fieldName);

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, value);
            stmt.setLong(2, idToExclude);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
