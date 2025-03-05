# Camp Slot Availability Notifier

## Project Overview
This project automatically checks for available camping slots at a specific recreation.gov site for a specified month. It stores any found slots in a MySQL database and sends an email notification if slots become available. Additionally, the project exposes an API endpoint and provides a simple web page to manually trigger a check and view the latest results.

---

## Architecture

### Backend Layers
| Layer                                     | Responsibility                                                           |
| ----------------------------------------- | ------------------------------------------------------------------------ |
| Service (`CampResortsService`)            | Fetches and parses data from recreation.gov, then saves results to MySQL |
| Repository (`SlotAvailabilityRepository`) | Handles saving and retrieving data from the MySQL database               |
| Controller (`CampResortsController`)      | Exposes `/api/check-slots` to trigger manual checks                      |
| Scheduler (`CampResortsScheduler`)        | Runs automatic checks every 5 minutes                                    |
| Email Service (`EmailService`)            | Sends email notifications via Formspree                                  |

### Frontend
- A simple HTML page to trigger slot checks and display the latest results.

---

## Technologies Used
- Java 17
- Spring Boot 3.4.3
- Spring Data JPA
- MySQL
- Formspree (email notifications)
- HTML + JavaScript (frontend)
- Maven

---

## Setup Instructions

### Step 1: Install and Set Up MySQL
1. Install MySQL.
2. Start the MySQL server.
3. Log into the MySQL shell:
    ```bash
    mysql -u root -p
    ```
4. Create the database:
    ```sql
    CREATE DATABASE campslots;
    ```

---

### Step 2: Configure `application.properties`
Located in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/campslots
spring.datasource.username=root
spring.datasource.password=your_password_here
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

---

### Step 3: Configure Formspree
Inside `EmailService.java`, set your Formspree URL:
```java
private static final String FORM_URL = "https://formspree.io/f/your-form-id";
```

---

### Step 4: Build and Run the Project
Run the project using:
```bash
./mvnw spring-boot:run
```

---

## How It Works

### Automatic Checks
Every 5 minutes, the application fetches available slots, saves them to the database, and sends email notifications if slots are found.

### Manual Trigger
You can manually trigger a slot check via:
```
GET http://localhost:8080/api/check-slots
```

### Frontend Interface
Visit:
```
http://localhost:8080/index.html
```
Click "Check Slots Now" to trigger a check and display the results in a table.

---

## Database Schema
| Column           | Type         | Description                |
| ---------------- | ------------ | -------------------------- |
| id               | BIGINT (PK)  | Auto-increment primary key |
| date             | VARCHAR(255) | Date of available slot     |
| remaining_slots  | INT          | Number of remaining slots  |
| checked_at       | DATETIME     | Timestamp of the check     |

---

## REST API Endpoints
| Method | Endpoint           | Description                                           |
| ------ | ------------------ | ----------------------------------------------------- |
| GET    | `/api/check-slots` | Manually triggers a check and returns available slots |

---

## Frontend HTML Example
```html
<h2>Manual Camp Slot Check</h2>
<button onclick="checkSlots()">Check Slots Now</button>
<table border="1">
    <thead>
        <tr>
            <th>Date</th>
            <th>Remaining Slots</th>
            <th>Checked At</th>
        </tr>
    </thead>
    <tbody id="results"></tbody>
</table>
<script>
async function checkSlots() {
    const response = await fetch('/api/check-slots');
    const slots = await response.json();

    const table = document.getElementById('results');
    table.innerHTML = ''; // Clear old results

    slots.forEach(slot => {
        const row = table.insertRow();
        row.insertCell(0).innerText = slot.date;
        row.insertCell(1).innerText = slot.remainingSlots;
        row.insertCell(2).innerText = slot.checkedAt;
    });
}
</script>
```

---

## Future Enhancements
- Add date filters to the `/api/check-slots` endpoint.
- Build a more interactive frontend using React or Vue.
- Replace Formspree with direct SMTP email sending.

---

## Contact
Created by: **Kevin You**  
GitHub: [k-you28](https://github.com/k-you28)  
LinkedIn: [linkedin.com/in/kyou24](https://www.linkedin.com/in/kyou24)

