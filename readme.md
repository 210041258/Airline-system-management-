# Airline System Management
# (swip to presention branch (merged-<enabled>))

## Overview

This application provides a comprehensive system for managing airline operations, including flight bookings, passenger information, staff assignments, and flight schedules. It's built with Java for a robust backend and a user-friendly graphical interface using JavaFX, powered by MySQL for data persistence.

## Features

- **Flight Management**:
    - Create and manage flight schedules, including:
        - Departure and arrival times
        - Destinations
        - Flight numbers
    - Modify and delete flight schedules as needed.
- **Passenger Management**:
    - Book passengers onto flights, handling:
        - Passenger names
        - Seat allocation
        - Ticket issuance
    - View passenger details associated with specific flights.
- **Staff Management**:
    - Assign staff members to flights, overseeing:
        - Staff names
        - Flight assignments
        - Staff schedules 
    - Retrieve staff information linked to individual flights.
- **Database Integration**:
    - Stores flight, passenger, and staff data in a MySQL database.
    - Retrieve and update data from the database seamlessly using JDBC for connectivity. 

## Tech Stack

- **Programming Language**: Java
- **Database**: MySQL
- **GUI Framework**: JavaFX (with Scene Builder for UI design)
- **Database Connectivity**: JDBC for MySQL. 

## Setup Instructions

### Prerequisites

1. **Java Development Kit (JDK)**: Ensure you have a recent JDK installed on your system.  
   [Download and Installation Instructions](https://www.oracle.com/java/technologies/downloads/) 
2. **JavaFX SDK**: Download and install the JavaFX SDK from [OpenJFX Website](https://openjfx.io/).  
3. **MySQL**: Install MySQL if you haven't already and verify it's running. 
   [MySQL Download and Installation](https://dev.mysql.com/downloads/mysql/)
4. **JDBC Driver (Connector/J):** Obtain the MySQL JDBC Driver (Connector/J) from 
   [MySQL Download Page](https://dev.mysql.com/downloads/connector/j/). 

### Database Setup 

1. **Open MySQL Shell:** Launch the MySQL command line interface (or use MySQL Workbench or phpMyAdmin if you prefer a visual client).
2. **Create Database and Tables:**
    - Create a database named `airline_db`:
        ```sql
        CREATE DATABASE airline_db;
        ```
    - Select the `airline_db` database: 
        ```sql
        USE airline_db; 
        ```
    - Create the following tables:

        ```sql
        CREATE TABLE flights (
            flight_id INT PRIMARY KEY AUTO_INCREMENT,
            destination VARCHAR(100),
            departure_time VARCHAR(100)
        );

        CREATE TABLE passengers (
            passenger_id INT PRIMARY KEY AUTO_INCREMENT,
            name VARCHAR(100),
            flight_id INT,
            FOREIGN KEY (flight_id) REFERENCES flights(flight_id)
        );

        CREATE TABLE staff (
            staff_id INT PRIMARY KEY AUTO_INCREMENT,
            name VARCHAR(100),
            assigned_flight_id INT,
            FOREIGN KEY (assigned_flight_id) REFERENCES flights(flight_id)
        );
        ```

### Running the Application

1. **Import Project:** Open your Java IDE (IntelliJ IDEA, Eclipse, NetBeans) and:
   - Create a new Java project (or import an existing one). 
   - Copy the Java code (`AirlineSystemManagement.java`) for your main application class into the appropriate package in your project. 
2. **Configure JavaFX and JDBC:**
    - **JavaFX:**  Add the JavaFX SDK to your project's build path (instructions vary by IDE; often found in project settings, "Libraries," or "Dependencies").
    - **JDBC Driver:** Include the downloaded MySQL JDBC Driver JAR file (Connector/J) in your project's classpath. Refer to IDE documentation for adding JARs to your project's dependencies.
3. **Update Connection Details:** Open the `AirlineSystemManagement.java` file and modify these lines to use your actual MySQL connection settings:

    ```java
    String url = "jdbc:mysql://localhost:3306/airline_db"; // Replace with your database URL
    String username = "your_username";
    String password = "your_password";  
    ```

4. **Compile and Launch:** Compile and run the `AirlineSystemManagement` class.  This will build and execute your application, opening its JavaFX user interface.

## Future Enhancements

- **Flight Rescheduling**: Implement functionality for modifying flight schedules (e.g., changing departure times, destinations).
- **Enhanced Validation**: Add robust input validation and error handling to ensure data integrity and user experience. 
- **Passenger Notifications**: Create mechanisms to send notifications to passengers about flight delays, cancellations, or changes in schedule.
- **Additional Features:**  Consider adding features like:
    - Flight search and filtering
    - Passenger check-in
    - Baggage handling
    - Staff task management
    - Reporting and analytics.

## License

This project is released under the [MIT License](https://opensource.org/license/MIT).

## Getting Started:

1.  **Create New Project:**  If you are starting from scratch:
    -   Open your Java IDE (IntelliJ IDEA, Eclipse, NetBeans) and create a new JavaFX project.
2.  **Database Setup:** 
    -   Connect to your MySQL database and run the SQL commands from the `Database Setup` section above to create the tables. 
3.  **Implement Java Classes:** Create Java classes in your project: 
    -   **`AirlineSystemManagement.java` (Main Class):**  This class contains the `main` method that initializes and runs your JavaFX application.
    -   **`MainView.fxml` (FXML File):** Design your user interface using Scene Builder.
    -   **`MainViewController.java` (Controller Class):**  Write the logic for handling UI elements and database interactions for the main view.

Let me know if you have any more questions. I can help you with specific code examples or details if needed!
