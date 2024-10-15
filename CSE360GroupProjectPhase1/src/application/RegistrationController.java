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

public class RegistrationController {
    private Main mainApp;
    private Stage primaryStage;

    public RegistrationController(Main mainApp) {
        this.mainApp = mainApp;
    }

    public void showRegistrationScene(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Create UI elements for the registration form
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Label confirmPasswordLabel = new Label("Confirm Password:");
        PasswordField confirmPasswordField = new PasswordField();
        Button registerButton = new Button("Register");
        Label messageLabel = new Label();

        // Create layout
        GridPane registrationLayout = new GridPane();
        registrationLayout.setPadding(new Insets(10));
        registrationLayout.setVgap(8);
        registrationLayout.setHgap(10);
        registrationLayout.add(usernameLabel, 0, 0);
        registrationLayout.add(usernameField, 1, 0);
        registrationLayout.add(passwordLabel, 0, 1);
        registrationLayout.add(passwordField, 1, 1);
        registrationLayout.add(confirmPasswordLabel, 0, 2);
        registrationLayout.add(confirmPasswordField, 1, 2);
        registrationLayout.add(registerButton, 1, 3);
        registrationLayout.add(messageLabel, 1, 4);

        // Handle register button click
        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (registerUser(username, password, confirmPassword)) {
                messageLabel.setText("Registration successful!");
                showLoginScene();
            } else {
                messageLabel.setText("Registration failed. Try again.");
            }
        });

        // Set and show the registration scene
        Scene registrationScene = new Scene(registrationLayout, 300, 200);
        primaryStage.setScene(registrationScene);
        primaryStage.show();
    }

    // Method to handle user registration logic
    private boolean registerUser(String username, String password, String confirmPassword) {
    	 try (Connection conn = DatabaseConnection.connect()) {
             // Check if there are any existing users
             String checkSql = "SELECT COUNT(*) FROM users";
             PreparedStatement checkPstmt = conn.prepareStatement(checkSql);
             ResultSet rs = checkPstmt.executeQuery();
             rs.next();
             boolean isFirstUser = rs.getInt(1) == 0;

             // Assign role: 'admin' for first user, 'student' by default for others
             String role = isFirstUser ? "admin" : "student";

             // Hash the password before storing it
             String hashedPassword = PasswordValidator.hashPassword(password);

             // Save user to the database with role
             String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
             PreparedStatement pstmt = conn.prepareStatement(sql);
             pstmt.setString(1, username);
             pstmt.setString(2, hashedPassword); // Save the hashed password
             pstmt.setString(3, role); // Assign the role
             pstmt.executeUpdate();

           // Redirect to the login scene
           showLoginScene();
           return true;
         } catch (SQLException ex) {
             ex.printStackTrace();
             return false;

         }
    }

    // Method to show the login scene after successful registration
    private void showLoginScene() {
        LoginController loginController = new LoginController(mainApp);
        Scene loginScene = loginController.createLoginScene(primaryStage);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }
}

