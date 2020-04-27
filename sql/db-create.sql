SET NAMES utf8;

DROP DATABASE IF EXISTS providerdb;
CREATE DATABASE providerdb CHARACTER SET utf8 COLLATE utf8_bin;
USE providerdb;

-- ---------------------- USER ROLES ---------------------- --
CREATE TABLE roles (
	id INTEGER NOT NULL PRIMARY KEY,
    name VARCHAR(10) NOT NULL UNIQUE
);

INSERT INTO roles (id, name) VALUES (0, 'Admin');
INSERT INTO roles (id, name) VALUES (1, 'Customer');
-- ---------------------- USER ROLES ---------------------- --

-- ---------------------- USER STATUSES ---------------------- --
CREATE TABLE statuses (
	id INTEGER NOT NULL PRIMARY KEY,
	name VARCHAR(10) NOT NULL UNIQUE
);

INSERT INTO statuses (id, name) VALUES (0, 'Normal');
INSERT INTO statuses (id, name) VALUES (1, 'Stopped');
INSERT INTO statuses (id, name) VALUES (2, 'Blocked');
-- ---------------------- USER STATUSES ---------------------- --

-- ---------------------- SERVICES ---------------------- --
CREATE TABLE services (
	id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL UNIQUE
);
-- ---------------------- SERVICES ---------------------- --

-- ---------------------- TARIFFS ---------------------- --
CREATE TABLE tariffs (
	id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL UNIQUE,
    connection_payment DECIMAL,
    month_payment DECIMAL,
    feature VARCHAR(50) NOT NULL,
    service_id INTEGER NOT NULL REFERENCES services(id) 
    ON DELETE CASCADE
    ON UPDATE CASCADE
);
-- ---------------------- TARIFFS ---------------------- --

