SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

 CREATE SCHEMA IF NOT EXISTS customer_service DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
 USE customer_service ;

-- -----------------------------------------------------
-- Table customer_service.organization
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS customer_service.organization (
  id INT NOT NULL AUTO_INCREMENT,
  org_name VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE INDEX org_name_UNIQUE (org_name ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table customer_service.employee
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS customer_service.employee (
  id INT NOT NULL AUTO_INCREMENT,
  email VARCHAR(100) NOT NULL,
  contact_number VARCHAR(15) NOT NULL,
  country_code VARCHAR(5) NULL,
  department_id INT NULL,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  designation VARCHAR(50) NOT NULL,
  is_email_verified TINYINT(1) NOT NULL,
  password VARCHAR(150) NOT NULL,
  is_enabled TINYINT(1) NOT NULL DEFAULT true,
  is_locked TINYINT(1) NOT NULL DEFAULT false,
  is_credentials_expired TINYINT(1) NOT NULL,
  is_account_expired TINYINT(1) NOT NULL DEFAULT false,
  salt VARCHAR(45) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE INDEX email_UNIQUE (email ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table customer_service.org_employee
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS customer_service.org_employee (
  org_id INT NOT NULL,
  employee_id INT NOT NULL,
  INDEX fk_org_id_idx (org_id ASC),
  INDEX fk_emp_id_idx (employee_id ASC),
  CONSTRAINT fk_org_id
    FOREIGN KEY (org_id)
    REFERENCES customer_service.organization (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_emp_id
    FOREIGN KEY (employee_id)
    REFERENCES customer_service.employee (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table customer_service.department
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS customer_service.department (
  id INT NOT NULL AUTO_INCREMENT,
  dept_name VARCHAR(100) NOT NULL,
  lead_emp_id INT NULL,
  PRIMARY KEY (id),
  UNIQUE INDEX id_UNIQUE (id ASC),
  INDEX fk_lead_emp_id_idx (lead_emp_id ASC),
  CONSTRAINT fk_lead_emp_id
    FOREIGN KEY (lead_emp_id)
    REFERENCES customer_service.employee (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table customer_service.admin_user
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS customer_service.admin_user (
  id INT NOT NULL AUTO_INCREMENT,
  email VARCHAR(100) NOT NULL,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  is_email_verified TINYINT(1) NULL DEFAULT false,
  password VARCHAR(150) NOT NULL,
  is_enabled TINYINT(1) NULL DEFAULT true,
  is_locked TINYINT(1) NULL DEFAULT false,
  is_credentials_expired TINYINT(1) NULL DEFAULT false,
  is_account_expired TINYINT(1) NULL DEFAULT false,
  salt VARCHAR(45) NULL,
  PRIMARY KEY (id))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table customer_service.previlege
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS customer_service.previlege (
  id INT NOT NULL AUTO_INCREMENT,
  previlege_name VARCHAR(45) NOT NULL,
  PRIMARY KEY (id))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table customer_service.admin_user_previlege
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS customer_service.admin_user_previlege (
  admin_user_id INT NOT NULL,
  previlege_id INT NOT NULL,
  INDEX fk_admin_user_id_idx (admin_user_id ASC),
  INDEX fk_previlege_id_idx (previlege_id ASC),
  CONSTRAINT fk_admin_user_id
    FOREIGN KEY (admin_user_id)
    REFERENCES customer_service.admin_user (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_previlege_id
    FOREIGN KEY (previlege_id)
    REFERENCES customer_service.previlege (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;





-- -----------------------------------------------------
-- Table customer_service.customer
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS customer_service.customer (
  id INT NOT NULL AUTO_INCREMENT,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NULL,
  email VARCHAR(100) NOT NULL,
  contact_number VARCHAR(45) NOT NULL,
  country_code VARCHAR(6) NOT NULL,
  PRIMARY KEY (id))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table customer_service.incident
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS customer_service.incident (
  id INT NOT NULL AUTO_INCREMENT,
  short_description VARCHAR(250) NOT NULL,
  description VARCHAR(3000) NOT NULL,
  raised_date DATETIME NOT NULL,
  completion_date DATETIME NULL,
  remarks VARCHAR(500) NULL,
  incident_type VARCHAR(45) NOT NULL,
  incident_status VARCHAR(45) NULL,
  customer_id INT NOT NULL,
  PRIMARY KEY (id),
  INDEX fk_customer_id_idx (customer_id ASC),
  CONSTRAINT fk_customer_id
    FOREIGN KEY (customer_id)
    REFERENCES customer_service.customer (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table customer_service.attachment
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS customer_service.attachment (
  id INT NOT NULL AUTO_INCREMENT,
  attachment_file BLOB NOT NULL,
  file_name VARCHAR(100) NOT NULL,
  attached_date DATETIME NOT NULL,
  PRIMARY KEY (id))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table customer_service.incident_attachment
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS customer_service.incident_attachment (
  incident_id INT NOT NULL,
  attachment_id INT NOT NULL,
  INDEX fk_incident_id_idx (incident_id ASC),
  INDEX fk_attachment_id_idx (attachment_id ASC),
  CONSTRAINT fk_incident_id
    FOREIGN KEY (incident_id)
    REFERENCES customer_service.incident (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_attachment_id
    FOREIGN KEY (attachment_id)
    REFERENCES customer_service.attachment (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table customer_service.incident_history
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS customer_service.incident_history (
  id INT NOT NULL AUTO_INCREMENT,
  incident_id INT NOT NULL,
  change_done VARCHAR(150) NOT NULL,
  employee_id INT NOT NULL,
  PRIMARY KEY (id),
  INDEX fk_incident_history_idx (incident_id ASC),
  INDEX fk_incident_employee_idx (employee_id ASC),
  CONSTRAINT fk_incident_history
    FOREIGN KEY (incident_id)
    REFERENCES customer_service.incident (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_incident_employee
    FOREIGN KEY (employee_id)
    REFERENCES customer_service.employee (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS customer_service.org_department (
  org_id INT NOT NULL,
  dept_id INT NOT NULL,
  INDEX fk_org_id_idx (org_id ASC),
  INDEX fk_dept_id_idx (dept_id ASC),
  CONSTRAINT fk_org_id_1
    FOREIGN KEY (org_id)
    REFERENCES customer_service.organization (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_dept_id_1
    FOREIGN KEY (dept_id)
    REFERENCES customer_service.department (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS customer_service.emp_previlege (
  emp_id INT NOT NULL,
  previlege_id INT NOT NULL,
  INDEX fk_emp_id_idx (emp_id ASC),
  INDEX fk_previlege_id_idx (previlege_id ASC),
  CONSTRAINT fk_emp_id_1
    FOREIGN KEY (emp_id)
    REFERENCES customer_service.employee (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_previlege_id_1
    FOREIGN KEY (previlege_id)
    REFERENCES customer_service.previlege (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS customer_service.incident_route (
  id INT NOT NULL,
  incident_id INT NOT NULL,
  employee_id INT NOT NULL,
  assigned_date DATETIME NOT NULL,
  PRIMARY KEY (id),
  INDEX fk_incident_id_idx (incident_id ASC),
  INDEX fk_emp_id_idx (employee_id ASC),
  CONSTRAINT fk_incident_id_2
    FOREIGN KEY (incident_id)
    REFERENCES customer_service.incident (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_emp_id_2
    FOREIGN KEY (employee_id)
    REFERENCES customer_service.employee (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS customer_service.incident_rating (
  id INT NOT NULL AUTO_INCREMENT,
  incident_id INT NOT NULL,
  what_went_well VARCHAR(250) NULL,
  what_can_be_improved VARCHAR(300) NULL,
  rating_number INT NOT NULL,
  remarks VARCHAR(100) NULL,
  PRIMARY KEY (id),
  INDEX fk_incident_id_idx (incident_id ASC),
  CONSTRAINT fk_incident_id_3
    FOREIGN KEY (incident_id)
    REFERENCES customer_service.incident (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- Data
INSERT INTO previlege (previlege_name) values ('ROLE_CUSTOMER');
INSERT INTO previlege (previlege_name) values ('ROLE_EMPLOYEE');
INSERT INTO previlege (previlege_name) values ('ROLE_MANAGER');
INSERT INTO previlege (previlege_name) values ('ROLE_ADMIN');

-- ---------------------------------------------------------------
insert into admin_user(email,first_name,last_name,password) values ('deep90@gmail.com','Pradheep','P','TXlQYXNzd29yZDU0MzIxJA==');
-- ---------------------------------------------------------------

