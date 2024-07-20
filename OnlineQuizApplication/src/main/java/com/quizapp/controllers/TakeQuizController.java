package com.quizapp.controllers;

import com.quizapp.utils.DatabaseUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TakeQuizController {
    @FXML
    private VBox quizBox;
    @FXML
    private Label questionLabel;
    @FXML
    private ToggleGroup optionsGroup;
    @FXML
    private RadioButton option1Radio;
    @FXML
    private RadioButton option2Radio;
    @FXML
    private RadioButton option3Radio;
    @FXML
    private RadioButton option4Radio;
    @FXML
    private Button nextQuestionButton;

    private int currentQuestionIndex = 0;
    private int score = 0;
    private ResultSet quizQuestions;

    @FXML
    public void initialize() {
        loadQuizQuestions();
    }

    private void loadQuizQuestions() {
        try {
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM questions WHERE quiz_id = ?");
            stmt.setInt(1, 1); // Replace with the actual quiz ID
            quizQuestions = stmt.executeQuery();

            if (quizQuestions.next()) {
                loadQuestion();
            } else {
                showAlert("No Questions", "This quiz has no questions.");
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred. Please try again.");
        }
    }

    private void loadQuestion() {
        try {
            questionLabel.setText(quizQuestions.getString("question_text"));
            int questionId = quizQuestions.getInt("id");

            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM options WHERE question_id = ?");
            stmt.setInt(1, questionId);
            ResultSet options = stmt.executeQuery();

            optionsGroup.getToggles().clear();
            option1Radio.setText("");
            option2Radio.setText("");
            option3Radio.setText("");
            option4Radio.setText("");

            int optionIndex = 0;
            while (options.next()) {
                String optionText = options.getString("option_text");
                boolean isCorrect = options.getBoolean("is_correct");

                RadioButton optionRadio;
                switch (optionIndex) {
                    case 0:
                        optionRadio = option1Radio;
                        break;
                    case 1:
                        optionRadio = option2Radio;
                        break;
                    case 2:
                        optionRadio = option3Radio;
                        break;
                    case 3:
                        optionRadio = option4Radio;
                        break;
                    default:
                        continue;
                }

                optionRadio.setText(optionText);
                optionRadio.setUserData(isCorrect);

                optionIndex++;
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred. Please try again.");
        }
    }

    @FXML
    public void nextQuestion(ActionEvent event) {
        RadioButton selectedOption = (RadioButton) optionsGroup.getSelectedToggle();
        if (selectedOption != null) {
            boolean isCorrect = (boolean) selectedOption.getUserData();
            if (isCorrect) {
                score++;
            }
        }

        try {
            if (quizQuestions.next()) {
                loadQuestion();
            } else {
                showAlert("Quiz Completed", "You scored " + score + " points.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred. Please try again.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