-- ---------------------- USERS ---------------------- --
CREATE TABLE users (
	id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    login VARCHAR(20) NOT NULL UNIQUE,
    password TEXT NOT NULL,
    first_name VARCHAR(20) NOT NULL,
    last_name VARCHAR(20) NOT NULL,
    
    role_id INTEGER NOT NULL REFERENCES roles(id) 
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
   
    
    status_id INTEGER NOT NULL DEFAULT(1) REFERENCES statuses(id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
    
    account_state DECIMAL NOT NULL
);
-- ---------------------- USERS ---------------------- --

-- ---------------------- USER SERVICES ---------------------- --
CREATE TABLE user_services (
	id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    
    user_id INTEGER NOT NULL REFERENCES users(id) 
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    
    tariff_id INTEGER NOT NULL REFERENCES tariffs(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    
    address VARCHAR(130) DEFAULT 'no address',
    
    last_payment_date DATE NOT NULL 
);
-- ---------------------- USER SERVICES ---------------------- --

-- ---------------------- USER ORDERS ---------------------- --
CREATE TABLE user_orders (
	id INTEGER PRIMARY KEY AUTO_INCREMENT,
    
    user_id INTEGER NOT NULL REFERENCES users(id) 
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    
    tariff_id INTEGER NOT NULL REFERENCES tariffs(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    
    user_phone VARCHAR(20) NOT NULL,
    
    comment VARCHAR(80) DEFAULT 'no comment',
    
    order_date DATE NOT NULL
);
-- ---------------------- USER ORDERS ---------------------- --

-- ---------------------- USER DESCRIBES ---------------------- --
CREATE TABLE user_describes (
	id INTEGER PRIMARY KEY AUTO_INCREMENT,
    
    user_service_id INTEGER NOT NULL REFERENCES user_services(id) 
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    
    user_phone VARCHAR(20) NOT NULL,
    
    comment VARCHAR(80) DEFAULT 'no comment',
    
    describe_date DATE NOT NULL
);
-- ---------------------- USER DESCRIBES ---------------------- --


-- ---------------------- DB USERS ---------------------- --
DROP USER IF EXISTS 'localAdmin'@'localhost';
CREATE USER 'localAdmin'@'localhost' IDENTIFIED BY 'providerLocalPassword';
GRANT ALL PRIVILEGES ON  providerdb TO 'localAdmin'@'localhost';
GRANT ALL PRIVILEGES ON TABLE services TO 'localAdmin'@'localhost';
GRANT ALL PRIVILEGES ON TABLE tariffs TO 'localAdmin'@'localhost';
GRANT ALL PRIVILEGES ON TABLE users TO 'localAdmin'@'localhost';
GRANT ALL PRIVILEGES ON TABLE user_services TO 'localAdmin'@'localhost';
GRANT ALL PRIVILEGES ON TABLE user_orders TO 'localAdmin'@'localhost';
GRANT ALL PRIVILEGES ON TABLE user_describes TO 'localAdmin'@'localhost';
-- ---------------------- DB USERS ---------------------- --

-- ---------------------- INSERT  ----------------------- --

-- USERS --
INSERT INTO users VALUES (DEFAULT, 'admin', '21232f297a57a5a743894a0e4a801fc3', 'Максим', 'Верес', 0, 0, 10000);
INSERT INTO users VALUES (DEFAULT, 'user', 'ee11cbb19052e40b07aac0ca060c23ee', 'Максим', 'Верес', 1, 0, 100);

-- SERVICES --
INSERT INTO services VALUES (DEFAULT, 'Internet');
INSERT INTO services VALUES (DEFAULT, 'Mobile internet');
INSERT INTO services VALUES (DEFAULT, 'Cable TV');
INSERT INTO services VALUES (DEFAULT, 'IP-TV');

-- TARIFFS --

 -- INTERNET --
	INSERT INTO tariffs VALUES (DEFAULT, 'Ethernet-10MB/s', 100, 30, 'Internet provides speed up to 10Mbps', 1);
	INSERT INTO tariffs VALUES (DEFAULT, 'Ethernet-50MB/s', 100, 60, 'Internet provides speed up to 50Mbps', 1);
	INSERT INTO tariffs VALUES (DEFAULT, 'Ethernet-100MB/s', 120, 100, 'Internet provides speed up to 100Mbps', 1);
	INSERT INTO tariffs VALUES (DEFAULT, 'Ethernet-500MB/s', 150, 160, 'Internet provides speed up to 500Mbps', 1);
	INSERT INTO tariffs VALUES (DEFAULT, 'Ethernet-1000MB/s', 250, 225, 'Internet provides speed up to 1000Mbps', 1);
    
 -- MOBILE INTERNET	--
	INSERT INTO tariffs VALUES (DEFAULT, '3G-5000MB', 20, 35, '3G internet provides limit to 5000Mb per month', 2);
    INSERT INTO tariffs VALUES (DEFAULT, '3G 10000MB', 45, 65, '3G internet provides limit to 10000Mb per month', 2);
    INSERT INTO tariffs VALUES (DEFAULT, '3G unlimited', 100, 100, '3G internet provides no limits', 2);
    INSERT INTO tariffs VALUES (DEFAULT, '4G-5000MB', 45, 70, '4G internet provides limit to 5000Mb per month', 2);
    INSERT INTO tariffs VALUES (DEFAULT, '4G-10000MB', 60, 100, '4G internet provides limit to 10000Mb per month', 2);
    INSERT INTO tariffs VALUES (DEFAULT, '4G unlimited', 135, 130, '4G internet provides no limits', 2);
    
 -- Cable TV --
	INSERT INTO tariffs VALUES (DEFAULT, 'Cable TV 20 channels', 100, 60, 'Cable TV provides 20 channels', 3);
	INSERT INTO tariffs VALUES (DEFAULT, 'Cable TV 50 channels', 100, 100, 'Cable TV provides 50 channels', 3);
    
 -- IP TV --
	INSERT INTO tariffs VALUES (DEFAULT, 'IP-TV 20 channels', 100, 100, 'IP-TV provides 20 channels', 4);
	INSERT INTO tariffs VALUES (DEFAULT, 'IP-TV 50 channels', 100, 120, 'IP-TV provides 50 channels', 4);
    
-- USER SERVICES --
	INSERT INTO user_services VALUES (DEFAULT, 2, 5, 'Kharkiv, Svobody 39 459', CAST('2020.03.17' as DATE));
	INSERT INTO user_services VALUES (DEFAULT, 2, 11, DEFAULT, CAST('2020.03.15' as DATE));
	INSERT INTO user_services VALUES (DEFAULT, 2, 15, 'Kharkiv, Svobody 39 459', CAST('2020.03.12' as DATE));
	

-- ---------------------- INSERT  ----------------------- --


SELECT * FROM users;
SELECT * FROM services;
SELECT * FROM tariffs;
SELECT * FROM user_services;
SELECT * FROM user_orders;
SELECT * FROM user_describes;