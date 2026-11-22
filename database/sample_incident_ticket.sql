USE quan_ly_tro_cnpm;

-- dữ liệu mẫu để test chức năng sự cố (chạy sau create_database.sql)
INSERT INTO tblUser(username, passwordHash, fullName, phone, email, role, status)
VALUES ('tenant_sc', '123', 'Nguyen Van B', '0900000002', 'tenant_sc@mail.com', 'TENANT', 'ACTIVE');

INSERT INTO tblBuilding(code, name, address, status)
VALUES ('BLD-SC', 'Building A', 'Ha Noi', 'ACTIVE');

INSERT INTO tblRoom(code, area, rentPrice, depositAmount, maxOccupants, status, tblBuildingid)
VALUES ('P.101', 25, 3000000, 5000000, 2, 'RENTED', LAST_INSERT_ID());

SET @roomId = LAST_INSERT_ID();

INSERT INTO tblContract(code, depositAmount, status, createdAt, tblRoomid)
VALUES ('HD-0123', 5000000, 'ACTIVE', '2026-01-01', @roomId);

INSERT INTO tblResident(idCardNo, dateOfBirth, hometown, note, tblUserid)
VALUES ('001200000001', '2000-01-01', 'Ha Noi', NULL, (SELECT id FROM tblUser WHERE username = 'tenant_sc'));

INSERT INTO tblIncidentTicket(code, title, description, status, faultType, createdAt, tblRoomid, tblResidentid)
VALUES ('TK-031', 'Leaking faucet', 'Bathroom faucet leaks continuously', 'NEW', 'NONE', '2026-11-20', @roomId, '001200000001');
