# Hotel Room Booking System
developing a Hotel Room  Booking System using Java with a Graphical User Interface (GUI) and a database. The  system will help hotel staff manage room bookings and guest details effectively. 

A comprehensive hotel management system built with Java and MySQL for managing room bookings, guest information, and hotel operations.

## 🎯 Scope

The system is designed to:
- Maintain a database of hotel rooms and guest information
- Facilitate room booking, cancellation, and check-in/check-out processes
- Provide a simple and intuitive GUI for staff interactions

## 🎨 Features

### Room Management
- Add new rooms with details (room number, type, price)
- Edit existing room information
- Delete rooms from the system
- Search rooms by type, price range, or availability

### Booking Management
- Create new room bookings
- Cancel existing bookings
- View current and upcoming bookings

### Guest Management
- Record guest details (name, contact, ID proof)
- Edit guest information
- Manage guest records

### Reporting
Generate reports for:
- Currently occupied rooms
- Room availability for specific dates
- Current hotel guests

## 🛠 Technical Requirements

### System Requirements
- Java Runtime Environment (JRE) 8 or higher
- MySQL/SQLite database
- Operating System: Windows, macOS, or Linux

### Development Tools
- Programming Language: Java
- GUI Framework: Java Swing/AWT
- Database: MySQL/SQLite/PostgreSQL
- IDE: IntelliJ IDEA, Eclipse, or NetBeans

## 📊 Database Schema

### Rooms Table
| Column Name | Data Type | Description |
|-------------|-----------|-------------|
| room_id | INT (PK) | Unique identifier |
| room_number | VARCHAR(10) | Room number |
| room_type | VARCHAR(20) | Type (single/double/suite) |
| price | DECIMAL(10,2) | Price per night |
| availability | BOOLEAN | Availability status |

### Guests Table
| Column Name | Data Type | Description |
|-------------|-----------|-------------|
| guest_id | INT (PK) | Unique identifier |
| name | VARCHAR(255) | Guest's name |
| contact_info | VARCHAR(255) | Contact details |
| id_proof | VARCHAR(255) | ID proof details |

### Bookings Table
| Column Name | Data Type | Description |
|-------------|-----------|-------------|
| booking_id | INT (PK) | Unique booking ID |
| room_id | INT (FK) | Booked room ID |
| guest_id | INT (FK) | Booking guest ID |
| check_in_date | DATE | Check-in date |
| check_out_date | DATE | Check-out date |

## 📋 Non-Functional Requirements

### Usability
- User-friendly interface
- Minimal training required for staff

### Performance
- Support for 200+ rooms
- Handle 1000+ bookings efficiently

### Security
- Restricted admin access
- Secure database connections
- Protected guest information

### Scalability
- Support for future enhancements
- Payment system integration ready
- Online booking capability

## 📦 Deliverables

1. Source Code
   - Java source files
   - GUI design files
   - SQL database scripts

2. Documentation
   - User guide
   - Developer guide
   - Test cases and results

## 💯 Evaluation Criteria

| Criteria | Points |
|----------|---------|
| Functionality | 50 |
| Database | 20 |
| GUI Usability | 20 |
| Code Documentation | 10 |
| **Total** | **100** |
