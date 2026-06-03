# Ticket Platform - Backend

This is the backend of the Ticket Platform, a robust and secure REST API designed to handle authentication, user management, and ticket lifecycles. It utilizes a connection pooler to securely communicate with a cloud database.

> ℹ️ **Note:** This repository only contains the backend code. The frontend application is hosted in a separate repository. You can find it here: **[[Link to Frontend Repo](https://github.com/Clintbr/TicketSystem-Frontend/tree/main)]**.

## 🚀 Tech Stack

* **Framework:** Java / Spring Boot
* **Security:** Spring Security (with OAuth2 / JWT Resource Server capabilities)
* **Database & ORM:** PostgreSQL (hosted on Supabase) & Hibernate / JPA
* **Database Pooling:** HikariCP

## 🛠️ Configuration & Environment Variables

The application is containerized and reads its configuration via environment variables. To run this project, you need to set up the following keys (e.g., in your IDE environment settings or a `.env` file):

```env
DB_PORT=your_database_port
DB_NAME=your_database_name
DB_USER=your_supabase_pooler_username
DB_PASSWORD=your_database_password
SUPABASE_PROJEKT_ID=your_supabase_project_id
SUPABASE_JWT_SECRET=your_supabase_jwt_secret

```

## 🌐 Deployment

The backend is deployed on **Render**.

* The `pom.xml` is configured with disabled resource filtering (`<filtering>false</filtering>`) to prevent encoding issues during the cloud build process.
* The database connection utilizes `sslmode=require` to ensure encrypted communication with the Supabase PostgreSQL instance.
