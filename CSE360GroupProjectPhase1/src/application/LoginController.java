package application;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
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
            if (validateLogin(username, password, primaryStage)) {
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
    
    private boolean validateLogin(String username, String password, Stage primaryStage) {
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT password, account_setup_complete, role, first_name, last_name FROM users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password"); // Get hashed password from the database

                // Verify the plain text password against the hashed password
                if (PasswordValidator.verifyPassword(password, dbPassword)) {
                    boolean accountSetupComplete = rs.getBoolean("account_setup_complete");
                    String role = rs.getString("role");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");

                    // If account setup is incomplete (no first name, last name, or not marked complete), show account setup
                    if (firstName == null || lastName == null || !accountSetupComplete) {
                        showAccountSetupScene(primaryStage, username);
                    } else {
                        // Redirect to the appropriate homepage based on role
                    	mainApp.showHomepage();
                        //redirectToHomepage(primaryStage, role);
                    }
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

        return false; // Return false as a fallback; no need for default admin/password check anymore
    }

    
    private void showAccountSetupScene(Stage primaryStage, String username) {
        VBox layout = new VBox(10);
        Scene scene = new Scene(layout, 400, 400);

        // Create UI elements for account setup
        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");

        TextField middleNameField = new TextField();
        middleNameField.setPromptText("Middle Name");

        TextField preferredNameField = new TextField();
        preferredNameField.setPromptText("Preferred Name");

        Button finishButton = new Button("Finish");

        finishButton.setOnAction(e -> {
            String email = emailField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String middleName = middleNameField.getText();
            String preferredName = preferredNameField.getText();

            try (Connection conn = DatabaseConnection.connect()) {
                // Update user details and mark account setup as complete
                String updateSql = "UPDATE users SET email = ?, first_name = ?, last_name = ?, middle_name = ?, preferred_name = ?, account_setup_complete = TRUE WHERE username = ?";
                PreparedStatement pstmt = conn.prepareStatement(updateSql);
                pstmt.setString(1, email);
                pstmt.setString(2, firstName);
                pstmt.setString(3, lastName);
                pstmt.setString(4, middleName);
                pstmt.setString(5, preferredName);
                pstmt.setString(6, username);
                pstmt.executeUpdate();
                
                // Redirect to the appropriate homepage based on user role
                mainApp.showHomepage();
                //redirectToHomepage(primaryStage, getRoleByUsername(username));
            } catch (SQLException ex) {
                ex.printStackTrace();
                showErrorAlert("Account setup failed: " + ex.getMessage());
            }
        });

        layout.getChildren().addAll(emailField, firstNameField, lastNameField, middleNameField, preferredNameField, finishButton);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


