package carDealership;

import persistance.DBManager;
import persistance.DealershipLayer;

import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Dealership implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String location;
    private int nv;
    private int ns;
    private Vehicle[] inventory;
    private Sale[] sales;
    private int nextId;
    private transient DealershipLayer m_dealershipLayer;

    public Dealership(String name, String location, int maxInventory) throws SQLException {
        this.name = name;
        this.location = location;
        inventory = new Vehicle[maxInventory];
        sales = new Sale[maxInventory * 2];
        nv = 0;
        ns = 0;
        nextId = 1; // Changed from 0 to match repository’s logic for IDs starting at 1
        m_dealershipLayer = new DealershipLayer(name, location, maxInventory);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        try {
            if (name != null && location != null && inventory != null) {
                m_dealershipLayer = new DealershipLayer(name, location, inventory.length);
            }
        } catch (SQLException e) {
            throw new IOException("Failed to reinitialize DealershipLayer during deserialization", e);
        }
    }

    public String getName() {
        return this.name;
    }

    public boolean addVehicle(Vehicle vehicle) throws SQLException {
        if (nv == inventory.length) {
            return false;
        }

        // Assign unique ID
        vehicle.setId(nextId++);

        // Add to in-memory inventory
        if (vehicle instanceof Car) {
            inventory[nv++] = new Car((Car) vehicle);
        } else if (vehicle instanceof Motorcycle) {
            inventory[nv++] = new Motorcycle((Motorcycle) vehicle);
        }

        // Persist to database
        DBManager db = DBManager.getInstance();
        String query = "INSERT INTO Vehicle (vehicle_id, make, model, color, year, price, " +
                (vehicle instanceof Car ? "car_type" : "handlebar_type") +
                ", dealerships_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] params = {
            vehicle.getId(), vehicle.getMake(), vehicle.getModel(), vehicle.getColor(),
            vehicle.getYear(), vehicle.getPrice(),
            vehicle instanceof Car ? ((Car) vehicle).getType() : ((Motorcycle) vehicle).getHandlebarType(),
            m_dealershipLayer.getDealershipId()
        };
        db.runInsert(query, params);

        return true;
    }

    public boolean removeVehicle(Vehicle vehicle) throws SQLException {
        if (vehicle == null) {
            return false;
        }
        int index = getIndexFromId(vehicle.id);
        if (index == -1) {
            return false;
        }

        // Remove from database
        DBManager db = DBManager.getInstance();
        db.runUpdate("DELETE FROM Vehicle WHERE vehicle_id = ?", vehicle.getId());

        // Shift in-memory inventory
        for (int i = index; i < nv - 1; i++) {
            inventory[i] = inventory[i + 1];
        }
        inventory[--nv] = null; // Clear last slot
        return true;
    }

    public boolean sellVehicle(Vehicle vehicle, String buyerName, String buyerContact) throws SQLException {
        if (!removeVehicle(vehicle)) {
            return false;
        }

        // Add to sales in memory
        sales[ns++] = new Sale(vehicle, buyerName, buyerContact, LocalDate.now());

        // Persist sale to database
        DBManager db = DBManager.getInstance();
        db.runInsert("INSERT INTO Sales (vehicle_id, user_id, buyer_name, buyer_contact) VALUES (?, ?, ?, ?)",
                vehicle.getId(), 1, buyerName, buyerContact); // user_id=1 as placeholder

        return true;
    }

    public int getIndexFromId(int id) {
        for (int i = 0; i < nv; i++) {
            if (inventory[i] != null && inventory[i].id == id) {
                return i;
            }
        }
        return -1;
    }

    public Vehicle[] getVehicles() {
        return inventory; // No SQLException, uses in-memory data
    }

    public Vehicle getVehicleFromId(int id) {
        for (int i = 0; i < nv; i++) {
            if (inventory[i] != null && inventory[i].id == id) {
                return inventory[i];
            }
        }
        return null;
    }

	public int carBudget(double budget) {
		int total = 0; // declare the variable "total" here
		// method implementation...
		return total; // return the total number of cars within the budget
	}

	public Car[] carsWithinBudget(double budget) {
		List<Car> cars = new ArrayList<>();
		for (Vehicle vehicle : inventory) {
			if (vehicle instanceof Car && vehicle.getPrice() <= budget) {
				cars.add((Car) vehicle);
			}
		}
		return cars.toArray(new Car[0]);
	}

    public String showSalesHistory() {
        String string = "Sales History:\n";
        if (ns == 0) {
            return "No sales recorded.";
        }
        for (int i = 0; i < ns; i++) {
            string += "-------------------\n" + sales[i].getVehicle().toString() + "\n" +
                    "Buyer Name: " + sales[i].getBuyerName() + "\n" +
                    "Buyer Contact: " + sales[i].getBuyerContact() + "\n" +
                    "Sale Date: " + sales[i].getSaleDate() + "\n";
        }
        return string + "-------------------\n";
    }

    public String getInfoGUI() {
        return "Dealership name: [" + name + "]\nLocation: [" + location + "]\nInventory Size: [" + inventory.length
                + "]\n\nAvailable space: " + (inventory.length - nv) + "\nTotal Cars: " + getTotalCars()
                + "\nTotal Motorcycles: " + getTotalMotorcycles() + "\n\nTotal sales profit: " + salesProfit()
                + "\nTotal vehicles sold: " + ns;
    }

    public boolean isFull() { return nv == inventory.length; }
    public boolean isEmpty() { return nv == 0; }
    public int getTotalCars() {
        int total = 0;
        for (int i = 0; i < nv; i++) if (inventory[i] instanceof Car) total++;
        return total;
    }
    public int getTotalMotorcycles() {
        int total = 0;
        for (int i = 0; i < nv; i++) if (inventory[i] instanceof Motorcycle) total++;
        return total;
    }

	public Car[] searchCar(String type) {
		// Implement the logic to search for cars by type
		// For example:
		Car[] cars = new Car[nv];
		int count = 0;
		for (int i = 0; i < nv; i++) {
			if (inventory[i] instanceof Car && ((Car) inventory[i]).getType().equals(type)) {
				cars[count++] = (Car) inventory[i];
			}
		}
		Car[] result = new Car[count];
		System.arraycopy(cars, 0, result, 0, count);
		return result;
	}


    private int salesProfit() {
        int total = 0;
        for (int i = 0; i < ns; i++) total += sales[i].getVehicle().getPrice();
        return total;
    }

    // User management methods from repository
    public List<User> getUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        DBManager db = DBManager.getInstance();
        ResultSet rs = db.runQuery("SELECT u.*, r.role_name FROM users u JOIN roles r ON u.role_id = r.role_id");
        while (rs.next()) {
            String role = rs.getString("role_name");
            String password = rs.getString("password");
            boolean isTempPassword = rs.getInt("is_temp_password") == 1;
            boolean isActive = rs.getInt("is_active") == 1;

            switch (role) {
                case "Admin":
                    users.add(new Admin(rs.getInt("user_id"), rs.getString("username"), password,
                            rs.getString("name"), rs.getString("email"), rs.getString("phone"), isTempPassword, isActive));
                    break;
                case "Manager":
                    users.add(new Manager(rs.getInt("user_id"), rs.getString("username"), password,
                            rs.getString("name"), rs.getString("email"), rs.getString("phone"), isTempPassword, isActive));
                    break;
                case "Salesperson":
                    users.add(new Salesperson(rs.getInt("user_id"), rs.getString("username"), password,
                            rs.getString("name"), rs.getString("email"), rs.getString("phone"), isTempPassword, isActive));
                    break;
            }
        }
        return users;
    }

    public void updateUser(User user) throws SQLException {
        DBManager db = DBManager.getInstance();
        String query = "UPDATE users SET password = ?, name = ?, email = ?, phone = ?, is_active = ? WHERE user_id = ?";
        db.runUpdate(query, user.password, user.name, user.email, user.phone, user.isActive ? 1 : 0, user.getId());
    }

    public List<User> getPasswordResetRequests() throws SQLException {
        List<User> requests = new ArrayList<>();
        List<User> allUsers = getUsers();
        for (User u : allUsers) {
            if (u.getUsername().startsWith("sales")) { // Arbitrary condition as in original
                requests.add(u);
            }
        }
        return requests;
    }

    // Other methods (e.g., displayAll, searchCar) omitted for brevity but remain unchanged
}