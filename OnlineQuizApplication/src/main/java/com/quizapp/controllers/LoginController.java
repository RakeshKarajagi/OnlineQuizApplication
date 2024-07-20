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
import java.sql.ResultSet;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;

    public void login(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        System.out.println("Trying to login with username: " + username);

        try {
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                System.out.println("Stored hashed password: " + storedHashedPassword);
                
                if (BCryptUtil.checkpw(password, storedHashedPassword)) {
                    String role = rs.getString("role");
                    System.out.println("Login successful, user role: " + role);

                    // Load appropriate UI based on role
                    if ("ADMIN".equals(role)) {
                        loadUI("/com/quizapp/view/AdminDashboard.fxml");
                    } else {
                        loadUI("/com/quizapp/view/UserDashboard.fxml");
                    }
                } else {
                    showAlert("Login Failed", "Invalid username or password.");
                }
            } else {
                showAlert("Login Failed", "Invalid username or password.");
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred. Please try again.");
        }
    }

    public void goToRegistration(ActionEvent event) {
        loadUI("/com/quizapp/view/Registration.fxml"); // Redirect to registration page
    }

    private void loadUI(String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Scene scene = loginButton.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
