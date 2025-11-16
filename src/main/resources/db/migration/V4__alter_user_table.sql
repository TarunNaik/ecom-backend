-- Migration script to add an image_url column to the users table
ALTER TABLE users
ADD COLUMN IF NOT EXISTS image_url VARCHAR(255);
