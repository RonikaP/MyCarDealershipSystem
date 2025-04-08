-- This script inserts a default dealership if none exists
-- Run this with: sqlite3 dealership.sqlite3 < setup_dealership.sql

INSERT INTO dealerships (name, location, capacity)
SELECT 'Car Dealership', 'New York', 50
WHERE NOT EXISTS (SELECT 1 FROM dealerships);