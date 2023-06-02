
CREATE SCHEMA IF NOT EXISTS `ParksNS` ;
USE `ParksNS` ;

CREATE TABLE IF NOT EXISTS `ParksNS`.`Parks` (
  `Id` INT NOT NULL,
  `Name` VARCHAR(45) NULL,
  `Description` VARCHAR(100) NULL,
  `Image` VARCHAR(50) NULL,
  `OpeningTime` DATETIME NULL,
  `ClosingTime` DATETIME NULL,
  `Location` VARCHAR(50) NULL,
  `Email` VARCHAR(30) NULL,
  `PhoneNumber` VARCHAR(15) NULL,
  PRIMARY KEY (`Id`));


CREATE TABLE IF NOT EXISTS `ParksNS`.`Sites` (
  `Id` INT NOT NULL,
  `Name` VARCHAR(45) NULL,
  `Status` VARCHAR(15) NULL,
  `Capacity` INT NULL,
  `Size` VARCHAR(10) NULL,
  `Firepit` INT NULL,
  `Barbeque` INT NULL,
  `ParkId` INT NOT NULL,
  PRIMARY KEY (`Id`),
  INDEX `siteParkId_idx` (`ParkId` ASC) VISIBLE,
  CONSTRAINT `siteParkId`
    FOREIGN KEY (`ParkId`)
    REFERENCES `ParksNS`.`Parks` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE TABLE IF NOT EXISTS `ParksNS`.`Map` (
  `Id` INT NOT NULL,
  `Link` VARCHAR(50) NULL,
  `Type` VARCHAR(15) NULL,
  `ParkId` INT NULL,
  PRIMARY KEY (`Id`),
  INDEX `MapParkId_idx` (`ParkId` ASC) VISIBLE,
  CONSTRAINT `MapParkId`
    FOREIGN KEY (`ParkId`)
    REFERENCES `ParksNS`.`Parks` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


CREATE TABLE IF NOT EXISTS `ParksNS`.`User` (
  `Id` INT NOT NULL,
  `Name` VARCHAR(45) NULL,
  `Email` VARCHAR(30) NULL,
  `PhoneNumber` VARCHAR(15) NULL,
  PRIMARY KEY (`Id`));

CREATE TABLE IF NOT EXISTS `ParksNS`.`Events` (
  `Id` INT NOT NULL,
  `Title` VARCHAR(45) NULL,
  `Description` VARCHAR(100) NULL,
  `ParkId` INT NULL,
  PRIMARY KEY (`Id`),
  INDEX `EventsParkId_idx` (`ParkId` ASC) VISIBLE,
  CONSTRAINT `EventsParkId`
    FOREIGN KEY (`ParkId`)
    REFERENCES `ParksNS`.`Parks` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


CREATE TABLE IF NOT EXISTS `ParksNS`.`Departments` (
  `Id` INT NOT NULL,
  `Name` VARCHAR(45) NULL,
  PRIMARY KEY (`Id`));


CREATE TABLE IF NOT EXISTS `ParksNS`.`Employment` (
  `Id` INT NOT NULL,
  `Title` VARCHAR(45) NULL,
  `DepartmentId` INT NULL,
  `ParkId` INT NULL,
  `Location` VARCHAR(45) NULL,
  `Deadline` DATETIME NULL,
  `Description` VARCHAR(100) NULL,
  PRIMARY KEY (`Id`),
  INDEX `EmploymentParkId_idx` (`ParkId` ASC) VISIBLE,
  INDEX `EmployementDepartmentId_idx` (`DepartmentId` ASC) VISIBLE,
  CONSTRAINT `EmploymentParkId`
    FOREIGN KEY (`ParkId`)
    REFERENCES `ParksNS`.`Parks` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `EmployementDepartmentId`
    FOREIGN KEY (`DepartmentId`)
    REFERENCES `ParksNS`.`Departments` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE TABLE IF NOT EXISTS `ParksNS`.`Equipments` (
  `Id` INT NOT NULL,
  `Description` VARCHAR(45) NULL,
  PRIMARY KEY (`Id`));


CREATE TABLE IF NOT EXISTS `ParksNS`.`Reservation` (
  `Id` INT NOT NULL,
  `ArrivalDate` DATETIME NULL,
  `DepartureDate` DATETIME NULL,
  `PartySize` INT NULL,
  `UserId` INT NULL,
  `SiteId` INT NULL,
  `ParkId` INT NULL,
  `EquipmentId` INT NULL,
  `ReservationDate` DATETIME NULL,
  PRIMARY KEY (`Id`),
  INDEX `UserReservationId_idx` (`UserId` ASC) VISIBLE,
  INDEX `ParksReservationId_idx` (`ParkId` ASC) VISIBLE,
  INDEX `EquipmentReservationId_idx` (`EquipmentId` ASC) VISIBLE,
  INDEX `SiteReservationId_idx` (`SiteId` ASC) VISIBLE,
  CONSTRAINT `UserReservationId`
    FOREIGN KEY (`UserId`)
    REFERENCES `ParksNS`.`User` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `ParksReservationId`
    FOREIGN KEY (`ParkId`)
    REFERENCES `ParksNS`.`Parks` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `SiteReservationId`
    FOREIGN KEY (`SiteId`)
    REFERENCES `ParksNS`.`Sites` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `EquipmentReservationId`
    FOREIGN KEY (`EquipmentId`)
    REFERENCES `ParksNS`.`Equipments` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


CREATE TABLE IF NOT EXISTS `ParksNS`.`Payment` (
  `Id` INT NOT NULL,
  `UserId` INT NULL,
  `Status` VARCHAR(15) NULL,
  `Amount` INT NULL,
  `ReservationId` INT NULL,
  `PaymentDate` DATETIME NULL,
  PRIMARY KEY (`Id`),
  INDEX `UserPaymentId_idx` (`UserId` ASC) VISIBLE,
  INDEX `ReservationPaymentId_idx` (`ReservationId` ASC) VISIBLE,
  CONSTRAINT `UserPaymentId`
    FOREIGN KEY (`UserId`)
    REFERENCES `ParksNS`.`User` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `ReservationPaymentId`
    FOREIGN KEY (`ReservationId`)
    REFERENCES `ParksNS`.`Reservation` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);
