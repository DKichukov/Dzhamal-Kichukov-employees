# Employee Collaboration Tracker

This project is a Spring Boot application that identifies the pair of employees who have worked together on the same project for the longest period. It also displays all common projects for each pair of employees.

## Features

- **Input Data**: Accepts a CSV file with the following format:
  ```
  EmpID, ProjectID, DateFrom, DateTo
  ```
  - `DateTo` can be `NULL`, which is treated as today's date.

- **UI**: A user-friendly interface built with Thymeleaf and Bootstrap:
  - Allows users to upload a CSV file.
  - Displays the longest collaboration pair and all common projects in a table.

## Bonus Features

1. **UI for File Upload**:
   - Users can upload a CSV file through the web interface.
   - Displays all common projects in a data grid with the following columns:
     - Employee ID #1
     - Employee ID #2
     - Project ID
     - Days Worked Together

2. **Support for Multiple Date Formats**:
   - The application supports a wide range of date formats, making it flexible for various input data.

## How to Run

### Prerequisites

- Java 21 or higher
- Maven 3.9.9 or higher
- Docker (optional, for containerized deployment)

### Steps to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/DKichukov/Dzhamal-Kichukov-employees.git
   cd Dzhamal-Kichukov-employees
   ```

2. Build the project:
   ```bash
   ./mvnw clean package
   ```

3. Run the application:
   ```bash
   java -jar target/employee-collaboration-tracker-0.0.1-SNAPSHOT.jar
   ```

4. Access the application in your browser at:
   ```
   http://localhost:8080
   ```

### Docker Deployment

1. Build the Docker image:
   ```bash
   docker build -t employee-collaboration-tracker .
   ```

2. Run the Docker container:
   ```bash
   docker run -p 8080:8080 employee-collaboration-tracker
   ```

## Usage

1. Navigate to the home page.
2. Upload a CSV file with the required format.
3. View the results:
   - The longest collaboration pair is displayed.
   - A table lists all common projects with the number of days worked together.

## Project Structure

- **Backend**: Spring Boot application with services for CSV parsing and collaboration calculation.
- **Frontend**: Thymeleaf templates styled with Bootstrap.
- **Error Handling**: Global exception handler for user-friendly error messages.

## Example Input

```csv
EmpID,ProjectID,DateFrom,DateTo
101,1,01.01.2020,30.06.2020
102,1,15.03.2020,30.09.2020
103,2,2021/01/01,NULL
104,2,2021/02/01,2021/05/15
```

## Example Output

### Longest Collaboration Pair

- Employee 101 and Employee 102 worked together for **108 days**.

### All Common Projects

| Employee ID #1 | Employee ID #2 | Project ID | Days Worked Together |
|----------------|----------------|------------|-----------------------|
| 101            | 102            | 1          | 108                   |
| 103            | 104            | 2          | 104                   |

## Technologies Used

- **Backend**: Spring Boot, Java 21
- **Frontend**: Thymeleaf, Bootstrap
- **Build Tool**: Maven

## Author

- **FirstName LastName**
- GitHub: [https://github.com/Dzhamal-Kichukov-employees](https://github.com/DKichukov/Dzhamal-Kichukov-employees)
```
