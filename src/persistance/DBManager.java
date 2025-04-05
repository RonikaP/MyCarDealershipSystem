package persistance;

import java.io.*;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Database manager for SQLite operations
 *
 * @author [Your Name] [Your ID]
 * @author [Team Member Name] [Team Member ID]
 * @since 1.8
 */
public class DBManager {

	private static DBManager m_dbManager;

	private String m_dbPath;
	private Connection m_connection;

	/**
	 * Private constructor for the DBManager class
	 * Creates a database connection and initializes the database if needed
	 *
	 * @throws SQLException if a database access error occurs
	 */
	private DBManager() throws SQLException {
		var fileSystem = FileSystems.getDefault();
		m_dbPath = fileSystem.getPath(System.getProperty("user.home"), "dealership.sqlite3").toString();

		initDB();
	}

	/**
	 * Execute an SQL insert statement with the provided parameters
	 *
	 * @param query - the SQL insert statement to execute
	 * @param params - variable number of parameters to replace placeholders in the query
	 * @throws SQLException if a database access error occurs
	 */
	public void runInsert(String query, Object... params) throws SQLException {
		var stmt = m_connection.prepareStatement(query);
		for (int i = 0; i < params.length; i++) {
			stmt.setObject(i + 1, params[i]);
		}
		stmt.execute();
		m_connection.commit();
	}

	/**
	 * Execute an SQL query statement and return the result set
	 *
	 * @param query - the SQL query statement to execute
	 * @param params - variable number of parameters to replace placeholders in the query
	 * @return the ResultSet containing the query results
	 * @throws SQLException if a database access error occurs
	 */
	public ResultSet runQuery(String query, Object... params) throws SQLException {
		System.out.println("Will run query: " + query);
		var stmt = m_connection.prepareStatement(query);
		for (int i = 0; i < params.length; i++) {
			stmt.setObject(i + 1, params[i]);
		}
		return stmt.executeQuery();
	}

	/**
	 * Execute an SQL update statement with the provided parameters
	 *
	 * @param query - the SQL update statement to execute
	 * @param params - variable number of parameters to replace placeholders in the query
	 * @throws SQLException if a database access error occurs
	 */
	public void runUpdate(String query, Object... params) throws SQLException {
		System.out.println("Will run update query: " + query);
		var stmt = m_connection.prepareStatement(query);
		for (int i = 0; i < params.length; i++) {
			stmt.setObject(i + 1, params[i]);
		}
		stmt.execute();
		m_connection.commit();
	}

	/**
	 * Initialize the database connection and create tables if the database doesn't exist
	 *
	 * @throws SQLException if a database access error occurs
	 */
	private void initDB() throws SQLException {
		var dbFile = new File(m_dbPath);
		var mustCreateTables = !dbFile.exists();

		var url = "jdbc:sqlite:" + m_dbPath;
		try {
			m_connection = DriverManager.getConnection(url);
			System.out.println("Connection to SQLite has been established.");
			m_connection.setAutoCommit(false);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		if (!mustCreateTables) {
			System.out.println("DB file " + m_dbPath + " already exists. Not creating the database.");
		} else {
			System.out.println("Creating the DB file " + m_dbPath + " and the tables.");
			createTables();
		}
	}

	/**
	 * Create the database tables structure
	 * Sets up dealerships, users, roles, vehicles, and sales tables
	 *
	 * @throws SQLException if a database access error occurs
	 */
	private void createTables() throws SQLException {
		System.out.println("Creating the dealerships table");
		var dealershipSQL = "CREATE TABLE IF NOT EXISTS dealerships (id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ " name text NOT NULL, location text NOT NULL, capacity INTEGER);";

		var stmt = m_connection.createStatement();
		stmt.execute(dealershipSQL);


		// Modified users table schema
		var userSQL = "CREATE TABLE IF NOT EXISTS users (user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
              "username TEXT UNIQUE NOT NULL, password TEXT NOT NULL, role_id INTEGER, " +
              "name TEXT NOT NULL, email TEXT, phone TEXT, is_active BOOLEAN DEFAULT TRUE, " +
              "join_date DATE DEFAULT CURRENT_DATE, FOREIGN KEY (role_id) REFERENCES roles(role_id));";

		stmt.execute(userSQL);

		// Modified roles table schema
		var roleSQL = "CREATE TABLE IF NOT EXISTS roles (role_id INTEGER PRIMARY KEY AUTOINCREMENT,"
		+ " role_name text NOT NULL);";

		stmt.execute(roleSQL);

		var addAdminRoleSQL = "INSERT INTO roles (role_name) VALUES ('Admin');";
		stmt.execute(addAdminRoleSQL);

		var addManagerRoleSQL = "INSERT INTO roles (role_name) VALUES ('Manager');";
		stmt.execute(addManagerRoleSQL);
		
		var addSalesPersonRoleSQL = "INSERT INTO roles (role_name) VALUES ('Salesperson');";
		stmt.execute(addSalesPersonRoleSQL);


		// Added Vehicles and Sales tables
		System.out.println("Creating the Vehicle table");
		stmt.execute("CREATE TABLE IF NOT EXISTS Vehicle (" +
					"vehicle_id INTEGER PRIMARY KEY AUTOINCREMENT, make TEXT NOT NULL, model TEXT NOT NULL, " +
					"color TEXT, year INTEGER, price REAL NOT NULL, type TEXT, handlebar_type TEXT, " +
					"car_type TEXT, is_sold BOOLEAN DEFAULT FALSE, dealerships_id INTEGER, " +
					"FOREIGN KEY (dealerships_id) REFERENCES dealerships(id))");
		System.out.println("Creating the Sales table");
		stmt.execute("CREATE TABLE IF NOT EXISTS Sales (" +
					"sale_id INTEGER PRIMARY KEY AUTOINCREMENT, vehicle_id INTEGER NOT NULL, " +
					"user_id INTEGER NOT NULL, buyer_name TEXT, buyer_contact TEXT, " +
					"sale_date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
					"FOREIGN KEY (vehicle_id) REFERENCES Vehicle(vehicle_id), " +
					"FOREIGN KEY (user_id) REFERENCES users(user_id))");

		m_connection.commit();
	}

	/**
	 * Get the singleton instance of the DBManager
	 * Creates a new instance if one doesn't exist
	 *
	 * @return the singleton DBManager instance
	 * @throws SQLException if a database access error occurs
	 */
	public static DBManager getInstance() throws SQLException {
		if (m_dbManager == null) {
			m_dbManager = new DBManager();
		}

		return m_dbManager;
	}

	/**
	 * Get the database connection
	 *
	 * @return the Connection object for the database
	 */
	public Connection getConnection() {
		return m_connection;
	}
}
