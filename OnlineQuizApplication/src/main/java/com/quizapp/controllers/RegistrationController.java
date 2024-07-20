package com.quizapp.controllers;

import com.quizapp.utils.BCryptUtil;
import com.quizapp.utils.DatabaseUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegistrationController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button registerButton;
    @FXML
    private Button backButton;

    public void register(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Registration Failed", "Username and password cannot be empty.");
            return;
        }

        try {
            Connection conn = DatabaseUtil.getConnection();
            String hashedPassword = BCryptUtil.hashpw(password);
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password, role) VALUES (?, ?, 'USER')");
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                showAlert("Registration Successful", "You have been registered successfully.");
                loadUI("/com/quizapp/view/Login.fxml"); // Redirect to login page
            } else {
                showAlert("Registration Failed", "Registration failed. Please try again.");
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred. Please try again.");
        }
    }

    public void goBack(ActionEvent event) {
        loadUI("/com/quizapp/view/Login.fxml"); // Redirect to login page
    }

    private void loadUI(String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Scene scene = registerButton.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
