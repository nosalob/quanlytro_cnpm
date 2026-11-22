

CREATE DATABASE IF NOT EXISTS quan_ly_tro_cnpm
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE quan_ly_tro_cnpm;



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


CREATE TABLE tblBuilding (
                             id INT NOT NULL AUTO_INCREMENT,
                             code VARCHAR(255) NOT NULL,
                             name VARCHAR(255) NOT NULL,
                             address VARCHAR(255) NOT NULL,
                             status VARCHAR(255) NOT NULL,
                             CONSTRAINT pk_tblBuilding PRIMARY KEY (id)
) ENGINE=InnoDB;


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
                                 tblUserid INT NULL,
                                 CONSTRAINT pk_tblMeterReading PRIMARY KEY (id),
                                 CONSTRAINT fk_tblMeterReading_tblRoom
                                     FOREIGN KEY (tblRoomid) REFERENCES tblRoom(id)
                                         ON UPDATE CASCADE ON DELETE RESTRICT,
                                 CONSTRAINT fk_tblMeterReading_tblResident
                                     FOREIGN KEY (tblResidentid) REFERENCES tblResident(idCardNo)
                                         ON UPDATE CASCADE ON DELETE RESTRICT,
                                 CONSTRAINT fk_tblMeterReading_tblReviewer
                                     FOREIGN KEY (tblUserid) REFERENCES tblUser(id)
                                         ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;


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


-- Stored procedures merged from the supplied database script.
DELIMITER $$

CREATE PROCEDURE spGetActiveBuildings()
BEGIN
    SELECT id, code, name, address, status
    FROM tblBuilding
    WHERE status = 'ACTIVE'
    ORDER BY name;
END$$

CREATE PROCEDURE spSearchVacantRoom(IN pBuildingId INT, IN pKeyword VARCHAR(255))
BEGIN
    SELECT id, code, area, rentPrice, depositAmount, maxOccupants, status
    FROM tblRoom
    WHERE tblBuildingid = pBuildingId
      AND status = 'VACANT'
      AND code LIKE CONCAT('%', pKeyword, '%')
    ORDER BY code;
END$$

CREATE PROCEDURE spCountRoomConflicts(IN pRoomId INT, IN pStart DATE, IN pEnd DATE)
BEGIN
    SELECT COUNT(*) AS conflicts
    FROM tblContract c
    JOIN tblContractTerm t ON t.tblContractid = c.id
    WHERE c.tblRoomid = pRoomId
      AND c.status = 'ACTIVE'
      AND t.startDate <= pEnd
      AND t.endDate >= pStart;
END$$

-- This project's tblResident.idCardNo is the resident identifier;
-- tblUserid links it to tblUser.
CREATE PROCEDURE spSearchResident(IN pIdCardNo VARCHAR(255))
BEGIN
    SELECT u.id, u.username, u.passwordHash, u.fullName, u.phone, u.email, u.role, u.status,
           r.idCardNo, r.dateOfBirth, r.hometown, r.note
    FROM tblResident r
    JOIN tblUser u ON u.id = r.tblUserid
    WHERE r.idCardNo = pIdCardNo;
END$$

DELIMITER ;


-- Sample data merged from verify_meter_reading.sql and
-- sample_incident_ticket.sql. It uses this project's schema:
-- tblResidentid is an ID-card number and meter readings store invoiceValue.
-- These inserts can be run again without duplicating the sample rows.

INSERT INTO tblUser (username, passwordHash, fullName, phone, email, role, status)
SELECT 'chutro01', SHA2('123456', 256), 'Nguyen Van A', '0900000001',
       'chutro01@example.com', 'LANDLORD', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM tblUser WHERE username = 'chutro01');

INSERT INTO tblUser (username, passwordHash, fullName, phone, email, role, status)
SELECT 'nguoithue01', SHA2('123456', 256), 'Tran Thi B', '0900000002',
       'nguoithue01@example.com', 'TENANT', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM tblUser WHERE username = 'nguoithue01');

INSERT INTO tblUser (username, passwordHash, fullName, phone, email, role, status)
SELECT 'tenant_sc', SHA2('123456', 256), 'Nguyen Van B', '0900000003',
       'tenant_sc@mail.com', 'TENANT', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM tblUser WHERE username = 'tenant_sc');

INSERT INTO tblResident (idCardNo, dateOfBirth, hometown, note, tblUserid)
SELECT '001099000001', '1999-05-20', 'Ha Noi', NULL, u.id
FROM tblUser u
WHERE u.username = 'nguoithue01'
  AND NOT EXISTS (SELECT 1 FROM tblResident WHERE idCardNo = '001099000001');

INSERT INTO tblResident (idCardNo, dateOfBirth, hometown, note, tblUserid)
SELECT '001200000001', '2000-01-01', 'Ha Noi', NULL, u.id
FROM tblUser u
WHERE u.username = 'tenant_sc'
  AND NOT EXISTS (SELECT 1 FROM tblResident WHERE idCardNo = '001200000001');

INSERT INTO tblBuilding (code, name, address, status)
SELECT 'NT01', 'Nha tro Hanh Phuc', '12 Xuan Thuy, Cau Giay, Ha Noi', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM tblBuilding WHERE code = 'NT01');

INSERT INTO tblBuilding (code, name, address, status)
SELECT 'BLD-SC', 'Building A', 'Ha Noi', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM tblBuilding WHERE code = 'BLD-SC');

