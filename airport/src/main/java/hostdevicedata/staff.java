package hostdevicedata;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class staff {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/user_databases";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Root@2023";
    private static final String STAFF_INDEX_FILE = "staff-index.txt";

    private String username;
    private String email;
    private double salary;
    private String jobPosition;

    // SQL Queries
    private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS staff (" +
            "username VARCHAR(50) PRIMARY KEY, " +
            "email VARCHAR(100) NOT NULL, " +
            "salary DECIMAL(10, 2) NOT NULL, " +
            "job_position VARCHAR(100) NOT NULL" +
            ")";

    private static final String CHECK_IF_EXISTS_SQL = "SELECT COUNT(*) FROM staff WHERE username = ?";
    private static final String INSERT_SQL = "INSERT INTO staff (username, email, salary, job_position) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE staff SET email = ?, salary = ?, job_position = ? WHERE username = ?";
    private static final String DELETE_SQL = "DELETE FROM staff WHERE username = ?";
    private static final String SEARCH_BY_USERNAME_SQL = "SELECT * FROM staff WHERE username = ?";
    private static final String SEARCH_BY_JOB_POSITION_SQL = "SELECT * FROM staff WHERE job_position = ?";

    // Constructor
    public staff(String username, String email, double salary, String jobPosition) {
        setUsername(username);
        setEmail(email);
        setSalary(salary);
        setJobPosition(jobPosition);
        createStaffTable();
    }




    // Getters and Setters with validation
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }
        this.email = email;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        if (salary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative.");
        }
        this.salary = salary;
    }

    public String getJobPosition() {
        return jobPosition;
    }

    public void setJobPosition(String jobPosition) {
        if (jobPosition == null || jobPosition.isEmpty()) {
            throw new IllegalArgumentException("Job position cannot be null or empty.");
        }
        this.jobPosition = jobPosition;
    }

    // Method to create the staff table if it does not exist
    public static void createStaffTable() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_TABLE_SQL);
            System.out.println("Staff table created or already exists.");
        } catch (SQLException e) {
            System.err.println("Error creating staff table: " + e.getMessage());
        }
    }

    // Method to insert or update a staff record in the database
    public static void insertOrUpdateStaff(staff staff) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement checkStatement = connection.prepareStatement(CHECK_IF_EXISTS_SQL)) {
            checkStatement.setString(1, staff.getUsername());
            ResultSet resultSet = checkStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            String sql = count > 0 ? UPDATE_SQL : INSERT_SQL;
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                if (count > 0) {
                    preparedStatement.setString(1, staff.getEmail());
                    preparedStatement.setDouble(2, staff.getSalary());
                    preparedStatement.setString(3, staff.getJobPosition());
                    preparedStatement.setString(4, staff.getUsername());
                    System.out.println("Staff record updated.");
                } else {
                    preparedStatement.setString(1, staff.getUsername());
                    preparedStatement.setString(2, staff.getEmail());
                    preparedStatement.setDouble(3, staff.getSalary());
                    preparedStatement.setString(4, staff.getJobPosition());
                    System.out.println("Staff record inserted.");
                }
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error inserting/updating staff record: " + e.getMessage());
        }
    }

    // Method to delete a staff record by username
    public static int deleteStaffByUsername(String username) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setString(1, username);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Staff member deleted.");
                return 1;
            } else {
                System.out.println("Staff member with username " + username + " not found.");
                return 0;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting staff member: " + e.getMessage());
            return 0;

        }
    }

    // Method to search for a staff member by username
    public static staff searchByUsername(String username) {
        staff staff = null;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(SEARCH_BY_USERNAME_SQL)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                staff = new staff(
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getDouble("salary"),
                        resultSet.getString("job_position")
                );
                System.out.println("Staff found: " + staff.getUsername());
            } else {
                System.out.println("Staff member with username " + username + " not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error searching for staff member: " + e.getMessage());
        }
        return staff;
    }

    // Method to search for staff members by job position
    public static List<staff> searchByJobPosition(String jobPosition) {
        List<staff> staffList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(SEARCH_BY_JOB_POSITION_SQL)) {
            preparedStatement.setString(1, jobPosition);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                staff staff = new staff(
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getDouble("salary"),
                        resultSet.getString("job_position")
                );
                staffList.add(staff);
            }

            if (staffList.isEmpty()) {
                System.out.println("No staff found with job position: " + jobPosition);
            }
        } catch (SQLException e) {
            System.err.println("Error searching for staff by job position: " + e.getMessage());
        }
        return staffList;
    }

    // Method to store staff data to a text file
    public static void storeToFile(List<staff> staffList) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(STAFF_INDEX_FILE))) {
            for (staff staff : staffList) {
                String line = staff.getUsername() + "," +
                        staff.getEmail() + "," +
                        staff.getSalary() + "," +
                        staff.getJobPosition();
                bw.write(line);
                bw.newLine();
            }
            System.out.println("Staff data stored to " + STAFF_INDEX_FILE + " successfully.");
        } catch (IOException e) {
            System.err.println("Error storing staff data to file: " + e.getMessage());
        }
    }

}
