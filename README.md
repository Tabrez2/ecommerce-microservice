🛒 E-Commerce Microservices Architecture

A distributed e-commerce backend system built with Java Spring Boot, utilizing Spring Cloud for orchestration and Docker for infrastructure management.

🏗️ System Overview

The architecture is divided into specialized microservices and essential infrastructure components, ensuring high availability and scalability.

Core Microservices (Spring Boot)

Service

Port

Description

Config Server

8888

Centralized configuration management.

Discovery Service

8761

Eureka server for service registration and discovery.

Gateway Service

8222

API entry point and request routing.

Customer Service

8090

Manages customer profiles and data.

Product Service

8050

Handles product catalog and inventory.

Order Service

8070

Manages the order lifecycle.

Infrastructure Components (Docker)

Databases:

PostgreSQL (Port 5432) for relational data.

MongoDB (Port 27017) for document-based storage.

Messaging: Kafka (Port 9092) & Zookeeper (Port 2181) for event-driven communication.

Identity Management: Keycloak (Port 9098) for IAM.

Observability: Zipkin (Port 9411) for distributed tracing.

Tools: pgAdmin (Port 5050), Mongo-Express (Port 8081), and Mail-Dev (Port 1080).

🚀 Getting Started

Prerequisites

Docker & Docker Compose

Java JDK (with Maven)

PowerShell (for automatic startup)

1. Initialize Infrastructure

Run the Docker containers to spin up the required databases and messaging queues:

docker-compose up -d


2. Start the Microservices

You must follow a specific startup order to ensure services register correctly.

Automatic Startup (Recommended)

Use the provided PowerShell script to launch all services in the correct sequence:

./start-all-services.ps1


Manual Startup Order

Config Server: cd services/config-server && ./mvnw.cmd spring-boot:run

Discovery Service: cd services/discovery && ./mvnw.cmd spring-boot:run

Business Services: Start Customer, Product, and Order services individually.

Gateway Service: Start this last to ensure routes are available.

🔗 Internal Access Points

Eureka Dashboard: http://localhost:8761

Config Server Check: http://localhost:8888/customer-service/default

API Gateway (Main Entry): http://localhost:8222

Zipkin Tracing: http://localhost:9411

Keycloak Admin: http://localhost:9098

MailDev UI: http://localhost:1080

🛠️ Troubleshooting

Port Conflicts: If a service fails to start, check for blocking processes: netstat -aon | findstr ":PORT".

Database Readiness: Ensure PostgreSQL and MongoDB containers are healthy before starting business services.

Config Sync: If changes are made to configurations, restart the Config Server followed by the dependent microservice.

Developed by Tabrez
