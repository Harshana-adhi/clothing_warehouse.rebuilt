create database Clothing_Warehouse;
use Clothing_Warehouse;

CREATE TABLE Employee(
EmployeeId varchar(20) not null primary key,
EmpName varchar(30) not null,
TellNo varchar(20) not null,
Salary decimal(8,2),
EmpPosition varchar(25)
);

CREATE TABLE Supplier(
SupplierId varchar (20) not null primary key,
SupName varchar (30) not null,
TellNo varchar (15) not null
);

CREATE TABLE Customer (
CustomerId varchar(20) not null primary key,
CusName varchar(30) not null ,
Address varchar(100) not null,
TelNo varchar(20) not null
);

-- 4. ClothingItem (UPDATED: Removed 'Size' to allow multiple sizes per Item)
CREATE TABLE ClothingItem(
    ClothId VARCHAR(20) NOT NULL PRIMARY KEY,
    Material VARCHAR(20) NOT NULL,
    Category VARCHAR(15),
    CostPrice DECIMAL(8,2),       -- New column for cost price
    RetailPrice DECIMAL(8,2),     -- New column for retail price
    SupplierId VARCHAR(20),
    FOREIGN KEY (SupplierId) REFERENCES Supplier(SupplierId) ON DELETE SET NULL
);


-- 5. ClothingStock (NEW TABLE: Handles Size and Quantity management)
CREATE TABLE ClothingStock(
    StockId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    Color varchar(15) not null,
    ClothId varchar(20) not null,
    Size varchar (15) Not null,
    Quantity INT DEFAULT 0, -- Tracks remaining quantity
    FOREIGN KEY (ClothId) references ClothingItem(ClothId) ON DELETE CASCADE,
    UNIQUE(ClothId, Color, Size)
);

CREATE TABLE Payment(
PaymentId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
PayType varchar (15) not null,
Amount decimal(8,2) not null,
PayDate Date not null,
EmployeeId varchar(20), FOREIGN KEY (EmployeeId) references Employee (EmployeeId) ON DELETE SET NULL,
CustomerId varchar(20), FOREIGN KEY (CustomerId) references Customer (CustomerId) ON DELETE SET NULL
);

CREATE TABLE Billing (
BillId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
BillDate DATE NOT NULL,
Amount DECIMAL(8,2) NOT NULL,   -- total amount
BillDescription VARCHAR(250),
BillStatus VARCHAR(20),
PaymentId INT ,FOREIGN KEY (PaymentId) REFERENCES Payment(PaymentId) ON DELETE SET NULL,
CustomerId VARCHAR(20) ,FOREIGN KEY (CustomerId) REFERENCES Customer(CustomerId) ON DELETE SET NULL
);

CREATE TABLE BillDetails (
    BillDetailId INT AUTO_INCREMENT PRIMARY KEY,
    BillId INT NOT NULL, 
    StockId INT, -- Changed from ClothId to StockId to know WHICH size was sold
    Quantity INT NOT NULL,
    TotalAmount DECIMAL(8,2) NOT NULL,
    FOREIGN KEY (BillId) REFERENCES Billing(BillId) ON DELETE CASCADE,
    FOREIGN KEY (StockId) REFERENCES ClothingStock(StockId) ON DELETE SET NULL
);

CREATE TABLE Invoice (
InvoiceId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
issueDate date  not null,
BillId INT, FOREIGN KEY (BillId) references Billing (BillId) ON DELETE SET NULL
);

CREATE TABLE Refund ( 
RefundId INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
RefundDate date,
Amount decimal(8,2),
Reason varchar(200),
RefundMethod varchar(15),
CustomerId varchar(20), FOREIGN KEY (CustomerId) references Customer(CustomerId) ON DELETE SET NULL,
BillId INT, FOREIGN KEY (BillId) references Billing(BillId) ON DELETE SET NULL
);

CREATE TABLE Users (
    UserId INT AUTO_INCREMENT PRIMARY KEY,
    Username VARCHAR(50) UNIQUE NOT NULL,
    Password VARCHAR(255) NOT NULL,
    Role ENUM('Admin', 'Manager', 'Staff') NOT NULL,
    EmployeeId VARCHAR(20),  -- New column to link to Employee
    CONSTRAINT fk_employee FOREIGN KEY (EmployeeId) REFERENCES Employee(EmployeeId) ON DELETE CASCADE
);

