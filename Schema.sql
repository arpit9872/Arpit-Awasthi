CREATE DATABASE hostel_db;
USE hostel_db;

CREATE TABLE rooms (
    room_id INT PRIMARY KEY AUTO_INCREMENT,
    room_no VARCHAR(10) UNIQUE,
    floor INT,
    capacity INT
);

CREATE TABLE allotments (
    allot_id INT PRIMARY KEY AUTO_INCREMENT,
    room_id INT,
    student VARCHAR(100),
    allot_date DATE DEFAULT CURRENT_DATE,
    vacated BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);
