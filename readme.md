
# Airline System Management
## pre_final-presention branch what we can do here i check it before we will contine (init-skill-check)
## Overview
This is an **Airline System Management** application developed in **Java** with a graphical interface using **JavaFX** and data management using **MySQL** connected via **JDBC**. The system handles flight bookings, passenger information, staff assignments, and flight schedules. 

## Features
- **Flight Management**: Manage flight schedules, including departures and arrivals.
- **Passenger Management**: Handle passenger booking, ticketing, and seating allocation.
- **Staff Management**: Assign staff to flights and manage staff schedules.
- **Data Retrieval**: Retrieve flight, booking, and staff details from the MySQL database.

## Tech Stack
- **Programming Language**: Java
- **Database**: MySQL
- **GUI Framework**: JavaFX with Scene Builder for designing the user interface.
- **Database Connectivity**: JDBC for MySQL.

## Setup Instructions

### Prerequisites:
1. **Java Development Kit (JDK)**: Ensure JDK is installed.
2. **JavaFX SDK**: Install JavaFX SDK from [JavaFX website](https://gluonhq.com/products/javafx/).
3. **MySQL**: Ensure MySQL is installed and running.
4. **JDBC Driver**: Add the MySQL JDBC Driver to your project. Download it [here](https://dev.mysql.com/downloads/connector/j/).

### Database Setup:
1. Open your MySQL command line or workbench.
2. Run the following commands to create the database and tables:

```sql
CREATE DATABASE airline_db;
USE airline_db;

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


### Running the Application:
1. Import the project into your preferred Java IDE (e.g., IntelliJ IDEA, Eclipse).
2. Make sure to add the **JavaFX SDK** and **JDBC Driver** to your project build path.
3. Replace the JDBC connection URL, username, and password in the `AirlineSystemManagement.java` file with your MySQL credentials.
4. Compile and run the `AirlineSystemManagement` class.

```bash
javac AirlineSystemManagement.java
java AirlineSystemManagement
```
**IT'S JUST EXAMPLE ON THE MAIN CODE (THE CODE IS THE PART OF THE DISTRUBTION )**
## Future Enhancements
- **Flight Rescheduling**: Add functionality for rescheduling flights.
- **Enhanced Validation**: Implement input validation and error handling.
- **Passenger Notifications**: Notify passengers about flight delays or cancellations.

## License
This project is released under the [MIT License](https://opensource.org/license/MIT).
