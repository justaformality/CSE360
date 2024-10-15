package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    private Stage primaryStage; // Hold the reference to the primary stage for redirection

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage; // Assign the stage reference
        DatabaseInitializer.initDatabase(); // Initialize database
        primaryStage.setTitle("Login System");
        showLoginScene(primaryStage); // Show login scene initially
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void showLoginScene(Stage primaryStage) {
        VBox layout = new VBox(10);
        Scene scene = new Scene(layout, 400, 300);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            loginUser(username, password); // Call the modified login method
        });

        Button createAccountButton = new Button("Create Account");
        createAccountButton.setOnAction(e -> showRegistrationScene(primaryStage));

        layout.getChildren().addAll(usernameField, passwordField, loginButton, createAccountButton);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loginUser(String username, String password) {
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
                    System.out.println("load admin_homepage");
                    // Load admin_homepage.fxml for admin users
                    loadPage("admin_homepage.fxml");
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
    }

    private void showRegistrationScene(Stage primaryStage) {
        VBox layout = new VBox(10);
        Scene scene = new Scene(layout, 400, 400);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password (8-12 characters, 1 uppercase, 1 lowercase, 1 number, 1 special)");

        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (!PasswordValidator.isValid(password)) {
                showErrorAlert("Invalid password format. Password must be 8-12 characters long, contain at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character.");
                return;
            }

            registerUser(username, password, primaryStage); // Call the modified method
        });

        layout.getChildren().addAll(usernameField, passwordField, registerButton);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAccountSetupScene(Stage primaryStage, String username) {
        VBox layout = new VBox(10);
        Scene scene = new Scene(layout, 400, 400);

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
                redirectToHomepage(username);
            } catch (SQLException ex) {
                ex.printStackTrace();
                showErrorAlert("Account setup failed: " + ex.getMessage());
            }
        });

        layout.getChildren().addAll(emailField, firstNameField, lastNameField, middleNameField, preferredNameField, finishButton);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void redirectToHomepage(String username) {
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT role FROM users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");

                // Load the appropriate homepage based on role
                if (role.equals("admin")) {
                    loadPage("admin_homepage.fxml");
                } else if (role.equals("student")) {
                    loadPage("student_homepage.fxml");
                } else if (role.equals("instructor")) {
                    loadPage("instructor_homepage.fxml");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Failed to redirect: " + e.getMessage());
        }
    }

   private void loadPage(String fxmlFile) {
        try {
        	 System.out.println("entering loadPage");
        	FXMLLoader loader = new FXMLLoader(getClass().getResource("/"+fxmlFile));
        	System.out.println(""+fxmlFile);
            Parent root = loader.load();
            System.out.println("FXMLfile loaded");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
           //Stage stage = (Stage) userTableView.getScene().getWindow();
            //stage.setScene(new Scene(root));
            //stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Page load failed: " + e.getMessage());
        }
    }


    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void registerUser(String username, String password, Stage primaryStage) {
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
            showLoginScene(primaryStage);
        } catch (SQLException ex) {
            ex.printStackTrace();
            showErrorAlert("Registration failed: " + ex.getMessage());
        }
    }
}
