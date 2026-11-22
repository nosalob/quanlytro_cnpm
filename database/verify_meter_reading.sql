CREATE DATABASE IF NOT EXISTS boardinghouse CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE boardinghouse;

CREATE TABLE IF NOT EXISTS tblUser (
	id INT NOT NULL AUTO_INCREMENT,
	username VARCHAR(255) NOT NULL,
	passwordHash VARCHAR(255) NOT NULL,
	fullName VARCHAR(255) NOT NULL,
	phone VARCHAR(255) NOT NULL,
	email VARCHAR(255) NOT NULL,
	role VARCHAR(255) NOT NULL,
	status VARCHAR(255) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE KEY uq_user_username (username)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tblResident (
	idCardNo VARCHAR(255) NOT NULL,
	dateOfBirth DATE NOT NULL,
	hometown VARCHAR(255) NOT NULL,
	note VARCHAR(255) NULL,
	tblUserid INT NOT NULL,
	PRIMARY KEY (tblUserid),
	FOREIGN KEY (tblUserid) REFERENCES tblUser(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tblBuilding (
	id INT NOT NULL AUTO_INCREMENT,
	code VARCHAR(255) NOT NULL,
	name VARCHAR(255) NOT NULL,
	address VARCHAR(255) NOT NULL,
	status VARCHAR(255) NOT NULL,
	PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tblRoom (
	id INT NOT NULL AUTO_INCREMENT,
	code VARCHAR(255) NOT NULL,
	area FLOAT NOT NULL,
	rentPrice FLOAT NOT NULL,
	depositAmount FLOAT NOT NULL,
	maxOccupants INT NOT NULL,
	status VARCHAR(255) NOT NULL,
	tblBuildingid INT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (tblBuildingid) REFERENCES tblBuilding(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tblMeterReading (
	id INT NOT NULL AUTO_INCREMENT,
	`month` INT NOT NULL,
	`year` INT NOT NULL,
	`type` VARCHAR(255) NOT NULL,
	previousValue FLOAT NOT NULL,
	currentValue FLOAT NOT NULL,
	evidenceImageUrl VARCHAR(255) NULL,
	eInvoiceValue FLOAT NULL,
	status VARCHAR(255) NOT NULL,
	reviewNote VARCHAR(255) NULL,
	submittedAt DATE NOT NULL,
	reviewedAt DATE NULL,
	tblRoomid INT NOT NULL,
	tblResidentid INT NOT NULL,
	tblUserid INT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (tblRoomid) REFERENCES tblRoom(id),
	FOREIGN KEY (tblResidentid) REFERENCES tblResident(tblUserid),
	FOREIGN KEY (tblUserid) REFERENCES tblUser(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tblReadingVerification (
	id INT NOT NULL AUTO_INCREMENT,
	reason VARCHAR(255) NOT NULL,
	method VARCHAR(255) NOT NULL,
	status VARCHAR(255) NOT NULL,
	result VARCHAR(255) NULL,
	requestedAt DATE NOT NULL,
	closedAt DATE NULL,
	tblMeterReadingid INT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (tblMeterReadingid) REFERENCES tblMeterReading(id)
) ENGINE=InnoDB;


INSERT INTO tblUser(id, username, passwordHash, fullName, phone, email, role, status) VALUES
	(1, 'chutro01', SHA2('123456', 256), 'Nguyen Van A', '0900000001', 'chutro01@example.com', 'landlord', 'ACTIVE'),
	(2, 'nguoithue01', SHA2('123456', 256), 'Tran Thi B', '0900000002', 'nguoithue01@example.com', 'tenant', 'ACTIVE');

INSERT INTO tblResident(idCardNo, dateOfBirth, hometown, note, tblUserid) VALUES
	('001099000001', '1999-05-20', 'Ha Noi', NULL, 2);

INSERT INTO tblBuilding(id, code, name, address, status) VALUES
	(1, 'NT01', 'Nha tro Hanh Phuc', '12 Xuan Thuy, Cau Giay, Ha Noi', 'ACTIVE');

INSERT INTO tblRoom(id, code, area, rentPrice, depositAmount, maxOccupants, status, tblBuildingid) VALUES
	(1, 'P101', 25, 3000000, 3000000, 2, 'OCCUPIED', 1),
	(2, 'P102', 20, 2500000, 2500000, 2, 'OCCUPIED', 1);

INSERT INTO tblMeterReading(id, `month`, `year`, `type`, previousValue, currentValue, evidenceImageUrl, eInvoiceValue, status, reviewNote, submittedAt, reviewedAt, tblRoomid, tblResidentid, tblUserid) VALUES
	(1, 11, 2026, 'ELECTRIC', 1240, 1318, NULL, 1315, 'PENDING', NULL, '2026-11-05', NULL, 1, 2, NULL),
	(2, 11, 2026, 'WATER',      85,   92, NULL, NULL, 'PENDING', NULL, '2026-11-05', NULL, 1, 2, NULL),
	(3, 11, 2026, 'ELECTRIC',  980, 1105, NULL, 1090, 'PENDING', NULL, '2026-11-05', NULL, 2, 2, NULL);
