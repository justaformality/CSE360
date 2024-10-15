package application;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LoginController {
    private Main mainApp;
    private Stage primaryStage;

    public LoginController(Main mainApp) {
        this.mainApp = mainApp;
        
    }

    public Scene createLoginScene(Stage primaryStage) {
        this.primaryStage = primaryStage; // Set reference to the stage

        // Create UI elements for the login scene
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        Button createAccountButton = new Button("Create Account"); // Create account button
        Label messageLabel = new Label();

        // Create layout
        GridPane loginLayout = new GridPane();
        loginLayout.setPadding(new Insets(10));
        loginLayout.setVgap(8);
        loginLayout.setHgap(10);
        loginLayout.add(usernameLabel, 0, 0);
        loginLayout.add(usernameField, 1, 0);
        loginLayout.add(passwordLabel, 0, 1);
        loginLayout.add(passwordField, 1, 1);
        loginLayout.add(loginButton, 1, 2);
        loginLayout.add(createAccountButton, 1, 3); // Add create account button
        loginLayout.add(messageLabel, 1, 4);

        // Handle login button click
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (validateLogin(username, password)) {
                mainApp.showHomepage();
            } else {
                messageLabel.setText("Invalid username or password.");
            }
        });

        // Handle "Create Account" button click
        createAccountButton.setOnAction(e -> {
            RegistrationController registrationController = new RegistrationController(mainApp);
            registrationController.showRegistrationScene(primaryStage); // Redirect to registration scene
        });

        // Create and return the login scene
        return new Scene(loginLayout, 300, 200);
    }

    private boolean validateLogin(String username, String password) {
    	try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT password, account_setup_complete, role FROM users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password"); // Get hashed password from the database

             // Verify the plain text password against the hashed password
                if (PasswordValidator.verifyPassword(password, dbPassword)) {
                    boolean accountSetupComplete = rs.getBoolean("account_setup_complete");
                    String role = rs.getString("role");
                    mainApp.showHomepage();
                } else {
                    showErrorAlert("Invalid password.");
                }
            } else {
                showErrorAlert("User not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Login failed: " + e.getMessage());
        }
       
        return "admin".equals(username) && "password".equals(password);
    }
    
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


