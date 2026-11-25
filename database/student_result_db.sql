-- Student Result and Grade Management System
-- Run the whole script in MySQL Workbench or:  mysql -u root -p < database/student_result_db.sql
-- Safe to re-run: existing rows are kept.

CREATE DATABASE IF NOT EXISTS student_result_db
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE student_result_db;

CREATE TABLE IF NOT EXISTS admin_users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL          -- PBKDF2 hash: pbkdf2$iterations$salt$hash
);
-- Older copies of this database used VARCHAR(100)
ALTER TABLE admin_users MODIFY password VARCHAR(255) NOT NULL;

CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    roll_no VARCHAR(30) NOT NULL UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(120),
    department VARCHAR(80) NOT NULL,
    semester INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_semester CHECK (semester BETWEEN 1 AND 8)
);

CREATE TABLE IF NOT EXISTS subjects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    subject_code VARCHAR(20) NOT NULL UNIQUE,
    subject_name VARCHAR(100) NOT NULL,
    max_marks INT NOT NULL DEFAULT 100,
    CONSTRAINT chk_max_marks CHECK (max_marks > 0)
);

CREATE TABLE IF NOT EXISTS marks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    subject_id INT NOT NULL,
    marks_obtained DECIMAL(6,2) NOT NULL,
    UNIQUE KEY uq_student_subject (student_id, subject_id),
    CONSTRAINT chk_marks_positive CHECK (marks_obtained >= 0),
    CONSTRAINT fk_marks_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_marks_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
);
ALTER TABLE marks MODIFY marks_obtained DECIMAL(6,2) NOT NULL;

-- Default login admin / admin123 (salted PBKDF2). Change it from the Password page after first sign-in.
INSERT IGNORE INTO admin_users(username, password) VALUES
('admin', 'pbkdf2$210000$/FFW/mN5ZTyHAi6I0Ta8+w==$wd1bro4dN9zLeTJ6BrpF1yrjpQAbTp2xBsRmT9U+ikw=');

INSERT IGNORE INTO subjects(subject_code, subject_name, max_marks) VALUES
('DBMS102', 'Database Management Systems', 100),
('JAVA101', 'Java Programming', 100),
('MATH105', 'Mathematics', 100),
('OS104',   'Operating Systems', 100),
('WEB103',  'Web Technologies', 100);

INSERT IGNORE INTO students(roll_no, full_name, email, department, semester) VALUES
('STU001', 'Aarav Kumar',    'aarav@example.com',  'Computer Science',       4),
('STU002', 'Priya Reddy',    'priya@example.com',  'Computer Science',       4),
('STU003', 'Rahul Sharma',   'rahul@example.com',  'Information Technology', 4),
('STU004', 'Fatima Shaikh',  'fatima@example.com', 'Information Technology', 4),
('STU005', 'Kiran Naidu',    NULL,                 'Electronics',            4),
('STU006', 'Sneha Iyer',     'sneha@example.com',  'Computer Science',       4);

-- Sample marks: STU001 A, STU002 A+, STU003 C, STU004 fails Maths, STU005 incomplete, STU006 pending.
INSERT IGNORE INTO marks(student_id, subject_id, marks_obtained)
SELECT s.id, sub.id, v.m
FROM (
          SELECT 'STU001' r, 'JAVA101' c, 84 m UNION ALL SELECT 'STU001','DBMS102',79 UNION ALL SELECT 'STU001','WEB103',88
UNION ALL SELECT 'STU001','OS104',81    UNION ALL SELECT 'STU001','MATH105',76
UNION ALL SELECT 'STU002','JAVA101',95  UNION ALL SELECT 'STU002','DBMS102',91 UNION ALL SELECT 'STU002','WEB103',93
UNION ALL SELECT 'STU002','OS104',88    UNION ALL SELECT 'STU002','MATH105',97
UNION ALL SELECT 'STU003','JAVA101',67  UNION ALL SELECT 'STU003','DBMS102',62 UNION ALL SELECT 'STU003','WEB103',71
UNION ALL SELECT 'STU003','OS104',58    UNION ALL SELECT 'STU003','MATH105',64
UNION ALL SELECT 'STU004','JAVA101',72  UNION ALL SELECT 'STU004','DBMS102',66 UNION ALL SELECT 'STU004','WEB103',80
UNION ALL SELECT 'STU004','OS104',55    UNION ALL SELECT 'STU004','MATH105',34
UNION ALL SELECT 'STU005','JAVA101',59  UNION ALL SELECT 'STU005','DBMS102',63 UNION ALL SELECT 'STU005','WEB103',70
) v
JOIN students s ON s.roll_no = v.r
JOIN subjects sub ON sub.subject_code = v.c;
