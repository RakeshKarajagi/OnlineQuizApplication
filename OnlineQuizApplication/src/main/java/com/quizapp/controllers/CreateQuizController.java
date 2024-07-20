package com.quizapp.controllers;

import com.quizapp.utils.DatabaseUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CreateQuizController {
    @FXML
    private TextField quizTitleField;
    @FXML
    private TextField quizDescriptionField;
    @FXML
    private VBox questionsBox;
    @FXML
    private Button addQuestionButton;
    @FXML
    private Button saveQuizButton;

    @FXML
    public void initialize() {
        // Remove the call to addQuestion() here
    }

    @FXML
    public void addQuestion(ActionEvent event) {
        // Add new question fields to the VBox
        TextField questionField = new TextField();
        questionField.setPromptText("Enter question");
        TextField option1Field = new TextField();
        option1Field.setPromptText("Option 1");
        TextField option2Field = new TextField();
        option2Field.setPromptText("Option 2");
        TextField option3Field = new TextField();
        option3Field.setPromptText("Option 3");
        TextField option4Field = new TextField();
        option4Field.setPromptText("Option 4");
        TextField correctOptionField = new TextField();
        correctOptionField.setPromptText("Correct Option (1-4)");

        questionsBox.getChildren().addAll(questionField, option1Field, option2Field, option3Field, option4Field, correctOptionField);
    }

    @FXML
    public void saveQuiz(ActionEvent event) {
        String quizTitle = quizTitleField.getText();
        String quizDescription = quizDescriptionField.getText();

        try {
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO quizzes (title, description) VALUES (?, ?)");
            stmt.setString(1, quizTitle);
            stmt.setString(2, quizDescription);
            stmt.executeUpdate();

            // Get the generated quiz ID
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            int quizId = -1;
            if (generatedKeys.next()) {
                quizId = generatedKeys.getInt(1);
            }

            // Save questions
            for (int i = 0; i < questionsBox.getChildren().size(); i += 6) {
                String questionText = ((TextField) questionsBox.getChildren().get(i)).getText();
                String option1 = ((TextField) questionsBox.getChildren().get(i + 1)).getText();
                String option2 = ((TextField) questionsBox.getChildren().get(i + 2)).getText();
                String option3 = ((TextField) questionsBox.getChildren().get(i + 3)).getText();
                String option4 = ((TextField) questionsBox.getChildren().get(i + 4)).getText();
                int correctOption = Integer.parseInt(((TextField) questionsBox.getChildren().get(i + 5)).getText());

                PreparedStatement questionStmt = conn.prepareStatement("INSERT INTO questions (quiz_id, question_text) VALUES (?, ?)");
                questionStmt.setInt(1, quizId);
                questionStmt.setString(2, questionText);
                questionStmt.executeUpdate();

                // Get the generated question ID
                ResultSet questionKeys = questionStmt.getGeneratedKeys();
                int questionId = -1;
                if (questionKeys.next()) {
                    questionId = questionKeys.getInt(1);
                }

                // Save options
                saveOption(conn, questionId, option1, correctOption == 1);
                saveOption(conn, questionId, option2, correctOption == 2);
                saveOption(conn, questionId, option3, correctOption == 3);
                saveOption(conn, questionId, option4, correctOption == 4);
            }

            showAlert("Success", "Quiz created successfully.");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred. Please try again.");
        }
    }

    private void saveOption(Connection conn, int questionId, String optionText, boolean isCorrect) throws Exception {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO options (question_id, option_text, is_correct) VALUES (?, ?, ?)");
        stmt.setInt(1, questionId);
        stmt.setString(2, optionText);
        stmt.setBoolean(3, isCorrect);
        stmt.executeUpdate();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
