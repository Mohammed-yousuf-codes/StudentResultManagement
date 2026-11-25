CREATE DATABASE IF NOT EXISTS student_result_db
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE student_result_db;

CREATE TABLE IF NOT EXISTS admin_users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    roll_no VARCHAR(30) NOT NULL UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(120),
    department VARCHAR(80) NOT NULL,
    semester INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS subjects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    subject_code VARCHAR(20) NOT NULL UNIQUE,
    subject_name VARCHAR(100) NOT NULL,
    max_marks INT NOT NULL DEFAULT 100
);

CREATE TABLE IF NOT EXISTS marks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    subject_id INT NOT NULL,
    marks_obtained DECIMAL(5,2) NOT NULL,
    UNIQUE KEY uq_student_subject (student_id, subject_id),
    CONSTRAINT fk_marks_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_marks_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
);

INSERT IGNORE INTO admin_users(username, password) VALUES ('admin', 'admin123');

INSERT IGNORE INTO subjects(subject_code, subject_name, max_marks) VALUES
('JAVA101', 'Java Programming', 100),
('DBMS102', 'Database Management Systems', 100),
('WEB103', 'Web Technologies', 100),
('OS104', 'Operating Systems', 100),
('MATH105', 'Mathematics', 100);

INSERT IGNORE INTO students(roll_no, full_name, email, department, semester) VALUES
('STU001', 'Aarav Kumar', 'aarav@example.com', 'Computer Science', 4),
('STU002', 'Priya Reddy', 'priya@example.com', 'Computer Science', 4),
('STU003', 'Rahul Sharma', 'rahul@example.com', 'Information Technology', 4);

INSERT IGNORE INTO marks(student_id, subject_id, marks_obtained)
SELECT s.id, sub.id,
       CASE
         WHEN s.roll_no='STU001' THEN 82
         WHEN s.roll_no='STU002' THEN 91
         ELSE 67
       END
FROM students s CROSS JOIN subjects sub;
