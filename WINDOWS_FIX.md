# Windows Setup Instructions

If you're experiencing issues with the login screen not appearing on Windows, follow these steps:

## Option 1: Run the SQL script to add a dealership

1. Navigate to your project directory in Command Prompt:
   ```
   cd C:\path\to\MyCarDealershipSystem
   ```

2. Run the following command to add a default dealership to your database:
   ```
   sqlite3 dealership.sqlite3 < setup_dealership.sql
   ```

3. Compile and run the application:
   ```
   javac -cp ".;libs\sqlite-jdbc-3.49.1.0.jar" src\carDealership\*.java src\persistance\*.java
   java -cp ".;libs\sqlite-jdbc-3.49.1.0.jar" carDealership.Main
   ```

## Option 2: Create a dealership through the UI

Alternatively, if you see the "Dealership Setup" screen:

1. Enter valid dealership information:
   - Dealership Name: Any name you prefer
   - Location: Any location
   - Inventory Capacity: A number between 1-100

2. Click "Go" to create the dealership

3. You should now see the login screen where you can log in with one of these accounts:
   - Username: Ronika, Password: 123 (Admin)
   - Username: Max, Password: 456 (Manager)
   - Username: Jessica, Password: 789 (Salesperson)

## Issue explanation

The problem occurs because:
1. The application expects to find a dealership record in the database
2. If no dealership record exists, it shows the setup screen
3. After creating a dealership, it should show the login screen

The fixes we've made ensure that:
1. You can easily add a dealership through the SQL script
2. After creating a dealership, the app will correctly show the login screen