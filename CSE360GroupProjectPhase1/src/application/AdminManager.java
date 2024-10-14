package application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminManager {

    // Method to invite a user by sending an invitation email and creating an entry in the database
    public static boolean inviteUser(String email, String role) {
        // Here you can implement logic to send an invitation email.
        // For now, we just generate a one-time invitation code.
        String invitationCode = UUID.randomUUID().toString();
        
        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "INSERT INTO invitations (email, role, code) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, role);
            pstmt.setString(3, invitationCode);
            pstmt.executeUpdate();

            // Send invitation email logic here (you can use JavaMail API)

            return true; // Invitation sent successfully
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Failed to invite user
        }
    }

    // Method to reset a user's password
    public static boolean resetUserPassword(String username) {
        String newPassword = generateRandomPassword(); // Method to generate a new password
        String expirationDate = generateExpirationDate(); // Generate expiration date (e.g., 1 hour from now)

        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "UPDATE users SET password = ?, password_expiration = ? WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newPassword);
            pstmt.setString(2, expirationDate);
            pstmt.setString(3, username);
            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                // Send new password to user email logic here (e.g., using JavaMail API)
                return true; // Password reset successfully
            } else {
                return false; // User not found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Error occurred
        }
    }

    // Method to delete a user account
    public static boolean deleteUser(String username) {
        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "DELETE FROM users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            int rowsDeleted = pstmt.executeUpdate();

            return rowsDeleted > 0; // Return true if a user was deleted
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Error occurred
        }
    }

    // Method to retrieve all users from the database
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "SELECT * FROM users";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Assuming you have a User class with a constructor
                User user = new User(
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("role")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users; // Return the list of users
    }

    // Method to generate a random password (for demonstration)
    private static String generateRandomPassword() {
        // Implement your logic to generate a secure random password
        return "TempPassword123!"; // Placeholder for now
    }

    // Method to generate an expiration date for the temporary password
    private static String generateExpirationDate() {
        // Generate expiration date logic, e.g., current time + 1 hour
        return new Timestamp(System.currentTimeMillis() + 3600000).toString(); // 1 hour from now
    }
}
