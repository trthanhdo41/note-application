# Note Application

A comprehensive web-based note application built with Java, Spring Boot, and MySQL. This project features user authentication, two-factor authentication (2FA), group management, and extensive note-taking capabilities.

## Features

- **User Management:**
  - 📝 User registration, login, and logout
  - 👤 User profile management
  - 🔐 Two-factor authentication (2FA) with Google Authenticator
  - 📧 Password recovery via email
  - ✉️ Account activation via email
  - 📨 Email notifications for account updates and deletions
  - 📷 Avatar capture and storage

- **Note Management:**
  - 📒 Personal note creation, editing, and deletion
  - 👥 Group management: create, edit, delete groups
  - 📑 Group notes: create, edit, delete group notes
  - 📊 Export group notes to Excel
  - 🗑️ Trash management for deleted notes

- **Admin Dashboard:**
  - 🛠️ User and group management
  - 📈 Reporting and statistics

## Technologies

- ☕ Java
- 🌱 Spring Boot
- 🔒 Spring Security
- 🗃️ Spring Data JPA
- 🖼️ Thymeleaf
- 🎨 Bootstrap
- 🐬 MySQL
- ☁️ AWS S3
- 📜 Jakarta Servlet
- 🛠️ Maven
- 📝 Lombok
- 📱 Google Authenticator
- 📧 JavaMail

## Getting Started

### Prerequisites

- ☕ Java Development Kit (JDK) 8 or higher
- 🛠️ Maven
- 🐬 MySQL
- ☁️ AWS account (for S3)

### Installation

1. 📥 Clone the repository:
   ```bash
   git clone https://github.com/trthanhdo41/note-application.git
   cd note-application
   
2. 🐬 Set up the database:
   ![erd](https://github.com/trthanhdo41/note-application/assets/136252271/588dc430-b1c7-46a8-bc8a-0f1f1108d4a2)
   Create a MySQL database named note_app and run the program.
   Or import the database backup file (database_backup.sql) into your MySQL database
   
4. 🛠️ Update the database configuration:
   Update the src/main/resources/application.properties file with your database credentials:
   spring.datasource.url=jdbc:mysql://localhost:3306/note_app
   spring.datasource.username=[username]
   spring.datasource.password=[password]

5. ▶️ Run the application:
   mvn spring-boot:run

Usage
🌐 Access the application at http://localhost:8080
📝 Register a new account or log in with existing credentials
Contributing
If you would like to contribute to this project, please fork the repository and submit a pull request. For major changes, please open an issue first to discuss what you would like to change.

License
This project is licensed under the MIT License. See the LICENSE file for details.

Contact
For any questions or suggestions, please contact trthanhdo41@gmail.com.
