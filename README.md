# Drink Dispenser Backend

## Overview
This project implements the backend for drink dispenser machines using Hexagonal Architecture, Domain-Driven Design 
(DDD), and reactive programming with WebFlux, R2DBC, and Spring Cloud Stream with RabbitMQ. The goal is to manage 
all the drink dispensers within the office building, handling product inventory, dispensing drinks, and notifying users 
and external systems asynchronously.

## Features
- Add products to the dispenser
- Consult product stock
- Consult machine status
- Dispense products
- Notify user of internal states via LCD
- Notify external system when product stock reaches zero

## Technologies Used
- Java 11
- Spring Boot
- Spring WebFlux
- R2DBC with MySQL
- Spring Cloud Stream with RabbitMQ

## Project Structure
The project follows Hexagonal Architecture, separating concerns into different layers: application, domain, 
infrastructure and integration.

### Layers
- **Application Layer**: Contains service and use case implementations.
- **Domain Layer**: Defines core business logic and domain models.
- **Infrastructure Layer**: Contains configurations, controllers, DTOs, entities, mappers, repository implementations.
- **Integration Layer**: Manages messaging with RabbitMQ, including event models, producers, and consumers.

## Getting Started

### Prerequisites
- Java 11
- Maven
- Docker (for MySQL and RabbitMQ)

### Setup

1. **Clone the repository**
    ```bash
    git clone https://github.com/Nereitay/drinks-dispenser-backend.git
    cd drinks-dispenser-backend
    mvn clean package -DskipTests
    ```

2. **Run MySQL and RabbitMQ using Docker**
     
Copy the `drinks-dispenser-backend-0.0.1-SNAPSHOT.jar` from target into the `docker` directory where the 
 `Dockerfile` and 
   `docker-compose.yml` are located.
   ```bash
     cd docker
     docker-compose up -d   
   ```
To run the project in your IDE without containers, please ensure to update the RabbitMQ configuration in `application.yml` and the MySQL configuration in `src/main/java/es/kiwi/drinksdispenser/infrastructure/config/R2dbcConfiguration.java`.
3. **Database schema and initial data**

The `dispenser_db` database and four main tables with initial data will be created in MySQL.
### Database Schema
The database schema includes four main tables: `machines`, `products`, `machine_products`, and `coins`. Below are the details:

#### `machines` Table

This table stores information about the dispensers located throughout the building, each identified by a unique machine ID.

| Column     | Type           | Description                                           |
|------------|----------------|-------------------------------------------------------|
| id         | BIGINT         | Unique identifier for each machine                    |
| name       | VARCHAR(50)    | Name of the machine                                   |
| location   | VARCHAR(255)   | Location of the machine                               |
| status     | INT            | Status of the machine (0 - AVAILABLE, 1 - OUT_OF_ORDER, 2 - MAINTENANCE) |
| operator   | VARCHAR(255)   | Operator managing the machine                         |
| created_at | TIMESTAMP      | Timestamp for record creation                         |
| updated_at | TIMESTAMP      | Timestamp for the last update                         |

#### `products` Table

This table manages product-related data, with each product identified by an ID and a unique name (in uppercase as 
parameters). 
The product name is used to find products in various use cases. Currently, there are four options. This project does not include a use case to expand the product options, as this function should be handled by an external management system.

- COKE (2.00 €) 
- REDBULL (2.25 €) 
- WATER (0.50 €) 
- ORANGE_JUICE (1.95 €)

| Column     | Type           | Description                                           |
|------------|----------------|-------------------------------------------------------|
| id         | BIGINT         | Unique identifier for each product                    |
| name       | VARCHAR(100)   | Name of the product                                   |
| price      | DECIMAL(6, 2)  | Price of the product                                  |
| type       | VARCHAR(50)    | Type of product, default is 'DRINK'                   |

#### `machine_products` Table

This table contains all product information within a dispenser. To find a unique product, combine the machine ID, product ID, and expiration date.