INSERT INTO tblRoom (code, area, rentPrice, depositAmount, maxOccupants, status, tblBuildingid)
SELECT 'P101', 25, 3000000, 3000000, 2, 'OCCUPIED', b.id
FROM tblBuilding b
WHERE b.code = 'NT01'
  AND NOT EXISTS (SELECT 1 FROM tblRoom r WHERE r.code = 'P101' AND r.tblBuildingid = b.id);

INSERT INTO tblRoom (code, area, rentPrice, depositAmount, maxOccupants, status, tblBuildingid)
SELECT 'P102', 20, 2500000, 2500000, 2, 'OCCUPIED', b.id
FROM tblBuilding b
WHERE b.code = 'NT01'
  AND NOT EXISTS (SELECT 1 FROM tblRoom r WHERE r.code = 'P102' AND r.tblBuildingid = b.id);

INSERT INTO tblRoom (code, area, rentPrice, depositAmount, maxOccupants, status, tblBuildingid)
SELECT 'P.101', 25, 3000000, 5000000, 2, 'OCCUPIED', b.id
FROM tblBuilding b
WHERE b.code = 'BLD-SC'
  AND NOT EXISTS (SELECT 1 FROM tblRoom r WHERE r.code = 'P.101' AND r.tblBuildingid = b.id);

INSERT INTO tblMeterReading
    (month, year, type, previousValue, currentValue, evidenceImageUrl, invoiceValue,
     status, reviewNote, submittedAt, reviewedAt, tblRoomid, tblResidentid, tblUserid)
SELECT 11, 2026, 'ELECTRIC', 1240, 1318, NULL, 1315, 'PENDING', NULL,
       '2026-11-05', NULL, r.id, '001099000001', NULL
FROM tblRoom r JOIN tblBuilding b ON b.id = r.tblBuildingid
WHERE b.code = 'NT01' AND r.code = 'P101'
  AND NOT EXISTS (SELECT 1 FROM tblMeterReading m
                  WHERE m.month = 11 AND m.year = 2026 AND m.type = 'ELECTRIC'
                    AND m.tblRoomid = r.id AND m.tblResidentid = '001099000001');

INSERT INTO tblMeterReading
    (month, year, type, previousValue, currentValue, evidenceImageUrl, invoiceValue,
     status, reviewNote, submittedAt, reviewedAt, tblRoomid, tblResidentid, tblUserid)
SELECT 11, 2026, 'WATER', 85, 92, NULL, NULL, 'PENDING', NULL,
       '2026-11-05', NULL, r.id, '001099000001', NULL
FROM tblRoom r JOIN tblBuilding b ON b.id = r.tblBuildingid
WHERE b.code = 'NT01' AND r.code = 'P101'
  AND NOT EXISTS (SELECT 1 FROM tblMeterReading m
                  WHERE m.month = 11 AND m.year = 2026 AND m.type = 'WATER'
                    AND m.tblRoomid = r.id AND m.tblResidentid = '001099000001');

INSERT INTO tblMeterReading
    (month, year, type, previousValue, currentValue, evidenceImageUrl, invoiceValue,
     status, reviewNote, submittedAt, reviewedAt, tblRoomid, tblResidentid, tblUserid)
SELECT 11, 2026, 'ELECTRIC', 980, 1105, NULL, 1090, 'PENDING', NULL,
       '2026-11-05', NULL, r.id, '001099000001', NULL
FROM tblRoom r JOIN tblBuilding b ON b.id = r.tblBuildingid
WHERE b.code = 'NT01' AND r.code = 'P102'
  AND NOT EXISTS (SELECT 1 FROM tblMeterReading m
                  WHERE m.month = 11 AND m.year = 2026 AND m.type = 'ELECTRIC'
                    AND m.tblRoomid = r.id AND m.tblResidentid = '001099000001');

INSERT INTO tblContract (code, depositAmount, status, createdAt, tblRoomid)
SELECT 'HD-0123', 5000000, 'ACTIVE', '2026-01-01', r.id
FROM tblRoom r JOIN tblBuilding b ON b.id = r.tblBuildingid
WHERE b.code = 'BLD-SC' AND r.code = 'P.101'
  AND NOT EXISTS (SELECT 1 FROM tblContract WHERE code = 'HD-0123');

INSERT INTO tblContractTerm (termNo, startDate, endDate, rentPrice, status, tblContractid)
SELECT 1, '2026-01-01', '2026-12-31', 3000000, 'ACTIVE', c.id
FROM tblContract c
WHERE c.code = 'HD-0123'
  AND NOT EXISTS (SELECT 1 FROM tblContractTerm WHERE tblContractid = c.id AND termNo = 1);

INSERT INTO tblOccupancy (isRepresentative, moveInDate, moveOutDate, tblContractid, tblResidentid)
SELECT 1, '2026-01-01', NULL, c.id, '001200000001'
FROM tblContract c
WHERE c.code = 'HD-0123'
  AND NOT EXISTS (SELECT 1 FROM tblOccupancy
                  WHERE tblContractid = c.id AND tblResidentid = '001200000001');

INSERT INTO tblIncidentTicket
    (code, title, description, status, faultType, createdAt, tblRoomid, tblResidentid)
SELECT 'TK-031', 'Leaking faucet', 'Bathroom faucet leaks continuously', 'NEW', 'NONE',
       '2026-11-20', r.id, '001200000001'
FROM tblRoom r JOIN tblBuilding b ON b.id = r.tblBuildingid
WHERE b.code = 'BLD-SC' AND r.code = 'P.101'
  AND NOT EXISTS (SELECT 1 FROM tblIncidentTicket WHERE code = 'TK-031');

