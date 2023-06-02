
CREATE SCHEMA IF NOT EXISTS `BedBreakfast` ;
USE `BedBreakfast` ;


CREATE TABLE IF NOT EXISTS `BedBreakfast`.`BedAndBreakfast` (
  `Id` INT NOT NULL,
  `Name` VARCHAR(45) NULL,
  `CheckOutTime` DATETIME NULL,
  `CheckInTime` DATETIME NULL,
  `Direction` VARCHAR(45) NULL,
  `Email` VARCHAR(45) NULL,
  `PhoneNumber` VARCHAR(15) NULL,
  PRIMARY KEY (`Id`));


CREATE TABLE IF NOT EXISTS `BedBreakfast`.`Rooms` (
  `Id` INT NOT NULL,
  `Type` VARCHAR(45) NULL,
  `Number` VARCHAR(10) NULL,
  `Availability` VARCHAR(10) NULL,
  `Description` VARCHAR(45) NULL,
  `Beds` INT NULL,
  `Images` VARCHAR(45) NULL,
  `Price` INT NULL,
  `BedAndBreakfastId` INT NULL,
  PRIMARY KEY (`Id`),
  INDEX `BedAndBreakfastId_idx` (`BedAndBreakfastId` ASC) VISIBLE,
  CONSTRAINT `BedAndBreakfastRooms`
    FOREIGN KEY (`BedAndBreakfastId`)
    REFERENCES `BedBreakfast`.`BedAndBreakfast` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


CREATE TABLE IF NOT EXISTS `BedBreakfast`.`Guest` (
  `Id` INT NOT NULL,
  `Name` VARCHAR(45) NULL,
  `Email` VARCHAR(45) NULL,
  `PhoneNumber` VARCHAR(15) NULL,
  PRIMARY KEY (`Id`));


CREATE TABLE IF NOT EXISTS `BedBreakfast`.`Reservation` (
  `Id` INT NOT NULL,
  `RoomId` INT NULL,
  `GuestId` INT NULL,
  `CheckInDate` DATETIME NULL,
  `CheckOutDate` DATETIME NULL,
  `Status` VARCHAR(10) NULL,
  `ReservationDate` DATETIME NULL,
  `Amount` INT NULL,
  `BedAndBreakfastId` INT NULL,
  PRIMARY KEY (`Id`),
  INDEX `BedAndBreakfast_idx` (`BedAndBreakfastId` ASC) VISIBLE,
  INDEX `Room_idx` (`RoomId` ASC) VISIBLE,
  INDEX `Guest_idx` (`GuestId` ASC) VISIBLE,
  CONSTRAINT `BedAndBreakfastReservation`
    FOREIGN KEY (`BedAndBreakfastId`)
    REFERENCES `BedBreakfast`.`BedAndBreakfast` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `Room`
    FOREIGN KEY (`RoomId`)
    REFERENCES `BedBreakfast`.`Rooms` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `GuestReservation`
    FOREIGN KEY (`GuestId`)
    REFERENCES `BedBreakfast`.`Guest` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


CREATE TABLE IF NOT EXISTS `BedBreakfast`.`Payment` (
  `Id` INT NOT NULL,
  `Date` DATETIME NULL,
  `Method` VARCHAR(25) NULL,
  `BillingStatus` VARCHAR(15) NULL,
  `Billing Address` VARCHAR(45) NULL,
  `Amount` INT NULL,
  `ReservationId` INT NULL,
  `GuestId` INT NULL,
  PRIMARY KEY (`Id`),
  INDEX `Guest_idx` (`GuestId` ASC) VISIBLE,
  INDEX `Reservation_idx` (`ReservationId` ASC) VISIBLE,
  CONSTRAINT `GuestPayment`
    FOREIGN KEY (`GuestId`)
    REFERENCES `BedBreakfast`.`Guest` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `Reservation`
    FOREIGN KEY (`ReservationId`)
    REFERENCES `BedBreakfast`.`Reservation` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


CREATE TABLE IF NOT EXISTS `BedBreakfast`.`Review` (
  `Id` INT NOT NULL,
  `Rating` INT NULL,
  `Experience` VARCHAR(45) NULL,
  `ReviewDate` DATETIME NULL,
  `GuestId` INT NULL,
  `BedAndBreakfastId` INT NULL,
  PRIMARY KEY (`Id`),
  INDEX `Guest_idx` (`GuestId` ASC) VISIBLE,
  INDEX `BedAndBreakfast_idx` (`BedAndBreakfastId` ASC) VISIBLE,
  CONSTRAINT `Guest`
    FOREIGN KEY (`GuestId`)
    REFERENCES `BedBreakfast`.`Guest` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `BedAndBreakfastReview`
    FOREIGN KEY (`BedAndBreakfastId`)
    REFERENCES `BedBreakfast`.`BedAndBreakfast` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


CREATE TABLE IF NOT EXISTS `BedBreakfast`.`ExternalLinks` (
  `Id` INT NOT NULL,
  `Link` VARCHAR(60) NULL,
  `Title` VARCHAR(45) NULL,
  `Description` VARCHAR(200) NULL,
  `Image` VARCHAR(45) NULL,
  `Type` VARCHAR(25) NULL,
  `BedAndBreakfastId` INT NULL,
  PRIMARY KEY (`Id`),
  INDEX `BedAndBreakast_idx` (`BedAndBreakfastId` ASC) VISIBLE,
  CONSTRAINT `BedAndBreakast`
    FOREIGN KEY (`BedAndBreakfastId`)
    REFERENCES `BedBreakfast`.`BedAndBreakfast` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


CREATE TABLE IF NOT EXISTS `BedBreakfast`.`FacilitiesCoupon` (
  `Id` INT NOT NULL,
  `Code` VARCHAR(45) NULL,
  `Type` VARCHAR(25) NULL,
  `Vendor` VARCHAR(25) NULL,
  `ValidUntil` DATETIME NULL,
  `Status` VARCHAR(25) NULL,
  `UsageLimit` INT NULL,
  `DiscountValue` INT NULL,
  `BedAndBreakfastId` INT NULL,
  PRIMARY KEY (`Id`),
  INDEX `BedAndBreakfast_idx` (`BedAndBreakfastId` ASC) VISIBLE,
  CONSTRAINT `BedAndBreakfastFacilitiesCoupon`
    FOREIGN KEY (`BedAndBreakfastId`)
    REFERENCES `BedBreakfast`.`BedAndBreakfast` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


CREATE TABLE IF NOT EXISTS `BedBreakfast`.`Employee` (
  `Id` INT NOT NULL,
  `Name` VARCHAR(45) NULL,
  `Email` VARCHAR(45) NULL,
  `PhoneNumber` VARCHAR(15) NULL,
  `Address` VARCHAR(45) NULL,
  `Designation` VARCHAR(35) NULL,
  `Salary` INT NULL,
  `BedAndBreakfastId` INT NULL,
  PRIMARY KEY (`Id`),
  INDEX `BedAndBreakfast_idx` (`BedAndBreakfastId` ASC) VISIBLE,
  CONSTRAINT `BedAndBreakfastEmployee`
    FOREIGN KEY (`BedAndBreakfastId`)
    REFERENCES `BedBreakfast`.`BedAndBreakfast` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


CREATE TABLE IF NOT EXISTS `BedBreakfast`.`BreakfastMenu` (
  `Id` INT NOT NULL,
  `Name` VARCHAR(45) NULL,
  `Description` VARCHAR(70) NULL,
  `DietaryRestriction` VARCHAR(45) NULL,
  `StartTime` DATETIME NULL,
  `EndTime` DATETIME NULL,
  `Price` INT NULL,
  `NutritionalValue` VARCHAR(45) NULL,
  `BedAndBreakfastId` INT NULL,
  PRIMARY KEY (`Id`),
  INDEX `BedAndBreakfast_idx` (`BedAndBreakfastId` ASC) VISIBLE,
  CONSTRAINT `BedAndBreakfastBreakfastMenu`
    FOREIGN KEY (`BedAndBreakfastId`)
    REFERENCES `BedBreakfast`.`BedAndBreakfast` (`Id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

