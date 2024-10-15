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
    private AdminHomepageController adminHomepageController;
    private LoginController loginController;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage; // Assign the stage reference
        loginController = new LoginController(this);
        adminHomepageController = new AdminHomepageController(this);
        DatabaseInitializer.initDatabase(); // Initialize database
                
        showLoginScene(); // Show login scene initially
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void showLoginScene() {
        primaryStage.setScene(loginController.createLoginScene(primaryStage));
        primaryStage.setTitle("Login System");
        primaryStage.show();
    }
   
    
    public void showHomepage() {
        primaryStage.setScene(adminHomepageController.createHomepageScene());
    }

    

   

 /*   private void showAccountSetupScene(Stage primaryStage, String username) {
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
              //  showErrorAlert("Account setup failed: " + ex.getMessage());
            }
        });

        layout.getChildren().addAll(emailField, firstNameField, lastNameField, middleNameField, preferredNameField, finishButton);
        primaryStage.setScene(scene);
        primaryStage.show();
    } */

/*    private void redirectToHomepage(String username) {
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
          //  showErrorAlert("Failed to redirect: " + e.getMessage());
        }
    } */

 


  /*  private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }*/
    
 }
