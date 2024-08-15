
# Payment System Application

## Overview

The Payment System Application provides a full-stack solution for managing merchant accounts and their transactions. The
backend, built with Spring Boot, handles transaction processing, validation, and user authentication with role-based
access control, while the frontend, developed with React and Material-UI, offers a user-friendly interface for
administrators and merchants to manage transactions and merchant data. The application features a secure REST API,
real-time data grid interfaces for managing merchants and transactions, and integrates role-specific access to ensure
proper authorization.

## Prerequisites

Ensure you have the following installed on your machine:

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)
- **Apache Maven 3.9.8**
- **Java 21.0.2**

### Checking Your Versions

You can verify your installed versions with the following commands:

```bash
mvn -v
java -version
```

- Maven should display `Apache Maven 3.9.8`.
- Java should display `java version "21.0.2"`.

## Running the Application

### 1. Clone the Repository

First, clone the repository to your local machine.

### 2. Build the Application

Before building the Docker images, ensure that the application is correctly packaged by running:

```bash
mvn clean package
```

This will:

- Compile the Java code.
- Process the resources (including copying the React build files).
- Package everything into a JAR file located in the `target/` directory.

### 3. Build and Run the Docker Containers

After packaging the application, build and start the Docker containers using Docker Compose:

```bash
docker-compose up --build
```

This command will:

- Build the Docker image for the Spring Boot application.
- Start the PostgreSQL container.
- Wait for PostgreSQL to be ready.
- Start the Spring Boot application container once PostgreSQL is healthy.

The application will be accessible at [https://localhost:8443](https://localhost:8443).

### 4. Stopping the Application

To stop the running containers, use the following command:

```bash
docker-compose down
```

This command stops and removes the containers, but the Docker images will remain available locally.

## Environment Variables

The application uses a `.env` file to manage environment variables, which configure the PostgreSQL database, SSL
settings, and paths to resources.

### Modifying Environment Variables

To change the environment variables, edit the `.env` file located in the root directory of the project:

```plaintext
# Database Credentials
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password

# SSL Keystore Password
KEYSTORE_PASS=your_keystore_password

# CSV File Path (optional)
CSV_PATH=/path/to/your/csvfile.csv
```

Ensure that all required environment variables are correctly set in the `.env` file.

## User Generation

On application startup, users will be automatically generated from the CSV file located at `src/main/resources/csv/default-users.csv`.

## Generating Transactions

To generate transactions, you can use the following commands:

### JSON Format

```bash
curl -k -X POST https://localhost:8443/api/v1/transactions \
-u merchant1@example.com:password123 \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-d '{
  "customerEmail": "customer@example.com",
  "customerPhone": "1234567890",
  "amount": 50.00,
  "referenceTransactionId": "5e57382a-eb85-4017-84c5-3efd09a15ec0",
  "type": "AUTHORIZE"
}'
```

### XML Format

```bash
curl -k -X POST https://localhost:8443/api/v1/transactions \
-u merchant1@example.com:password123 \
-H "Content-Type: application/xml" \
-H "Accept: application/xml" \
-d '<?xml version="1.0" encoding="UTF-8"?>
<TransactionDto>
  <customerEmail>customer@example.com</customerEmail>
  <customerPhone>1234567890</customerPhone>
  <amount>100.00</amount>
  <referenceTransactionId>24c06e55-e400-4bd3-8ce5-8e3c30753964</referenceTransactionId>
  <type>CHARGE</type>
</TransactionDto>'
```

### Possible Transaction Types

- AUTHORIZE
- CHARGE
- REFUND
- REVERSAL

## Additional Information

- **Spring Boot Version**: 3.3.2
- **PostgreSQL Version**: 16.4
- **React**: 18.3.1
