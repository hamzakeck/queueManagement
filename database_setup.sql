-- ========================================
-- Queue Management System - Database Setup
-- ========================================

-- Create Database
CREATE DATABASE IF NOT EXISTS queue_management_db 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE queue_management_db;

-- ========================================
-- Table: administrators
-- ========================================
CREATE TABLE administrators (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email)
) ENGINE=InnoDB;

-- ========================================
-- Table: agencies
-- ========================================
CREATE TABLE agencies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    address VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    total_counters INT DEFAULT 5,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_city (city)
) ENGINE=InnoDB;

-- ========================================
-- Table: services
-- ========================================
CREATE TABLE services (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    estimated_time INT DEFAULT 15 COMMENT 'Estimated time in minutes',
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_active (active)
) ENGINE=InnoDB;

-- ========================================
-- Table: citizens
-- ========================================
CREATE TABLE citizens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    phone VARCHAR(20),
    cin VARCHAR(50) COMMENT 'National ID Number',
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_cin (cin)
) ENGINE=InnoDB;

-- ========================================
-- Table: employees
-- ========================================
CREATE TABLE employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    agency_id INT NOT NULL,
    counter_id INT COMMENT 'Guichet/Counter number',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (agency_id) REFERENCES agencies(id) ON DELETE CASCADE,
    INDEX idx_email (email),
    INDEX idx_agency (agency_id)
) ENGINE=InnoDB;

-- ========================================
-- Table: tickets
-- ========================================
CREATE TABLE tickets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ticket_number VARCHAR(20) UNIQUE NOT NULL,
    citizen_id INT NOT NULL,
    service_id INT NOT NULL,
    agency_id INT NOT NULL,
    status ENUM('WAITING', 'CALLED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'WAITING',
    position INT COMMENT 'Current position in queue',
    counter_id INT COMMENT 'Assigned counter number',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    called_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (citizen_id) REFERENCES citizens(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE,
    FOREIGN KEY (agency_id) REFERENCES agencies(id) ON DELETE CASCADE,
    INDEX idx_status (status),
    INDEX idx_agency_service (agency_id, service_id),
    INDEX idx_created (created_at)
) ENGINE=InnoDB;

-- ========================================
-- Insert Sample Data
-- ========================================

-- Insert Admin (password: admin123)
INSERT INTO administrators (first_name, last_name, email, password) 
VALUES ('Admin', 'System', 'admin@queue.com', 'admin123');

-- Insert Agencies
INSERT INTO agencies (name, address, city, phone, total_counters) VALUES
('Agence Centre-Ville', '123 Avenue Mohammed V', 'Casablanca', '0522-123456', 8),
('Agence Maarif', '456 Boulevard Zerktouni', 'Casablanca', '0522-234567', 6),
('Agence Hay Hassani', '789 Rue Moulay Ismail', 'Casablanca', '0522-345678', 5);

-- Insert Services
INSERT INTO services (name, description, estimated_time, active) VALUES
('Carte d''identité nationale', 'Demande ou renouvellement de CIN', 20, TRUE),
('Passeport', 'Demande ou renouvellement de passeport', 25, TRUE),
('Acte de naissance', 'Demande d''acte de naissance', 15, TRUE),
('Certificat de résidence', 'Obtention de certificat de résidence', 10, TRUE),
('Légalisation', 'Légalisation de documents', 15, TRUE);

-- Insert Sample Citizens (password: citizen123)
INSERT INTO citizens (first_name, last_name, email, phone, cin, password) VALUES
('Mohammed', 'Alami', 'mohammed@email.com', '0661234567', 'AB123456', 'citizen123'),
('Fatima', 'Benani', 'fatima@email.com', '0662345678', 'CD234567', 'citizen123'),
('Hassan', 'El Idrissi', 'hassan@email.com', '0663456789', 'EF345678', 'citizen123');

-- Insert Sample Employees (password: employee123)
INSERT INTO employees (first_name, last_name, email, password, agency_id, counter_id) VALUES
('Ahmed', 'Tazi', 'ahmed.tazi@queue.com', 'employee123', 1, 1),
('Samira', 'Chraibi', 'samira.chraibi@queue.com', 'employee123', 1, 2),
('Youssef', 'Benjelloun', 'youssef.b@queue.com', 'employee123', 2, 1),
('Amina', 'Fassi', 'amina.fassi@queue.com', 'employee123', 2, 2);

-- Insert Sample Tickets
INSERT INTO tickets (ticket_number, citizen_id, service_id, agency_id, status, position) VALUES
('A001', 1, 1, 1, 'WAITING', 1),
('A002', 2, 2, 1, 'WAITING', 2),
('B001', 3, 3, 2, 'WAITING', 1);

-- ========================================
-- Useful Queries for Development
-- ========================================

-- Get all waiting tickets for an agency
-- SELECT * FROM tickets WHERE agency_id = 1 AND status = 'WAITING' ORDER BY position;

-- Get citizen's active ticket
-- SELECT t.*, s.name as service_name, a.name as agency_name 
-- FROM tickets t 
-- JOIN services s ON t.service_id = s.id 
-- JOIN agencies a ON t.agency_id = a.id 
-- WHERE t.citizen_id = 1 AND t.status IN ('WAITING', 'CALLED', 'IN_PROGRESS');

-- Get employee's assigned tickets
-- SELECT * FROM tickets WHERE counter_id = 1 AND status = 'IN_PROGRESS';

-- Get daily statistics
-- SELECT 
--     DATE(created_at) as date,
--     COUNT(*) as total_tickets,
--     SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completed,
--     SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) as cancelled
-- FROM tickets 
-- GROUP BY DATE(created_at);

-- ========================================
-- Test Credentials Summary
-- ========================================
-- ADMIN:
--   Email: admin@queue.com
--   Password: admin123
--
-- EMPLOYEE:
--   Email: ahmed.tazi@queue.com
--   Password: employee123
--
-- CITIZEN:
--   Email: mohammed@email.com
--   Password: citizen123
-- ========================================