| Column          | Type           | Description                                           |
|-----------------|----------------|-------------------------------------------------------|
| id              | BIGINT         | Unique identifier for each machine-product relationship |
| machine_id      | BIGINT         | Reference to the machine                              |
| product_id      | BIGINT         | Reference to the product                              |
| stock           | INT UNSIGNED   | Current stock of the product in the machine           |
| expiration_date | DATE           | Expiration date of the product                        |
| operator        | VARCHAR(255)   | Operator managing the stock                           |
| updated_at      | TIMESTAMP      | Timestamp for the last update                         |

#### `coins` Table

This table manages the coins within the dispenser. Each coin is identified by machine ID and denomination. The machine accepts six types of coins:
- FIVE_CENTS
- TEN_CENTS
- TWENTY_CENTS
- FIFTY_CENTS
- ONE_EURO
- TWO_EURO

| Column       | Type           | Description                                           |
|--------------|----------------|-------------------------------------------------------|
| id           | BIGINT         | Unique identifier for each coin record                |
| machine_id   | BIGINT         | Reference to the machine                              |
| denomination | VARCHAR(50)    | Denomination of the coin (e.g., 5 cents, 1€)          |
| value        | DECIMAL(6, 2)  | Value of the coin                                     |
| quantity     | INT UNSIGNED   | Number of coins of this denomination available in the machine |
| updated_at   | TIMESTAMP      | Timestamp for the last update                         |

4. **Swagger for API use**

Once the project is started, navigate to http://localhost:8088/api to access the Swagger UI for testing the API endpoints.

## Usage

### Endpoints
- **Add More Products To a dispenser**: `POST /v1/machine-products`

This endpoint accepts a list of products, each identified by machine ID and product name (COKE, REDBULL, WATER, ORANGE_JUICE). If the machine is not found in the machines table or if any products are not in the specified options, the service will return a 404 "Machine/Products not found."

Initially, there are two machines: machine ID 1 (available status) and machine ID 2 (out of order). You can add stock to a machine even if it is not available.

- **Consult Product Stock**: `GET /v1/machine-products/stock/{machineId}`

This endpoint receives a machineId and a product option (an enum type). It returns the stock of the specified product in the dispenser. Expired products are excluded from the stock count. If the machine ID does not exist, the service returns a 404 error.

- **Consult Machine Status**: `GET /v1/machine/{machineId}`

This endpoint provides information about the dispenser, including its status (available, out of order, or maintenance) and name. If the machine ID is not found, it returns a 404 error.

- **Consult the products options in the machine**: `GET /v1/machine-products/products-option/{machineId}`

This endpoint returns all product options available in the dispenser, regardless of stock levels. If the machine ID is not found, it returns a 404 error.

- **Dispense Product**: `POST /v1/dispense-drink`

The request should include:

1. `machineId` - The dispenser the user is operating.
2. `productOption` - One of the four options (COKE, REDBULL, WATER, ORANGE_JUICE).
3. `coins` - A list of coins as strings (FIVE_CENTS, TEN_CENTS, TWENTY_CENTS, FIFTY_CENTS, ONE_EURO, TWO_EURO). Coins can be duplicated to represent multiple insertions (e.g., ["ONE_EURO", "ONE_EURO", "ONE_EURO"] for 3 euros).
4. `confirmed` - A boolean value indicating whether to proceed with the dispensing process.
If `confirmed` is true, the service follows the flow: CHECK_PRODUCT_AVAILABILITY -> VALIDATE_MONEY -> 
   DISPENSE_PRODUCT. If any step takes more than 5 seconds or an error occurs, the dispenser will return the coins, 
   cancel the operation with a message, and also will send internal states to the LCD app. A simple message consumer in the project will 
   print the state to the console. If the product stock reaches zero (excluding expired items) after dispensing, a notification is sent to a manager app. The message is sent to a RabbitMQ topic, and consumers can receive it based on different routing keys. The project includes three consumers: one receives all messages, another only messages related to machine 1, and the last one only messages related to ORANGE_JUICE.

If `confirmed` is false, the service will return the coins directly.


### Testing

#### Unit Tests
Unit tests and integration tests are located in `src/test/java/es/kiwi/drinkdispenser`, mainly using Mockito, Junit 
and test binder for asynchronous integrations.
