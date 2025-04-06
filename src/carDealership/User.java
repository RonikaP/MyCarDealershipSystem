package carDealership;

import persistance.DBManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Car Dealership System
 * Abstract base class for all user types in the system
 *
 * @author Ronika Patel (40156217)
 * @author Nazim Chaib Cherif-Baza (40017992)
 * @author Andrea Delgado Anderson (40315869)
 * @author Grace Pan (40302283)
 * @author Bao Tran Nguyen (40257379)
 * @author Michael Persico (40090861)
 * @since 1.8
 */
public abstract class User {
    protected int id;
    protected String username;
    protected String password;
    protected String name;
    protected String email;
    protected String phone;
    protected String role;
    protected boolean isActive;
    protected String joinDate;
    protected boolean isTempPassword;
    protected Map<String, Boolean> permissions; // Dynamic permissions
    
    /**
     * Constructor for the User class
     * Creates a user with the specified attributes
     *
     * @param id - the user's unique identifier
     * @param username - the user's login username
     * @param password - the user's password
     * @param name - the user's full name
     * @param email - the user's email address
     * @param phone - the user's phone number
     * @param role - the user's role in the system
     * @param isActive - whether the user account is active
     * @param joinDate - the date the user joined the system
     * @param isTempPassword - whether the password is temporary and requires changing
     */
    public User(int id, String username, String password, String name, String email, String phone,
                String role, boolean isActive, String joinDate, boolean isTempPassword) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.isActive = isActive;
        this.joinDate = joinDate;
        this.isTempPassword = isTempPassword;
        this.permissions = new HashMap<>();
        loadPermissions();
    }

    /**
     * Load user permissions from the database
     * Populates the permissions map with permission names and their enabled status
     */
    public void loadPermissions() {
        try {
            DBManager db = DBManager.getInstance();
            String query = "SELECT p.permission_name, up.is_enabled " +
                           "FROM user_permissions up " +
                           "JOIN permissions p ON up.permission_id = p.permission_id " +
                           "WHERE up.user_id = ?";
            ResultSet rs = db.runQuery(query, id); 
            permissions.clear();
            while (rs.next()) {
                permissions.put(rs.getString("permission_name"), rs.getBoolean("is_enabled"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading permissions: " + e.getMessage());
        }
    }

    /**
     * Get the complete map of user permissions
     *
     * @return a map containing all permission names and their enabled status
     */
    public Map<String, Boolean> getPermissionsMap() {
        return permissions;
    }

    /**
     * Get a formatted string of enabled permissions
     *
     * @return a comma-separated list of enabled permissions, or "None (Inactive)" if user is inactive
     */
    public String getPermissions() {
        if (!isActive) {
            return "None (Inactive)";
        }
        return permissions.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(", "));
    }

    /**
     * Verify if a provided password matches the user's stored password
     *
     * @param passwordAttempt - the password to check
     * @return true if the password matches, false otherwise
     * @throws Exception if a validation error occurs
     */
    public boolean checkPassword(String passwordAttempt) throws Exception {
        System.out.println("Stored password: " + this.password);
        System.out.println("Attempted password: " + passwordAttempt);
        return this.password.trim().equals(passwordAttempt);
    }

    /**
     * Getter method for the user ID
     *
     * @return the user's ID
     */
    public int getId() { return id; }
    
    /**
     * Getter method for the username
     *
     * @return the user's username
     */
    public String getUsername() { return username; }
    
    /**
     * Getter method for the user role
     *
     * @return the user's role
     */
    public String getRole() { return role; }
    
    /**
     * Getter method for the user's name
     *
     * @return the user's full name
     */
    public String getName() { return name; }
    
    /**
     * Getter method for the user's email
     *
     * @return the user's email address
     */
    public String getEmail() { return email; }
    
    /**
     * Getter method for the user's phone number
     *
     * @return the user's phone number
     */
    public String getPhone() { return phone; }
    
    /**
     * Check if the user account is active
     *
     * @return true if the account is active, false otherwise
     */
    public boolean isActive() { return isActive; }
    
    /**
     * Getter method for the user's join date
     *
     * @return the date when the user joined the system
     */
    public String getJoinDate() { return joinDate; }
    
    /**
     * Check if the user has a temporary password
     *
     * @return true if the password is temporary, false otherwise
     */
    public boolean isTempPassword() { return isTempPassword; }
    
    /**
     * Set a new password for the user
     * Also updates the database and clears the temporary password flag
     *
     * @param password - the new password to set
     * @throws Exception if a database error occurs
     */
    public void setPassword(String password) throws Exception {
        this.password = password;
        this.isTempPassword = false;
        updatePasswordInDB();
    }

    /**
     * Update the user's password in the database
     * Sets is_temp_password to 0 (false)
     *
     * @throws Exception if a database error occurs
     */
    private void updatePasswordInDB() throws Exception {
        DBManager db = DBManager.getInstance();
        String query = "UPDATE users SET password = '" + this.password + "', is_temp_password = 0 WHERE user_id = " + this.id;
        db.runUpdate(query);    
    }

    /**
     * Set the active status of the user account
     * Also updates the database with the new status
     *
     * @param isActive - the new active status to set
     */
    public void setActive(boolean isActive) {
        this.isActive = isActive;
        try {
            updateActiveInDB();
        } catch (SQLException e) {
            // Handle the exception, e.g., log an error message
            System.err.println("Error updating active status: " + e.getMessage());
        }
    }

    /**
     * Update the user's active status in the database
     *
     * @throws SQLException if a database error occurs
     */
    private void updateActiveInDB() throws SQLException {
        DBManager db = DBManager.getInstance();
        String query = "UPDATE users SET is_active = " + (isActive ? 1 : 0) + " WHERE user_id = " + this.id;
        db.runUpdate(query);
    }

    /**
     * Load a user from the database by username
     * Creates and returns the appropriate user subclass instance based on role
     *
     * @param username - the username to search for
     * @return the User object if found, null otherwise
     * @throws SQLException if a database error occurs
     * @throws Exception for other errors
     */
    public static User loadUser(String username) throws SQLException, Exception {
        DBManager db = DBManager.getInstance();
        ResultSet rs = db.runQuery("SELECT u.*, r.role_name FROM users u JOIN roles r " +
                                   "ON u.role_id = r.role_id WHERE u.username = '" + username + "'");
        if (rs.next()) {
            String role = rs.getString("role_name");
            String password = rs.getString("password");
            boolean isTempPassword = rs.getInt("is_temp_password") == 1;
            boolean isActive = rs.getInt("is_active") == 1; // Fetch is_active from DB
    
            switch (role) {
                case "Admin":
                    return new Admin(rs.getInt("user_id"), rs.getString("username"), password,
                                     rs.getString("name"), rs.getString("email"), rs.getString("phone"),
                                     isTempPassword, isActive);
                case "Manager":
                    return new Manager(rs.getInt("user_id"), rs.getString("username"), password,
                                       rs.getString("name"), rs.getString("email"), rs.getString("phone"),
                                       isTempPassword, isActive);
                case "Salesperson":
                    return new Salesperson(rs.getInt("user_id"), rs.getString("username"), password,
                                           rs.getString("name"), rs.getString("email"), rs.getString("phone"),
                                           isTempPassword, isActive);
                default:
                    throw new SQLException("Unknown role: " + role);
            }
        }
        return null;
    }
}

/**
 * Administrator user with system management capabilities
 * Has access to user management, password resets, and all system functions
 *
 * @author [Your Name] [Your ID]
 * @author [Team Member Name] [Team Member ID]
 * @since 1.8
 */
class Admin extends User {
    /**
     * Constructor for the Admin class
     *
     * @param id - the admin's unique identifier
     * @param username - the admin's login username
     * @param password - the admin's password
     * @param name - the admin's full name
     * @param email - the admin's email address
     * @param phone - the admin's phone number
     * @param isTempPassword - whether the password is temporary and requires changing
     * @param isActive - whether the admin account is active
     */
    public Admin(int id, String username, String password, String name, String email, String phone,
                 boolean isTempPassword, boolean isActive) {
        super(id, username, password, name, email, phone, "Admin", isActive, LocalDate.now().toString(), isTempPassword);
    }

    /**
     * Create a new user account in the system
     * Inserts a new user record into the database with a temporary password
     *
     * @param role - the role for the new user (Admin, Manager, or Salesperson)
     * @param username - the login username for the new user
     * @param password - the initial password for the new user
     * @param name - the full name of the new user
     * @param email - the email address of the new user
     * @param phone - the phone number of the new user
     * @throws SQLException if a database error occurs
     * @throws Exception for other errors
     */
    public void createUser(String role, String username, String password, String name, String email, String phone)
            throws SQLException, Exception {
        int role_id;
        switch (role.toUpperCase()) {
            case "ADMIN": role_id = 1; break;
            case "MANAGER": role_id = 2; break;
            case "SALESPERSON": role_id = 3; break;
            default: throw new IllegalArgumentException("Invalid role: " + role);
        }
        DBManager db = DBManager.getInstance();
        String query = "INSERT INTO users (username, password, role_id, name, email, phone, is_temp_password) VALUES (?, ?, ?, ?, ?, ?, 1)";
        db.runInsert(query, username, password, role_id, name, email, phone);
    }

    /**
     * Reset a user's password
     * Sets a new temporary password for the specified user
     *
     * @param user - the user whose password to reset
     * @param newPassword - the new temporary password to set
     * @return true if the password was successfully reset, false otherwise
     * @throws Exception if a database error occurs
     */
    public boolean resetPassword(User var1, String var2) throws Exception {
        if (var1 != null && var1.isActive()) {
            var1.setPassword(var2); // Set the new password in the User object
            DBManager db = DBManager.getInstance();
            String query = "UPDATE users SET password = '" + var2 + "', is_temp_password = 1 WHERE user_id = " + var1.getId();
            db.runUpdate(query); // Execute the update query
            return true;
        } else {
            return false;
        }
    }
}

/**
 * Manager user with inventory and sales management capabilities
 * Has access to vehicle management and some user functions
 *
 * @author [Your Name] [Your ID]
 * @author [Team Member Name] [Team Member ID]
 * @since 1.8
 */
class Manager extends User {
    /**
     * Constructor for the Manager class
     *
     * @param id - the manager's unique identifier
     * @param username - the manager's login username
     * @param password - the manager's password
     * @param name - the manager's full name
     * @param email - the manager's email address
     * @param phone - the manager's phone number
     * @param isTempPassword - whether the password is temporary and requires changing
     * @param isActive - whether the manager account is active
     */
    public Manager(int id, String username, String password, String name, String email, String phone,
                   boolean isTempPassword, boolean isActive) {
        super(id, username, password, name, email, phone, "Manager", isActive, LocalDate.now().toString(), isTempPassword);
    }
}

/**
 * Salesperson user with basic system access
 * Primarily focused on vehicle sales and inventory viewing
 *
 * @author [Your Name] [Your ID]
 * @author [Team Member Name] [Team Member ID]
 * @since 1.8
 */
class Salesperson extends User {
    /**
     * Constructor for the Salesperson class
     *
     * @param id - the salesperson's unique identifier
     * @param username - the salesperson's login username
     * @param password - the salesperson's password
     * @param name - the salesperson's full name
     * @param email - the salesperson's email address
     * @param phone - the salesperson's phone number
     * @param isTempPassword - whether the password is temporary and requires changing
     * @param isActive - whether the salesperson account is active
     */
    public Salesperson(int id, String username, String password, String name, String email, String phone, boolean isTempPassword, boolean isActive) {
        super(id, username, password, name, email, phone, "Salesperson", isActive, LocalDate.now().toString() , isTempPassword);
    }
}
