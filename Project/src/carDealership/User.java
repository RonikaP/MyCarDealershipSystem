package carDealership;

import persistance.DBManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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

    private void loadPermissions() {
        try {
            DBManager db = DBManager.getInstance();
            String query = "SELECT p.permission_name, up.is_enabled " +
                           "FROM user_permissions up " +
                           "JOIN permissions p ON up.permission_id = p.permission_id " +
                           "WHERE up.user_id = " + this.id;
            ResultSet rs = db.runQuery(query);
            while (rs.next()) {
                permissions.put(rs.getString("permission_name"), rs.getBoolean("is_enabled"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading permissions: " + e.getMessage());
        }
    }

    public abstract String getPermissions();

    // Not using this method in the current implementation
    //public static String hashPassword(String password) throws Exception {
        //MessageDigest md = MessageDigest.getInstance("SHA-256");
        //byte[] hash = md.digest(password.getBytes());
        //StringBuilder sb = new StringBuilder();
        //for (byte b : hash) {
          //  sb.append(String.format("%02x", b));
        //}
        //return sb.toString();
    //}

    // Load permissions from the database


    public boolean checkPassword(String passwordAttempt) throws Exception {
        System.out.println("Stored password: " + this.password);
        System.out.println("Attempted password: " + passwordAttempt);
        return this.password.trim().equals(passwordAttempt);
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public boolean isActive() { return isActive; }
    public String getJoinDate() { return joinDate; }
    public boolean isTempPassword() { return isTempPassword; }
    
    public void setPassword(String password) throws Exception {
        this.password = password;
        this.isTempPassword = false;
        updatePasswordInDB();
    }


    // Update the password in the database
    private void updatePasswordInDB() throws Exception {
        DBManager db = DBManager.getInstance();
        String query = "UPDATE users SET password = '" + this.password + "', is_temp_password = 0 WHERE user_id = " + this.id;
        db.runUpdate(query);    
    }


    public void setActive(boolean isActive) {
        this.isActive = isActive;
        try {
            updateActiveInDB();
        } catch (SQLException e) {
            // Handle the exception, e.g., log an error message
            System.err.println("Error updating active status: " + e.getMessage());
        }
    }

    private void updateActiveInDB() throws SQLException {
        DBManager db = DBManager.getInstance();
        String query = "UPDATE users SET is_active = " + (isActive ? 1 : 0) + " WHERE user_id = " + this.id;
        db.runUpdate(query);
    }


    // New method to load a user using raw SQL
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

class Admin extends User {
    private static final Map<String, Boolean> PERMISSIONS;
    static {
        PERMISSIONS = new HashMap<>();
        PERMISSIONS.put("MANAGE_USERS", true);
        PERMISSIONS.put("RESET_PASSWORDS", true);
        PERMISSIONS.put("VIEW_DEALERSHIP_INFO", true);
        PERMISSIONS.put("VIEW_SALES_HISTORY", true);
    }

    public Admin(int id, String username, String password, String name, String email, String phone, boolean isTempPassword, boolean isActive) {
        super(id, username, password, name, email, phone, "Admin", isActive, LocalDate.now().toString(), isTempPassword);
    }

    @Override
    public String getPermissions() {
        StringBuilder permissions = new StringBuilder();
        for (String action : PERMISSIONS.keySet()) {
            permissions.append(action).append(", ");
        }
        return permissions.toString().trim().replaceAll(", $", "");
    }

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
        String query = "INSERT INTO users (username, password, role_id, name, email, phone, is_temp_password) VALUES ('" +
                       username + "', '" + password + "', " + role_id + ", '" + name + "', '" + 
                       email + "', '" + phone + "', 1)";
        db.runInsert(query);
    }

    public boolean resetPassword(User user, String newPassword) throws Exception {
        if (user != null && user.isActive) {
            user.setPassword(newPassword);
            return true;
        }
        return false;
    }
}

class Manager extends User {
    private static final Map<String, Boolean> PERMISSIONS;
    static {
        PERMISSIONS = new HashMap<>();
        PERMISSIONS.put("ADD_VEHICLE", true);
        PERMISSIONS.put("EDIT_VEHICLE", true);
        PERMISSIONS.put("REMOVE_VEHICLE", true);
        PERMISSIONS.put("SELL_VEHICLE", true);
        PERMISSIONS.put("SEARCH_VEHICLES", true);
        PERMISSIONS.put("VIEW_SALES_HISTORY", true);
        PERMISSIONS.put("VIEW_DEALERSHIP_INFO", true);
    }

    public Manager(int id, String username, String password, String name, String email, String phone, boolean isTempPassword, boolean isActive) {
        super(id, username, password, name, email, phone, "Manager", isActive, LocalDate.now().toString(), isTempPassword);
    }

    @Override
    public String getPermissions() {
        StringBuilder permissions = new StringBuilder();
        for (String action : PERMISSIONS.keySet()) {
            permissions.append(action).append(", ");
        }
        return permissions.toString().trim().replaceAll(", $", "");
    }
}

class Salesperson extends User {
    private static final Map<String, Boolean> PERMISSIONS;
    static {
        PERMISSIONS = new HashMap<>();
        PERMISSIONS.put("SELL_VEHICLE", true);
        PERMISSIONS.put("SEARCH_VEHICLES", true);
        PERMISSIONS.put("VIEW_SALES_HISTORY", true);
    }

    public Salesperson(int id, String username, String password, String name, String email, String phone, boolean isTempPassword, boolean isActive) {
        super(id, username, password, name, email, phone, "Salesperson", isActive, LocalDate.now().toString() , isTempPassword);
    }

    public String getPermissions() {
        StringBuilder permissions = new StringBuilder();
        for (String action : PERMISSIONS.keySet()) {
            permissions.append(action).append(", ");
        }
        return permissions.toString().trim().replaceAll(", $", "");
    }
}