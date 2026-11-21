-- =========================================================
-- DATABASE: quan_ly_tro_cnpm
-- Generated from the supplied Visual Paradigm ERD image.
-- Database name: quan_ly_tro_cnpm.
-- MySQL 8.x / InnoDB / utf8mb4
--
-- Notes:
-- 1. Column names and data types follow the ERD.
-- 2. INT(10) and FLOAT(10) are represented as INT and FLOAT.
--    The display width in INT(10) does not affect the stored range.
-- 3. The ERD does not clearly show a primary-key marker for
--    tblResident and tblReadingVerification. The keys below are
--    inferred: tblResident.idCardNo and tblReadingVerification.reason.
--    Verify these two choices against the original Visual Paradigm model.
-- =========================================================

CREATE DATABASE IF NOT EXISTS quan_ly_tro_cnpm
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE quan_ly_tro_cnpm;

-- Script không DROP bảng để tránh xóa dữ liệu hiện có.

-- ---------------------------------------------------------
-- 1. User
-- ---------------------------------------------------------
CREATE TABLE tblUser (
                         id INT NOT NULL AUTO_INCREMENT,
                         username VARCHAR(255) NOT NULL,
                         passwordHash VARCHAR(255) NOT NULL,
                         fullName VARCHAR(255) NOT NULL,
                         phone VARCHAR(255) NOT NULL,
                         email VARCHAR(255) NOT NULL,
                         role VARCHAR(255) NOT NULL,
                         status VARCHAR(255) NOT NULL,
                         CONSTRAINT pk_tblUser PRIMARY KEY (id),
                         CONSTRAINT uq_tblUser_username UNIQUE (username)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- 2. Resident
-- ---------------------------------------------------------
CREATE TABLE tblResident (
                             idCardNo VARCHAR(255) NOT NULL,
                             dateOfBirth DATE NOT NULL,
                             hometown VARCHAR(255) NOT NULL,
                             note VARCHAR(255) NULL,
                             tblUserid INT NOT NULL,
                             CONSTRAINT pk_tblResident PRIMARY KEY (idCardNo),
                             CONSTRAINT fk_tblResident_tblUser
                                 FOREIGN KEY (tblUserid) REFERENCES tblUser(id)
                                     ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- 3. Building
-- ---------------------------------------------------------
CREATE TABLE tblBuilding (
                             id INT NOT NULL AUTO_INCREMENT,
                             code VARCHAR(255) NOT NULL,
                             name VARCHAR(255) NOT NULL,
                             address VARCHAR(255) NOT NULL,
                             status VARCHAR(255) NOT NULL,
                             CONSTRAINT pk_tblBuilding PRIMARY KEY (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- 4. Room
-- ---------------------------------------------------------
CREATE TABLE tblRoom (
                         id INT NOT NULL AUTO_INCREMENT,
                         code VARCHAR(255) NOT NULL,
                         area FLOAT NOT NULL,
                         rentPrice FLOAT NOT NULL,
                         depositAmount FLOAT NOT NULL,
                         maxOccupants INT NOT NULL,
                         status VARCHAR(255) NOT NULL,
                         tblBuildingid INT NOT NULL,
                         CONSTRAINT pk_tblRoom PRIMARY KEY (id),
                         CONSTRAINT fk_tblRoom_tblBuilding
                             FOREIGN KEY (tblBuildingid) REFERENCES tblBuilding(id)
                                 ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- 5. Equipment
-- ---------------------------------------------------------
CREATE TABLE tblEquipment (
                              id INT NOT NULL AUTO_INCREMENT,
                              name VARCHAR(255) NOT NULL,
                              `condition` VARCHAR(255) NOT NULL,
                              quantity INT NOT NULL,
                              tblRoomid INT NOT NULL,
                              CONSTRAINT pk_tblEquipment PRIMARY KEY (id),
                              CONSTRAINT fk_tblEquipment_tblRoom
                                  FOREIGN KEY (tblRoomid) REFERENCES tblRoom(id)
                                      ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- 6. Contract
-- ---------------------------------------------------------
CREATE TABLE tblContract (
                             id INT NOT NULL AUTO_INCREMENT,
                             code VARCHAR(255) NOT NULL,
                             depositAmount FLOAT NOT NULL,
                             status VARCHAR(255) NOT NULL,
                             createdAt DATE NOT NULL,
                             terminatedAt DATE NULL,
                             terminationType VARCHAR(255) NULL,
                             tblRoomid INT NOT NULL,
                             CONSTRAINT pk_tblContract PRIMARY KEY (id),
                             CONSTRAINT fk_tblContract_tblRoom
                                 FOREIGN KEY (tblRoomid) REFERENCES tblRoom(id)
                                     ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- 7. Contract term
-- ---------------------------------------------------------
CREATE TABLE tblContractTerm (
                                 id INT NOT NULL AUTO_INCREMENT,
                                 termNo INT NOT NULL,
                                 startDate DATE NOT NULL,
                                 endDate DATE NOT NULL,
                                 rentPrice FLOAT NOT NULL,
                                 status VARCHAR(255) NOT NULL,
                                 tblContractid INT NOT NULL,
                                 CONSTRAINT pk_tblContractTerm PRIMARY KEY (id),
                                 CONSTRAINT fk_tblContractTerm_tblContract
                                     FOREIGN KEY (tblContractid) REFERENCES tblContract(id)
                                         ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- 8. Occupancy
-- ---------------------------------------------------------
CREATE TABLE tblOccupancy (
                              id INT NOT NULL AUTO_INCREMENT,
                              isRepresentative INT NOT NULL,
                              moveInDate DATE NOT NULL,
                              moveOutDate DATE NULL,
                              tblContractid INT NOT NULL,
                              tblResidentid VARCHAR(255) NOT NULL,
                              CONSTRAINT pk_tblOccupancy PRIMARY KEY (id),
                              CONSTRAINT fk_tblOccupancy_tblContract
                                  FOREIGN KEY (tblContractid) REFERENCES tblContract(id)
                                      ON UPDATE CASCADE ON DELETE RESTRICT,
                              CONSTRAINT fk_tblOccupancy_tblResident
                                  FOREIGN KEY (tblResidentid) REFERENCES tblResident(idCardNo)
                                      ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- 9. Invoice
-- ---------------------------------------------------------
CREATE TABLE tblInvoice (
                            id INT NOT NULL AUTO_INCREMENT,
                            code VARCHAR(255) NOT NULL,
                            month INT NOT NULL,
                            year INT NOT NULL,
                            dueDate DATE NOT NULL,
                            status VARCHAR(255) NOT NULL,
                            issuedAt DATE NOT NULL,
                            paidAt DATE NULL,
                            tblContractid INT NOT NULL,
                            CONSTRAINT pk_tblInvoice PRIMARY KEY (id),
                            CONSTRAINT fk_tblInvoice_tblContract
                                FOREIGN KEY (tblContractid) REFERENCES tblContract(id)
                                    ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- 10. Invoice item
-- ---------------------------------------------------------
CREATE TABLE tblInvoiceItem (
                                id INT NOT NULL AUTO_INCREMENT,
                                itemType VARCHAR(255) NOT NULL,
                                description VARCHAR(255) NULL,
                                quantity FLOAT NOT NULL,
                                unitPrice FLOAT NOT NULL,
                                tblInvoiceid INT NOT NULL,
                                CONSTRAINT pk_tblInvoiceItem PRIMARY KEY (id),
                                CONSTRAINT fk_tblInvoiceItem_tblInvoice
                                    FOREIGN KEY (tblInvoiceid) REFERENCES tblInvoice(id)
                                        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- 11. Payment proof
-- ---------------------------------------------------------
CREATE TABLE tblPaymentProof (
                                 id INT NOT NULL AUTO_INCREMENT,
                                 imageUrl VARCHAR(255) NOT NULL,
                                 declaredAmount FLOAT NOT NULL,
                                 transferDate DATE NOT NULL,
                                 note VARCHAR(255) NULL,
                                 status VARCHAR(255) NOT NULL,
                                 rejectReason VARCHAR(255) NULL,
                                 submittedAt DATE NOT NULL,
                                 reviewedAt DATE NULL,
                                 tblInvoiceid INT NOT NULL,
                                 tblUserid INT NOT NULL,
                                 CONSTRAINT pk_tblPaymentProof PRIMARY KEY (id),
                                 CONSTRAINT fk_tblPaymentProof_tblInvoice
                                     FOREIGN KEY (tblInvoiceid) REFERENCES tblInvoice(id)
                                         ON UPDATE CASCADE ON DELETE RESTRICT,
                                 CONSTRAINT fk_tblPaymentProof_tblUser
                                     FOREIGN KEY (tblUserid) REFERENCES tblUser(id)
                                         ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- 12. Meter reading
-- ---------------------------------------------------------
CREATE TABLE tblMeterReading (
                                 id INT NOT NULL AUTO_INCREMENT,
                                 month INT NOT NULL,
                                 year INT NOT NULL,
                                 type VARCHAR(255) NOT NULL,
                                 previousValue FLOAT NOT NULL,
                                 currentValue FLOAT NOT NULL,
                                 evidenceImageUrl VARCHAR(255) NULL,
                                 invoiceValue FLOAT NULL,
                                 status VARCHAR(255) NOT NULL,
                                 reviewNote VARCHAR(255) NULL,
                                 submittedAt DATE NOT NULL,
                                 reviewedAt DATE NULL,
                                 tblRoomid INT NOT NULL,
                                 tblResidentid VARCHAR(255) NOT NULL,
                                 CONSTRAINT pk_tblMeterReading PRIMARY KEY (id),
                                 CONSTRAINT fk_tblMeterReading_tblRoom
                                     FOREIGN KEY (tblRoomid) REFERENCES tblRoom(id)
                                         ON UPDATE CASCADE ON DELETE RESTRICT,
                                 CONSTRAINT fk_tblMeterReading_tblResident
                                     FOREIGN KEY (tblResidentid) REFERENCES tblResident(idCardNo)
                                         ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- 13. Reading verification
-- ---------------------------------------------------------
CREATE TABLE tblReadingVerification (
                                        reason VARCHAR(255) NOT NULL,
                                        method VARCHAR(255) NOT NULL,
                                        status VARCHAR(255) NOT NULL,
                                        result VARCHAR(255) NULL,
                                        requestedAt DATE NOT NULL,
                                        closedAt DATE NULL,
                                        tblMeterReadingid INT NOT NULL,
                                        CONSTRAINT pk_tblReadingVerification PRIMARY KEY (reason),
                                        CONSTRAINT fk_tblReadingVerification_tblMeterReading
                                            FOREIGN KEY (tblMeterReadingid) REFERENCES tblMeterReading(id)
                                                ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- 14. Incident ticket
-- ---------------------------------------------------------
CREATE TABLE tblIncidentTicket (
                                   id INT NOT NULL AUTO_INCREMENT,
                                   code VARCHAR(255) NOT NULL,
                                   title VARCHAR(255) NOT NULL,
                                   description VARCHAR(255) NOT NULL,
                                   imageUrl VARCHAR(255) NULL,
                                   status VARCHAR(255) NOT NULL,
                                   faultType VARCHAR(255) NOT NULL,
                                   resolutionNote VARCHAR(255) NULL,
                                   createdAt DATE NOT NULL,
                                   resolvedAt DATE NULL,
                                   tblRoomid INT NOT NULL,
                                   tblEquipmentid INT NULL,
                                   tblResidentid VARCHAR(255) NOT NULL,
                                   CONSTRAINT pk_tblIncidentTicket PRIMARY KEY (id),
                                   CONSTRAINT fk_tblIncidentTicket_tblRoom
                                       FOREIGN KEY (tblRoomid) REFERENCES tblRoom(id)
                                           ON UPDATE CASCADE ON DELETE RESTRICT,
                                   CONSTRAINT fk_tblIncidentTicket_tblEquipment
                                       FOREIGN KEY (tblEquipmentid) REFERENCES tblEquipment(id)
                                           ON UPDATE CASCADE ON DELETE RESTRICT,
                                   CONSTRAINT fk_tblIncidentTicket_tblResident
                                       FOREIGN KEY (tblResidentid) REFERENCES tblResident(idCardNo)
                                           ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- 15. Compensation
-- ---------------------------------------------------------
CREATE TABLE tblCompensation (
                                 id INT NOT NULL AUTO_INCREMENT,
                                 amount FLOAT NOT NULL,
                                 reason VARCHAR(255) NOT NULL,
                                 method VARCHAR(255) NOT NULL,
                                 status VARCHAR(255) NOT NULL,
                                 createdAt DATE NOT NULL,
                                 settledAt DATE NULL,
                                 tblIncidentTicketid INT NOT NULL,
                                 CONSTRAINT pk_tblCompensation PRIMARY KEY (id),
                                 CONSTRAINT fk_tblCompensation_tblIncidentTicket
                                     FOREIGN KEY (tblIncidentTicketid) REFERENCES tblIncidentTicket(id)
                                         ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

