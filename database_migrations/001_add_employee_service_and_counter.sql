-- Migration: add service_id and counter_id to employees if missing
-- Run: mysql -u root -p queue_management_db < database_migrations/001_add_employee_service_and_counter.sql

USE queue_management_db;

-- Add service_id if missing (MySQL 8: IF NOT EXISTS)
ALTER TABLE employees
  ADD COLUMN IF NOT EXISTS service_id INT NOT NULL DEFAULT 1 AFTER agency_id;

-- Add counter_id if missing
ALTER TABLE employees
  ADD COLUMN IF NOT EXISTS counter_id INT NULL AFTER service_id;

-- Create an index for service if missing
CREATE INDEX IF NOT EXISTS idx_service ON employees (service_id);

-- Add a FK to services if not present (best-effort, may fail if name exists)
-- First check whether the FK already exists (information_schema)
SET @fk_exists = (
  SELECT COUNT(*)
  FROM information_schema.REFERENTIAL_CONSTRAINTS rc
  JOIN information_schema.KEY_COLUMN_USAGE kcu
    ON rc.CONSTRAINT_NAME = kcu.CONSTRAINT_NAME
  WHERE rc.CONSTRAINT_SCHEMA = DATABASE()
    AND rc.TABLE_NAME = 'employees'
    AND kcu.COLUMN_NAME = 'service_id'
);

-- If no FK found, add it (change name if needed)
-- Note: This part may fail on older MySQL; run manually if needed

-- Add optional FK (commented out for safety; uncomment to enforce referential integrity)
-- ALTER TABLE employees
--   ADD CONSTRAINT fk_employees_service FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE;

-- Optional: add a default service row if none exists (safe fallback)
INSERT INTO services (name, description, estimated_time, active)
SELECT 'Default Service', 'Auto-created service for migration', 15, TRUE
WHERE NOT EXISTS (SELECT 1 FROM services WHERE id = 1);

-- Done
SELECT 'Migration finished' AS message;
