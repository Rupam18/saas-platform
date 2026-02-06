#!/bin/bash
# Force H2 Database Configuration for Local Demo
export SPRING_DATASOURCE_URL='jdbc:h2:mem:saas_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE'
export SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.h2.Driver
export SPRING_DATASOURCE_USERNAME=sa
export SPRING_DATASOURCE_PASSWORD=password
export SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.H2Dialect

echo "🚀 Starting Backend in LOCAL DEMO MODE (H2 Database)..."
./mvnw spring-boot:run
