# Java-crud-api-using-mysql

This is my project's README file. It contains information about the project, its dependencies, and a brief description.

## Application Details

- Tested Java Version: 10
- Tomcat Version: 9.0.62
- MySQL Connector Version: 8.0.30

## About the Application

This application is a backend API-only project that serves as a ...

## Gson Library

We use the Gson library in this project to handle JSON data. Gson is a Java library that can be used to convert Java objects to JSON and vice versa.

- **Library**: Gson
- **Version**: 2.8.8 (You can specify the version you are using)
- **Purpose**: Gson is used for parsing and serializing JSON data in the API.

## API Routes and Methods

Here are the routes and methods/functions used in the API:

### 1. Create a New Record

- **Route**: `POST /record`
- **Description**: Create a new record.
- **Function/Method**: `doPost`

### 2. Get a Record by ID

- **Route**: `GET /record/{id}`
- **Description**: Retrieve a record by its ID.
- **Function/Method**: `doGet`

### 3. Get All Records

- **Route**: `GET /record`
- **Description**: Retrieve a list of all records.
- **Function/Method**: `doGet`

### 4. Delete a Record by ID

- **Route**: `DELETE /record/{id}`
- **Description**: Delete a record by its ID.
- **Function/Method**: `doDelete`

### 5. Delete Multiple Records

- **Route**: `DELETE /record`
- **Description**: Delete multiple records.
- **Function/Method**: `doDelete`

### 6. Update a Record

- **Route**: `PUT /record`
- **Description**: Update a record (description to be provided).
- **Function/Method**: `doPut`

## Getting Started

To get started with this project, you can follow the steps below:

1. Clone the repository.
2. Set up your development environment.
3. Run the application.
