package carDealership;

import persistance.DBManager;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDate;


public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;
    private Dealership dealership;

    public LoginFrame(Dealership dealership) {
        this.dealership = dealership;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Dealership System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 650, 400);
        setLayout(null);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(200, 100, 100, 25);
        add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(300, 100, 150, 25);
        add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(200, 150, 100, 25);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(300, 150, 150, 25);
        add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(300, 200, 100, 30);
        add(loginButton);

        statusLabel = new JLabel("");
        statusLabel.setBounds(200, 250, 250, 25);
        add(statusLabel);

        loginButton.addActionListener(e -> authenticateUser());

        setLocationRelativeTo(null);
        setVisible(true);
    }


    public static User loadUser(String username) throws SQLException, Exception {
    DBManager db = DBManager.getInstance();
    ResultSet rs = db.runQuery("SELECT u.*, r.role_name FROM users u JOIN roles r " +
                               "ON u.role_id = r.role_id WHERE u.username = '" + username + "'");
    if (rs.next()) {
        String role = rs.getString("role_name");
        String password = rs.getString("password");
        boolean isTempPassword = rs.getInt("is_temp_password") == 1;
        int rawIsActive = rs.getInt("is_active"); // Get raw value
        boolean isActive = rawIsActive == 1;
        System.out.println("Raw is_active from DB for " + username + ": " + rawIsActive);
        System.out.println("Computed isActive: " + isActive);

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

    private void forcePasswordChange(User user) {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        JLabel newPassLabel = new JLabel("New Password:");
        JPasswordField newPassField = new JPasswordField();
        JLabel confirmPassLabel = new JLabel("Confirm Password:");
        JPasswordField confirmPassField = new JPasswordField();

        panel.add(newPassLabel);
        panel.add(newPassField);
        panel.add(confirmPassLabel);
        panel.add(confirmPassField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Change Temporary Password", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String newPassword = new String(newPassField.getPassword());
            String confirmPassword = new String(confirmPassField.getPassword());

            if (newPassword.isEmpty()) {
                statusLabel.setText("Password cannot be empty.");
                statusLabel.setForeground(Color.RED);
                forcePasswordChange(user); // Re-prompt
            } else if (!newPassword.equals(confirmPassword)) {
                statusLabel.setText("Passwords do not match.");
                statusLabel.setForeground(Color.RED);
                forcePasswordChange(user); // Re-prompt
            } else {
                try {
                    user.setPassword(newPassword); // Updates DB and clears is_temp_password
                    statusLabel.setText("Password changed successfully! Please log in again.");
                    statusLabel.setForeground(Color.GREEN);
                    // Force re-login
                    usernameField.setText("");
                    passwordField.setText("");
                } catch (SQLException ex) {
                    statusLabel.setText("Error updating password: " + ex.getMessage());
                    statusLabel.setForeground(Color.RED);
                } catch (Exception ex) {
                    statusLabel.setText("An error occured: " + ex.getMessage());
                    statusLabel.setForeground(Color.RED);
                }
            }
        } else {
            statusLabel.setText("Password change required to proceed.");
            statusLabel.setForeground(Color.RED);
        }
    }

    private void authenticateUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        // Add your login authentication logic here
        try {
            User user = loadUser(username);
            if (user != null && user.checkPassword(password) && user.isActive()) {
                // Login successful, check if user needs to change password
                if (user.isTempPassword()) {
                    forcePasswordChange(user);
                } else {
                    // Login successful, no password change needed
                    loginSuccessful(user);
                }
            } else {
                // Login failed, display error message
                if (user != null && !user.isActive()) {
                    System.out.println("Your account is inactive. Please contact an administrator.");
                } else {
                    System.out.println("Login failed");
                }
            }
        } catch (SQLException e) {
            // Handle SQLException
            System.out.println("A SQL error occurred: " + e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
    private void loginSuccessful(User user) {
        dispose(); // Close the login frame
        openDashboard(user);
    }
    
    private void openDashboard(User user) {
        dispose();
        SwingUtilities.invokeLater(() -> {
            if (user instanceof Admin) {
                new AdminDashboard(user, dealership).setVisible(true);
            } else if (user instanceof Manager) {
                new ManagerDashboard(user, dealership).setVisible(true);
            } else if (user instanceof Salesperson) {
                new SalespersonDashboard(user, dealership).setVisible(true);
            }
        });
    }

    // Dashboard views by Role
    class AdminDashboard extends JFrame implements ActionListener {
        private Dealership dealership;
        private User user;
        private JButton displayAllButton, addVehicleButton, sellVehicleButton, removeVehicleButton,
                editVehicleButton, salesHistoryButton, searchCarButton, dealershipInfoButton,
                createProfileButton, employeeListButton, passwordManagementButton;
        private JTextArea textArea;
        private JScrollPane scrollPane;
        private JMenuBar menuBar;
        private JMenu fileMenu;
        private JMenuItem saveItem, deleteDealershipItem;
    
        public AdminDashboard(User user, Dealership dealership) {
            this.user = user;
            this.dealership = dealership;
            setTitle("Admin Dashboard - " + dealership.getName());
            setSize(1200, 700);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(null);
            getContentPane().setBackground(Color.decode("#ADD8E6"));
            initializeUI();
            setLocationRelativeTo(null);
        }
    
        private void initializeUI() {
            JLabel welcomeLabel = new JLabel("Welcome " + user.getName() + "!" + " - Admin");
            welcomeLabel.setBounds(20, 10, 300, 25);
            add(welcomeLabel);
    
            menuBar = new JMenuBar();
            fileMenu = new JMenu("File");
            saveItem = new JMenuItem("Save");
            deleteDealershipItem = new JMenuItem("Delete Dealership");
            fileMenu.add(saveItem);
            fileMenu.add(deleteDealershipItem);
            menuBar.add(fileMenu);
            setJMenuBar(menuBar);
    
            displayAllButton = new JButton("Display All");
            setButtonStyle(displayAllButton, "#F09EA7", 20, 40);
    
            addVehicleButton = new JButton("Add Vehicle");
            setButtonStyle(addVehicleButton, "#F6CA94", 180, 40);
    
            sellVehicleButton = new JButton("Sell Vehicle");
            setButtonStyle(sellVehicleButton, "#FAFABE", 340, 40);
            sellVehicleButton.setForeground(Color.GREEN);
    
            removeVehicleButton = new JButton("Remove Vehicle");
            setButtonStyle(removeVehicleButton, "#C1EBC0", 500, 40);
            removeVehicleButton.setForeground(Color.RED);
    
            editVehicleButton = new JButton("Edit Vehicle");
            setButtonStyle(editVehicleButton, "#C7CAFF", 660, 40);
    
            salesHistoryButton = new JButton("Sales History");
            setButtonStyle(salesHistoryButton, "#CDABEB", 820, 40);
    
            searchCarButton = new JButton("Search Car (Budget)");
            setButtonStyle(searchCarButton, "#F6C2F3", 980, 40);
    
            dealershipInfoButton = new JButton("Dealership Info");
            setButtonStyle(dealershipInfoButton, "#FFD700", 500, 120, 150, 50);
    
            createProfileButton = new JButton("Create New Profile");
            setButtonStyle(createProfileButton, "#F6CA94", 180, 120, 150, 50);
    
            employeeListButton = new JButton("Employee List");
            setButtonStyle(employeeListButton, "#C7CAFF", 340, 120, 150, 50);
    
            passwordManagementButton = new JButton("Password Management");
            setButtonStyle(passwordManagementButton, "#F6C2F3", 660, 120, 150, 50);
    
            displayAllButton.addActionListener(this);
            addVehicleButton.addActionListener(this);
            sellVehicleButton.addActionListener(this);
            removeVehicleButton.addActionListener(this);
            editVehicleButton.addActionListener(this);
            salesHistoryButton.addActionListener(this);
            searchCarButton.addActionListener(this);
            dealershipInfoButton.addActionListener(this);
            saveItem.addActionListener(this);
            deleteDealershipItem.addActionListener(this);
            createProfileButton.addActionListener(this);
            employeeListButton.addActionListener(this);
            passwordManagementButton.addActionListener(this);
    
            add(displayAllButton); add(addVehicleButton); add(sellVehicleButton); add(removeVehicleButton);
            add(editVehicleButton); add(salesHistoryButton); add(searchCarButton); add(dealershipInfoButton);
            add(createProfileButton); add(employeeListButton); add(passwordManagementButton);
        }
    
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (e.getSource() == displayAllButton) {
                    if (dealership.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Inventory is empty!");
                    } else {
                        textArea = new JTextArea(dealership.displayAlls());
                        textArea.setEditable(false);
                        scrollPane = new JScrollPane(textArea);
                        scrollPane.setPreferredSize(new Dimension(400, 300));
                        JOptionPane.showMessageDialog(this, scrollPane, "Inventory", JOptionPane.PLAIN_MESSAGE);
                    }
                } else if (e.getSource() == addVehicleButton) {
                    SwingUtilities.invokeLater(() -> {
                        if (!dealership.isFull()) {
                            VehicleMenu vehicleMenu = new VehicleMenu();
                            vehicleMenu.setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(this, "Sorry, your inventory is full!");
                        }
                    });
                } else if (e.getSource() == sellVehicleButton) {
                    String idString = JOptionPane.showInputDialog(this, "Enter the id of the vehicle:");
                    if (idString == null) return;
                    int id = Integer.parseInt(idString);
                    if (dealership.getIndexFromId(id) == -1) {
                        JOptionPane.showMessageDialog(this, "Vehicle not found!");
                        return;
                    }
                    String buyerName = JOptionPane.showInputDialog(this, "Enter the buyer's name:");
                    String buyerContact = JOptionPane.showInputDialog(this, "Enter the buyer's contact:");
                    Vehicle vehicle = dealership.getVehicleFromId(id);
                    if (dealership.sellVehicle(vehicle, buyerName, buyerContact)) {
                        JOptionPane.showMessageDialog(this, "Vehicle sold successfully.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Couldn't sell vehicle.");
                    }
                } else if (e.getSource() == removeVehicleButton) {
                    String idString = JOptionPane.showInputDialog(this, "Enter the id of the vehicle:");
                    if (idString == null) return;
                    int id = Integer.parseInt(idString);
                    if (dealership.getIndexFromId(id) == -1) {
                        JOptionPane.showMessageDialog(this, "Vehicle not found!");
                    } else {
                        Vehicle vehicle = dealership.getVehicleFromId(id);
                        int confirm = JOptionPane.showConfirmDialog(this,
                                "Are you sure you want to delete this vehicle\nwith id: " + id, "Confirm Deletion",
                                JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            if (dealership.removeVehicle(vehicle)) {
                                JOptionPane.showMessageDialog(this, "Vehicle removed successfully.");
                            } else {
                                JOptionPane.showMessageDialog(this, "Couldn't remove vehicle.");
                            }
                        }
                    }
                } else if (e.getSource() == editVehicleButton) {
                    String idString = JOptionPane.showInputDialog(this, "Enter the id of the vehicle:");
                    if (idString == null) return;
                    int id = Integer.parseInt(idString);
                    if (dealership.getIndexFromId(id) == -1) {
                        JOptionPane.showMessageDialog(this, "Vehicle not found!");
                        return;
                    }
                    Vehicle vehicle = dealership.getVehicleFromId(id);
                    JTextField makeField = new JTextField();
                    JTextField modelField = new JTextField();
                    JTextField colorField = new JTextField();
                    JTextField yearField = new JTextField();
                    JTextField priceField = new JTextField();
                    JTextField typeField = new JTextField();
                    JTextField handlebarField = new JTextField();
                    JPanel editPanel = new JPanel(new GridLayout(0, 2));
    
                    if (vehicle instanceof Car) {
                        Car car = (Car) vehicle;
                        editPanel.add(new JLabel("Make:")); makeField.setText(car.getMake()); editPanel.add(makeField);
                        editPanel.add(new JLabel("Model:")); modelField.setText(car.getModel()); editPanel.add(modelField);
                        editPanel.add(new JLabel("Color:")); colorField.setText(car.getColor()); editPanel.add(colorField);
                        editPanel.add(new JLabel("Year:")); yearField.setText(String.valueOf(car.getYear())); editPanel.add(yearField);
                        editPanel.add(new JLabel("Price:")); priceField.setText(String.valueOf(car.getPrice())); editPanel.add(priceField);
                        editPanel.add(new JLabel("Type:")); typeField.setText(car.getType()); editPanel.add(typeField);
                    } else if (vehicle instanceof Motorcycle) {
                        Motorcycle motorcycle = (Motorcycle) vehicle;
                        editPanel.add(new JLabel("Make:")); makeField.setText(motorcycle.getMake()); editPanel.add(makeField);
                        editPanel.add(new JLabel("Model:")); modelField.setText(motorcycle.getModel()); editPanel.add(modelField);
                        editPanel.add(new JLabel("Color:")); colorField.setText(motorcycle.getColor()); editPanel.add(colorField);
                        editPanel.add(new JLabel("Year:")); yearField.setText(String.valueOf(motorcycle.getYear())); editPanel.add(yearField);
                        editPanel.add(new JLabel("Price:")); priceField.setText(String.valueOf(motorcycle.getPrice())); editPanel.add(priceField);
                        editPanel.add(new JLabel("Handlebar Type:")); handlebarField.setText(motorcycle.getHandlebarType()); editPanel.add(handlebarField);
                    }
                    int option = JOptionPane.showConfirmDialog(this, editPanel, "Edit Vehicle", JOptionPane.OK_CANCEL_OPTION);
                    if (option == JOptionPane.OK_OPTION) {
                        if (vehicle instanceof Car) {
                            Car car = (Car) vehicle;
                            car.setMake(makeField.getText());
                            car.setModel(modelField.getText());
                            car.setColor(colorField.getText());
                            car.setYear(Integer.parseInt(yearField.getText()));
                            car.setPrice(Double.parseDouble(priceField.getText()));
                            car.setType(typeField.getText());
                        } else if (vehicle instanceof Motorcycle) {
                            Motorcycle motorcycle = (Motorcycle) vehicle;
                            motorcycle.setMake(makeField.getText());
                            motorcycle.setModel(modelField.getText());
                            motorcycle.setColor(colorField.getText());
                            motorcycle.setYear(Integer.parseInt(yearField.getText()));
                            motorcycle.setPrice(Double.parseDouble(priceField.getText()));
                            motorcycle.setHandlebarType(handlebarField.getText());
                        }
                        JOptionPane.showMessageDialog(this, "Vehicle edited successfully.");
                    }
                } else if (e.getSource() == salesHistoryButton) {
                    textArea = new JTextArea(dealership.showSalesHistory());
                    textArea.setEditable(false);
                    scrollPane = new JScrollPane(textArea);
                    scrollPane.setPreferredSize(new Dimension(400, 300));
                    JOptionPane.showMessageDialog(this, scrollPane, "Sales History", JOptionPane.PLAIN_MESSAGE);
                } else if (e.getSource() == searchCarButton) {
                    String budgetText = JOptionPane.showInputDialog(this, "Enter Budget:");
                    if (budgetText == null) return;
                    double budget = Double.parseDouble(budgetText);
                    if (budget < 0) {
                        JOptionPane.showMessageDialog(this, "Invalid input. Please enter a positive number.");
                        return;
                    }
                    Car[] carsWithinBudget = dealership.carsWithinBudget(budget);
                    if (carsWithinBudget.length == 0) {
                        JOptionPane.showMessageDialog(this, "No cars found within the budget of " + budget + " SAR.");
                    } else {
                        StringBuilder message = new StringBuilder();
                        for (Car car : carsWithinBudget) {
                            if (car != null && car.getPrice() <= budget) {
                                message.append(car.toString()).append("\n--------------------\n");
                            }
                        }
                        if (message.length() == 0) {
                            JOptionPane.showMessageDialog(this, "No cars found within the budget of " + budget);
                        } else {
                            JTextArea resultArea = new JTextArea(message.toString());
                            scrollPane = new JScrollPane(resultArea);
                            scrollPane.setPreferredSize(new Dimension(400, 300));
                            JOptionPane.showMessageDialog(this, scrollPane, "Cars within Budget", JOptionPane.PLAIN_MESSAGE);
                        }
                    }
                } else if (e.getSource() == dealershipInfoButton) {
                    textArea = new JTextArea(dealership.getInfoGUI());
                    textArea.setEditable(false);
                    scrollPane = new JScrollPane(textArea);
                    JOptionPane.showMessageDialog(this, scrollPane, "All Information", JOptionPane.PLAIN_MESSAGE);
                } else if (e.getSource() == saveItem) {
                    File saveFile = new File("save.data");
                    try (FileOutputStream outFileStream = new FileOutputStream(saveFile);
                         ObjectOutputStream outObjStream = new ObjectOutputStream(outFileStream)) {
                        outObjStream.writeObject(dealership);
                        JOptionPane.showMessageDialog(this, "Dealership saved!");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "Error saving dealership: " + ex.getMessage());
                    }
                } else if (e.getSource() == deleteDealershipItem) {
                    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the dealership?",
                            "Confirmation", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        File saveFile = new File("save.data");
                        if (saveFile.exists()) saveFile.delete();
                        dispose();
                    }
                } else if (e.getSource() == createProfileButton) {
                    createNewProfile();
                } else if (e.getSource() == employeeListButton) {
                    showEmployeeList();
                } else if (e.getSource() == passwordManagementButton) {
                    managePasswords();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid number.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    
        private void createNewProfile() throws SQLException, Exception {
            if (!(user instanceof Admin)) {
                JOptionPane.showMessageDialog(this, "Only admins can create profiles!");
                return;
            }
            JPanel panel = new JPanel(new GridLayout(5, 2));
            JTextField nameField = new JTextField();
            JTextField emailField = new JTextField();
            JTextField phoneField = new JTextField();
            String[] roles = {"Admin", "Manager", "Salesperson"};
            JComboBox<String> roleCombo = new JComboBox<>(roles);
            JTextField usernameField = new JTextField();
    
            panel.add(new JLabel("Name:")); panel.add(nameField);
            panel.add(new JLabel("Email:")); panel.add(emailField);
            panel.add(new JLabel("Phone:")); panel.add(phoneField);
            panel.add(new JLabel("Role:")); panel.add(roleCombo);
            panel.add(new JLabel("Username:")); panel.add(usernameField);
    
            int result = JOptionPane.showConfirmDialog(this, panel, "Create New Profile", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String name = nameField.getText();
                String email = emailField.getText();
                String phone = phoneField.getText();
                String role = (String) roleCombo.getSelectedItem();
                String username = usernameField.getText();
    
                if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || username.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields must be filled!");
                    return;
                }
    
                if (User.loadUser(username) != null) {
                    JOptionPane.showMessageDialog(this, "Username already exists!");
                    return;
                }
    
                String tempPassword = "temp" + System.currentTimeMillis();
                ((Admin) user).createUser(role, username, tempPassword, name, email, phone);


                JOptionPane.showMessageDialog(this, 
                "New Employee Profile Created!\n\nUsername: " + username + 
                "\nTemporary Password: " + tempPassword + 
                "\n\nShare this manually with the employee.", 
                "Profile Created", JOptionPane.INFORMATION_MESSAGE);


                JOptionPane.showMessageDialog(this,
                        "Profile created for " + name + "\nCredentials sent to " + email + ":\nUsername: " + username + "\nPassword: " + tempPassword,
                        "New Profile Created", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    
        private void showEmployeeList() throws SQLException {
            StringBuilder employeeList = new StringBuilder("Employee List:\n\n");
            List<User> users = dealership.getUsers();
            if (users.isEmpty()) {
                employeeList.append("No employees found.");
            } else {
                Object[][] data = new Object[users.size()][7]; // Assuming you have 7 columns
        
                for (int i = 0; i < users.size(); i++) {
                    User u = users.get(i);
                    data[i][0] = u.getName();
                    data[i][1] = u.getEmail();
                    data[i][2] = u.getPhone();
                    data[i][3] = u.getRole();
                    data[i][4] = u.getUsername();
                    data[i][5] = u.isActive() ? "Yes" : "No";
                    data[i][6] = getPermissions(u);
        
                    employeeList.append("Name: ").append(u.getName())
                            .append("\nEmail: ").append(u.getEmail())
                            .append("\nPhone: ").append(u.getPhone())
                            .append("\nRole: ").append(u.getRole())
                            .append("\nUsername: ").append(u.getUsername())
                            .append("\nActive: ").append(u.isActive() ? "Yes" : "No")
                            .append("\nPermissions: ").append(getPermissions(u))
                            .append("\n------------------------------------\n");
                }
        
                String[] columnNames = {"Name", "Email", "Phone", "Role", "Username", "Active", "Permissions"};
        
                textArea = new JTextArea(employeeList.toString());
                textArea.setEditable(false);
                scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(400, 300));
        
                JTable table = new JTable(data, columnNames) {
                    @Override
                    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                        Component c = super.prepareRenderer(renderer, row, column);
                        if (!((String) getValueAt(row, 5)).equals("Yes")) { // Check "Active" column
                            c.setForeground(Color.GRAY);
                        } else {
                            c.setForeground(Color.BLACK);
                        }
                        return c;
                    }
                };
        
                table.setEnabled(false); // Make it read-only
                JScrollPane scrollPaneTable = new JScrollPane(table);
                scrollPaneTable.setPreferredSize(new Dimension(600, 300));
        
                JPanel optionsPanel = new JPanel(new GridLayout(2, 1));
                JButton toggleActiveButton = new JButton("Toggle Active Status");
                JButton editPermissionsButton = new JButton("Edit Permissions");
                optionsPanel.add(toggleActiveButton);
                optionsPanel.add(editPermissionsButton);
        
                toggleActiveButton.addActionListener(e -> {
                    try {
                        toggleActiveStatus(users);
                        showEmployeeList(); // Refresh the list
                    } catch (SQLException ex) {
                        // Handle SQLException specifically
                        JOptionPane.showMessageDialog(this, "SQL Error: " + ex.getMessage());
                    } catch (Exception ex) {
                        // Handle other exceptions
                        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                    }
                });

                editPermissionsButton.addActionListener(e -> editPermissions(users));
        
                JOptionPane.showOptionDialog(this, new Object[]{scrollPane, optionsPanel}, "Employee List",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{"OK"}, "OK");
            }
        }
    
        private void toggleActiveStatus(List<User> users) throws SQLException, Exception {
            String username = JOptionPane.showInputDialog(this, "Enter username to toggle active status:");
            if (username == null) return;
            User targetUser = User.loadUser(username);
            if (targetUser != null) {
                targetUser.setActive(!targetUser.isActive()); // Toggle the active status
                
                dealership.updateUser(targetUser);
                JOptionPane.showMessageDialog(this, "User " + username + " is now " + (targetUser.isActive() ? "active" : "inactive"));
            } else {
                JOptionPane.showMessageDialog(this, "User not found!");
            }
        }
    
        private void editPermissions(List<User> users) {
            String username = JOptionPane.showInputDialog(this, "Enter username to edit permissions:");
            if (username == null) return;
            User targetUser = null;
            try {
                targetUser = User.loadUser(username);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading user: " + ex.getMessage());
                return;
            }
            if (targetUser != null) {
                if (!targetUser.isActive()) {
                    JOptionPane.showMessageDialog(this, "Cannot edit permissions for an inactive user!");
                    return;
                }
                JPanel panel = new JPanel(new GridLayout(0, 1));
                JCheckBox[] permissions = {
                    new JCheckBox("Display All"), new JCheckBox("Add Vehicle"), new JCheckBox("Sell Vehicle"),
                    new JCheckBox("Remove Vehicle"), new JCheckBox("Edit Vehicle"), new JCheckBox("Sales History"),
                    new JCheckBox("Dealership Info"), new JCheckBox("Manage Users"), new JCheckBox("Reset Passwords")
                };
                for (JCheckBox cb : permissions) panel.add(cb);
        
                String currentPermissions = getPermissions(targetUser);
                for (JCheckBox cb : permissions) {
                    cb.setSelected(currentPermissions.contains(cb.getText()));
                }
        
                int result = JOptionPane.showConfirmDialog(this, panel, "Edit Permissions for " + username, JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    StringBuilder newPermissions = new StringBuilder();
                    for (JCheckBox cb : permissions) {
                        if (cb.isSelected()) newPermissions.append(cb.getText()).append(", ");
                    }
                    JOptionPane.showMessageDialog(this, "Permissions updated for " + username + ": " + newPermissions.toString());
                    // Note: Permissions not persisted to DB yet; consider adding a permissions table
                }
            } else {
                JOptionPane.showMessageDialog(this, "User not found!");
            }
        }
    
        private void managePasswords() throws SQLException, Exception {
            if (!(user instanceof Admin)) {
                JOptionPane.showMessageDialog(this, "Only admins can manage passwords!");
                return;
            }
            List<User> passwordResetRequests = dealership.getPasswordResetRequests();
            if (passwordResetRequests.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No password reset requests pending.");
                return;
            }
    
            StringBuilder requests = new StringBuilder("Password Reset Requests:\n\n");
            for (User u : passwordResetRequests) {
                requests.append("Request by ").append(u.getName()).append(" (").append(u.getUsername())
                        .append(") at ").append(LocalDate.now()).append("\n");
            }
    
            textArea = new JTextArea(requests.toString());
            textArea.setEditable(false);
            scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));
    
            String username = JOptionPane.showInputDialog(this, new Object[]{scrollPane}, "Enter username to reset password:", JOptionPane.PLAIN_MESSAGE);
            if (username != null) {
                User targetUser = User.loadUser(username);
                if (targetUser != null && passwordResetRequests.stream().anyMatch(u -> u.getUsername().equals(username))) {
                    int confirm = JOptionPane.showConfirmDialog(this, "Reset password for " + targetUser.getName() + "?\n(An email will be sent.)",
                            "Confirm Reset", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        String newPassword = "reset" + System.currentTimeMillis();
                        ((Admin) user).resetPassword(targetUser, newPassword);
                        JOptionPane.showMessageDialog(this, "Password reset for " + username + ". New password: " + newPassword + "\nEmail sent to " + targetUser.getEmail());
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No reset request found for " + username);
                }
            }
        }
    
        private String getPermissions(User user) {
            if (!user.isActive()) {
                return "None (Inactive)";
            }
            StringBuilder permissions = new StringBuilder();
            if (user instanceof Admin) {
                permissions.append("Display All, Add Vehicle, Sell Vehicle, Remove Vehicle, Edit Vehicle, Sales History, Dealership Info, Manage Users, Reset Passwords");
            } else if (user instanceof Manager) {
                permissions.append("Display All, Add Vehicle, Sell Vehicle, Remove Vehicle, Edit Vehicle, Sales History, Dealership Info");
            } else if (user instanceof Salesperson) {
                permissions.append("Sell Vehicle, Search Car (Budget), Sales History");
            }
            return permissions.toString();
        }
    
        private void setButtonStyle(JButton button, String color, int x, int y) {
            setButtonStyle(button, color, x, y, 150, 70);
        }
    
        private void setButtonStyle(JButton button, String color, int x, int y, int width, int height) {
            button.setBackground(Color.decode(color));
            button.setForeground(Color.BLACK);
            button.setBounds(x, y, width, height);
            button.setOpaque(true);
            button.setBorderPainted(false);
        }
    }

    class ManagerDashboard extends JFrame implements ActionListener {
        private Dealership dealership;
        private User user;
        private JButton displayAllButton, addVehicleButton, sellVehicleButton, removeVehicleButton,
                editVehicleButton, salesHistoryButton, searchCarButton, dealershipInfoButton;
        private JTextArea textArea;
        private JScrollPane scrollPane;

        public ManagerDashboard(User user, Dealership dealership) {
            this.user = user;
            this.dealership = dealership;
            setTitle("Manager Dashboard - " + dealership.getName());
            setSize(1200, 700); // Same size as AdminDashboard
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(null);
            getContentPane().setBackground(Color.decode("#ADD8E6")); // Same background as AdminDashboard
            initializeUI();
            setLocationRelativeTo(null);
        }

        private void initializeUI() {
            // Welcome label
            JLabel welcomeLabel = new JLabel("Welcome " + user.getName() + "!" + " - Manager");
            welcomeLabel.setBounds(20, 10, 300, 25);
            add(welcomeLabel);

            // Buttons (styled like Frame and AdminDashboard)
            displayAllButton = new JButton("Display All");
            displayAllButton.setBackground(Color.decode("#F09EA7"));
            displayAllButton.setForeground(Color.BLACK); // Default text color
            displayAllButton.setBounds(20, 40, 150, 70);
            displayAllButton.setOpaque(true); // Ensure background color shows
            displayAllButton.setBorderPainted(false); // Optional: cleaner look

            addVehicleButton = new JButton("Add Vehicle");
            addVehicleButton.setBackground(Color.decode("#F6CA94"));
            addVehicleButton.setForeground(Color.BLACK);
            addVehicleButton.setBounds(180, 40, 150, 70);
            addVehicleButton.setOpaque(true);
            addVehicleButton.setBorderPainted(false);

            sellVehicleButton = new JButton("Sell Vehicle");
            sellVehicleButton.setBackground(Color.decode("#FAFABE"));
            sellVehicleButton.setForeground(Color.GREEN); // Green text
            sellVehicleButton.setBounds(340, 40, 150, 70);
            sellVehicleButton.setOpaque(true);
            sellVehicleButton.setBorderPainted(false);

            removeVehicleButton = new JButton("Remove Vehicle");
            removeVehicleButton.setBackground(Color.decode("#C1EBC0"));
            removeVehicleButton.setForeground(Color.RED); // Red text
            removeVehicleButton.setBounds(500, 40, 150, 70);
            removeVehicleButton.setOpaque(true);
            removeVehicleButton.setBorderPainted(false);

            editVehicleButton = new JButton("Edit Vehicle");
            editVehicleButton.setBackground(Color.decode("#C7CAFF"));
            editVehicleButton.setForeground(Color.BLACK);
            editVehicleButton.setBounds(660, 40, 150, 70);
            editVehicleButton.setOpaque(true);
            editVehicleButton.setBorderPainted(false);

            salesHistoryButton = new JButton("Sales History");
            salesHistoryButton.setBackground(Color.decode("#CDABEB"));
            salesHistoryButton.setForeground(Color.BLACK);
            salesHistoryButton.setBounds(820, 40, 150, 70);
            salesHistoryButton.setOpaque(true);
            salesHistoryButton.setBorderPainted(false);

            searchCarButton = new JButton("Search Car (Budget)");
            searchCarButton.setBackground(Color.decode("#F6C2F3"));
            searchCarButton.setForeground(Color.BLACK);
            searchCarButton.setBounds(980, 40, 150, 70);
            searchCarButton.setOpaque(true);
            searchCarButton.setBorderPainted(false);

            dealershipInfoButton = new JButton("Dealership Info");
            dealershipInfoButton.setBackground(Color.decode("#FFD700"));
            dealershipInfoButton.setForeground(Color.BLACK);
            dealershipInfoButton.setBounds(500, 120, 150, 50);
            dealershipInfoButton.setOpaque(true);
            dealershipInfoButton.setBorderPainted(false);

            // Add action listeners
            displayAllButton.addActionListener(this);
            addVehicleButton.addActionListener(this);
            sellVehicleButton.addActionListener(this);
            removeVehicleButton.addActionListener(this);
            editVehicleButton.addActionListener(this);
            salesHistoryButton.addActionListener(this);
            searchCarButton.addActionListener(this);
            dealershipInfoButton.addActionListener(this);

            // Add buttons to frame
            add(displayAllButton); add(addVehicleButton); add(sellVehicleButton); add(removeVehicleButton);
            add(editVehicleButton); add(salesHistoryButton); add(searchCarButton); add(dealershipInfoButton);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == displayAllButton) {
                if (dealership.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Inventory is empty!");
                } else {
                    textArea = new JTextArea(dealership.displayAlls());
                    textArea.setEditable(false);
                    scrollPane = new JScrollPane(textArea);
                    scrollPane.setPreferredSize(new Dimension(400, 300));
                    JOptionPane.showMessageDialog(this, scrollPane, "Inventory", JOptionPane.PLAIN_MESSAGE);
                }
            } else if (e.getSource() == addVehicleButton) {
                SwingUtilities.invokeLater(() -> {
                    if (!dealership.isFull()) {
                        VehicleMenu vehicleMenu = new VehicleMenu();
                        vehicleMenu.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "Sorry, your inventory is full!");
                    }
                });
            } else if (e.getSource() == sellVehicleButton) {
                try {
                    String idString = JOptionPane.showInputDialog(this, "Enter the id of the vehicle:");
                    if (idString == null) return;
                    int id = Integer.parseInt(idString);
                    if (dealership.getIndexFromId(id) == -1) {
                        JOptionPane.showMessageDialog(this, "Vehicle not found!");
                        return;
                    }
                    String buyerName = JOptionPane.showInputDialog(this, "Enter the buyer's name:");
                    String buyerContact = JOptionPane.showInputDialog(this, "Enter the buyer's contact:");
                    Vehicle vehicle = dealership.getVehicleFromId(id);
                    if (dealership.sellVehicle(vehicle, buyerName, buyerContact)) {
                        JOptionPane.showMessageDialog(this, "Vehicle sold successfully.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Couldn't sell vehicle.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid integer.");
                }
            } else if (e.getSource() == removeVehicleButton) {
                try {
                    String idString = JOptionPane.showInputDialog(this, "Enter the id of the vehicle:");
                    if (idString == null) return;
                    int id = Integer.parseInt(idString);
                    if (dealership.getIndexFromId(id) == -1) {
                        JOptionPane.showMessageDialog(this, "Vehicle not found!");
                    } else {
                        Vehicle vehicle = dealership.getVehicleFromId(id);
                        int confirm = JOptionPane.showConfirmDialog(this,
                                "Are you sure you want to delete this vehicle\nwith id: " + id, "Confirm Deletion",
                                JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            if (dealership.removeVehicle(vehicle)) {
                                JOptionPane.showMessageDialog(this, "Vehicle removed successfully.");
                            } else {
                                JOptionPane.showMessageDialog(this, "Couldn't remove vehicle.");
                            }
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid integer.");
                }
            } else if (e.getSource() == editVehicleButton) {
                try {
                    String idString = JOptionPane.showInputDialog(this, "Enter the id of the vehicle:");
                    if (idString == null) return;
                    int id = Integer.parseInt(idString);
                    if (dealership.getIndexFromId(id) == -1) {
                        JOptionPane.showMessageDialog(this, "Vehicle not found!");
                        return;
                    }
                    Vehicle vehicle = dealership.getVehicleFromId(id);
                    JTextField makeField = new JTextField();
                    JTextField modelField = new JTextField();
                    JTextField colorField = new JTextField();
                    JTextField yearField = new JTextField();
                    JTextField priceField = new JTextField();
                    JTextField typeField = new JTextField();
                    JTextField handlebarField = new JTextField();
                    JPanel editPanel = new JPanel();
                    editPanel.setLayout(new GridLayout(0, 2));

                    if (vehicle instanceof Car) {
                        Car car = (Car) vehicle;
                        editPanel.add(new JLabel("Make:")); makeField.setText(car.getMake()); editPanel.add(makeField);
                        editPanel.add(new JLabel("Model:")); modelField.setText(car.getModel()); editPanel.add(modelField);
                        editPanel.add(new JLabel("Color:")); colorField.setText(car.getColor()); editPanel.add(colorField);
                        editPanel.add(new JLabel("Year:")); yearField.setText(String.valueOf(car.getYear())); editPanel.add(yearField);
                        editPanel.add(new JLabel("Price:")); priceField.setText(String.valueOf(car.getPrice())); editPanel.add(priceField);
                        editPanel.add(new JLabel("Type:")); typeField.setText(car.getType()); editPanel.add(typeField);
                    } else if (vehicle instanceof Motorcycle) {
                        Motorcycle motorcycle = (Motorcycle) vehicle;
                        editPanel.add(new JLabel("Make:")); makeField.setText(motorcycle.getMake()); editPanel.add(makeField);
                        editPanel.add(new JLabel("Model:")); modelField.setText(motorcycle.getModel()); editPanel.add(modelField);
                        editPanel.add(new JLabel("Color:")); colorField.setText(motorcycle.getColor()); editPanel.add(colorField);
                        editPanel.add(new JLabel("Year:")); yearField.setText(String.valueOf(motorcycle.getYear())); editPanel.add(yearField);
                        editPanel.add(new JLabel("Price:")); priceField.setText(String.valueOf(motorcycle.getPrice())); editPanel.add(priceField);
                        editPanel.add(new JLabel("Handlebar Type:")); handlebarField.setText(motorcycle.getHandlebarType()); editPanel.add(handlebarField);
                    }
                    int option = JOptionPane.showConfirmDialog(this, editPanel, "Edit Vehicle", JOptionPane.OK_CANCEL_OPTION);
                    if (option == JOptionPane.OK_OPTION) {
                        if (vehicle instanceof Car) {
                            Car car = (Car) vehicle;
                            car.setMake(makeField.getText());
                            car.setModel(modelField.getText());
                            car.setColor(colorField.getText());
                            car.setYear(Integer.parseInt(yearField.getText()));
                            car.setPrice(Double.parseDouble(priceField.getText()));
                            car.setType(typeField.getText());
                        } else if (vehicle instanceof Motorcycle) {
                            Motorcycle motorcycle = (Motorcycle) vehicle;
                            motorcycle.setMake(makeField.getText());
                            motorcycle.setModel(modelField.getText());
                            motorcycle.setColor(colorField.getText());
                            motorcycle.setYear(Integer.parseInt(yearField.getText()));
                            motorcycle.setPrice(Double.parseDouble(priceField.getText()));
                            motorcycle.setHandlebarType(handlebarField.getText());
                        }
                        JOptionPane.showMessageDialog(this, "Vehicle edited successfully.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input. Year and price must be numeric values.");
                }
            } else if (e.getSource() == salesHistoryButton) {
                textArea = new JTextArea(dealership.showSalesHistory());
                textArea.setEditable(false);
                scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(400, 300));
                JOptionPane.showMessageDialog(this, scrollPane, "Sales History", JOptionPane.PLAIN_MESSAGE);
            } else if (e.getSource() == searchCarButton) {
                String budgetText = JOptionPane.showInputDialog(this, "Enter Budget:");
                if (budgetText == null) return;
                try {
                    double budget = Double.parseDouble(budgetText);
                    if (budget < 0) {
                        JOptionPane.showMessageDialog(this, "Invalid input. Please enter a positive number.");
                        return;
                    }
                    Car[] carsWithinBudget = dealership.carsWithinBudget(budget);
                    if (carsWithinBudget.length == 0) {
                        JOptionPane.showMessageDialog(this, "No cars found within the budget of " + budget + " SAR.");
                    } else {
                        StringBuilder message = new StringBuilder();
                        for (Car car : carsWithinBudget) {
                            if (car != null && car.getPrice() <= budget) {
                                message.append(car.toString()).append("\n--------------------\n");
                            }
                        }
                        if (message.length() == 0) {
                            JOptionPane.showMessageDialog(this, "No cars found within the budget of " + budget);
                        } else {
                            JTextArea resultArea = new JTextArea(message.toString());
                            scrollPane = new JScrollPane(resultArea);
                            scrollPane.setPreferredSize(new Dimension(400, 300));
                            JOptionPane.showMessageDialog(this, scrollPane, "Cars within Budget", JOptionPane.PLAIN_MESSAGE);
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid number.");
                }
            } else if (e.getSource() == dealershipInfoButton) {
                textArea = new JTextArea(dealership.getInfoGUI());
                textArea.setEditable(false);
                scrollPane = new JScrollPane(textArea);
                JOptionPane.showMessageDialog(this, scrollPane, "All Information", JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    class SalespersonDashboard extends JFrame implements ActionListener {
        private Dealership dealership;
        private User user;
        private JButton sellVehicleButton, searchCarButton, salesHistoryButton;
        private JTextArea textArea;
        private JScrollPane scrollPane;
    
        public SalespersonDashboard(User user, Dealership dealership) {
            this.user = user;
            this.dealership = dealership;
            setTitle("Salesperson Dashboard - " + dealership.getName());
            setSize(1200, 700); // Same size as AdminDashboard and ManagerDashboard
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(null);
            getContentPane().setBackground(Color.decode("#ADD8E6")); // Same background color
            initializeUI();
            setLocationRelativeTo(null);
        }
    
        private void initializeUI() {
            // Welcome label
            JLabel welcomeLabel = new JLabel("Welcome " + user.getName() + "!" + " - Salesperson");
            welcomeLabel.setBounds(20, 10, 300, 25);
            add(welcomeLabel);
    
            // Buttons (styled like Frame and AdminDashboard, limited to salesperson features)
            sellVehicleButton = new JButton("Sell Vehicle");
            sellVehicleButton.setBackground(Color.decode("#FAFABE"));
            sellVehicleButton.setForeground(Color.GREEN); // Green text as requested
            sellVehicleButton.setBounds(340, 40, 150, 70); // Position matches AdminDashboard
            sellVehicleButton.setOpaque(true); // Ensure background color shows
            sellVehicleButton.setBorderPainted(false); // Cleaner look
    
            searchCarButton = new JButton("Search Car (Budget)");
            searchCarButton.setBackground(Color.decode("#F6C2F3"));
            searchCarButton.setForeground(Color.BLACK);
            searchCarButton.setBounds(500, 40, 150, 70); // Adjusted position for fewer buttons
            searchCarButton.setOpaque(true);
            searchCarButton.setBorderPainted(false);
    
            salesHistoryButton = new JButton("Sales History");
            salesHistoryButton.setBackground(Color.decode("#CDABEB"));
            salesHistoryButton.setForeground(Color.BLACK);
            salesHistoryButton.setBounds(660, 40, 150, 70); // Adjusted position
            salesHistoryButton.setOpaque(true);
            salesHistoryButton.setBorderPainted(false);
    
            // Add action listeners
            sellVehicleButton.addActionListener(this);
            searchCarButton.addActionListener(this);
            salesHistoryButton.addActionListener(this);
    
            // Add buttons to frame
            add(sellVehicleButton);
            add(searchCarButton);
            add(salesHistoryButton);
        }
    
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == sellVehicleButton) {
                try {
                    String idString = JOptionPane.showInputDialog(this, "Enter the id of the vehicle:");
                    if (idString == null) return;
                    int id = Integer.parseInt(idString);
                    if (dealership.getIndexFromId(id) == -1) {
                        JOptionPane.showMessageDialog(this, "Vehicle not found!");
                        return;
                    }
                    String buyerName = JOptionPane.showInputDialog(this, "Enter the buyer's name:");
                    String buyerContact = JOptionPane.showInputDialog(this, "Enter the buyer's contact:");
                    Vehicle vehicle = dealership.getVehicleFromId(id);
                    if (dealership.sellVehicle(vehicle, buyerName, buyerContact)) {
                        JOptionPane.showMessageDialog(this, "Vehicle sold successfully.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Couldn't sell vehicle.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid integer.");
                }
            } else if (e.getSource() == searchCarButton) {
                String budgetText = JOptionPane.showInputDialog(this, "Enter Budget:");
                if (budgetText == null) return;
                try {
                    double budget = Double.parseDouble(budgetText);
                    if (budget < 0) {
                        JOptionPane.showMessageDialog(this, "Invalid input. Please enter a positive number.");
                        return;
                    }
                    Car[] carsWithinBudget = dealership.carsWithinBudget(budget);
                    if (carsWithinBudget.length == 0) {
                        JOptionPane.showMessageDialog(this, "No cars found within the budget of " + budget + " SAR.");
                    } else {
                        StringBuilder message = new StringBuilder();
                        for (Car car : carsWithinBudget) {
                            if (car != null && car.getPrice() <= budget) {
                                message.append(car.toString()).append("\n--------------------\n");
                            }
                        }
                        if (message.length() == 0) {
                            JOptionPane.showMessageDialog(this, "No cars found within the budget of " + budget);
                        } else {
                            JTextArea resultArea = new JTextArea(message.toString());
                            scrollPane = new JScrollPane(resultArea);
                            scrollPane.setPreferredSize(new Dimension(400, 300));
                            JOptionPane.showMessageDialog(this, scrollPane, "Cars within Budget", JOptionPane.PLAIN_MESSAGE);
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid number.");
                }
            } else if (e.getSource() == salesHistoryButton) {
                textArea = new JTextArea(dealership.showSalesHistory());
                textArea.setEditable(false);
                scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(400, 300));
                JOptionPane.showMessageDialog(this, scrollPane, "Sales History", JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

}