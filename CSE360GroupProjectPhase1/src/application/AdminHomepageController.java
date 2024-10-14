package application;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Optional;

public class AdminHomepageController {

    @FXML
    private TableView<User> userTableView;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    private ObservableList<User> userList; // List to hold user data

    public void initialize() {
        // Initialize table columns and load users
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());

        // Load user data into the table
        loadUsers();
    }

    @FXML
    private void handleInviteUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Invite User");
        dialog.setHeaderText("Enter Email and Role (e.g., user@example.com, admin):");
        dialog.setContentText("Email and Role:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input -> {
            String[] parts = input.split(",");
            if (parts.length == 2) {
                String email = parts[0].trim();
                String role = parts[1].trim();
                boolean success = AdminManager.inviteUser(email, role);
                if (success) {
                    showAlert("User invited successfully.");
                } else {
                    showAlert("Error inviting user. Please check the details.");
                }
            } else {
                showAlert("Invalid input format. Please provide 'email, role'.");
            }
        });
    }

    @FXML
    private void handleResetUserPassword() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reset User Password");
        dialog.setHeaderText("Enter Username to Reset Password:");
        dialog.setContentText("Username:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(username -> {
            boolean success = AdminManager.resetUserPassword(username);
            if (success) {
                showAlert("Password reset successfully.");
            } else {
                showAlert("Error resetting password. User not found.");
            }
        });
    }

    @FXML
    private void handleDeleteUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete User");
        dialog.setHeaderText("Enter Username to Delete:");
        dialog.setContentText("Username:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(username -> {
            boolean confirmed = showConfirmationDialog("Are you sure you want to delete the user: " + username + "?");
            if (confirmed) {
                boolean success = AdminManager.deleteUser(username);
                if (success) {
                    showAlert("User deleted successfully.");
                    loadUsers(); // Refresh the user list
                } else {
                    showAlert("Error deleting user. User not found.");
                }
            }
        });
    }

    @FXML
    private void handleListUsers() {
        loadUsers();
    }

    @FXML
    private void handleLogout() {
        // Log out the user and redirect to login
       
       
    }

    private void loadUsers() {
        userList = FXCollections.observableArrayList(AdminManager.getAllUsers()); // Fetch users from database
        userTableView.setItems(userList);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmationDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(message);
        alert.setContentText("Confirm your action:");

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
