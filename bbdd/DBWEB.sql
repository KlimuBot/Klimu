use climu;
show tables;

drop database if exists climu;
create database climu;
GRANT ALL
    ON climu.*
    TO 'admin'@'%';
USE climu;

create table User
(
    userID     INT AUTO_INCREMENT,
    name       VARCHAR(20)        NOT NULL,
    surname    VARCHAR(30)        NOT NULL,
    actualTown VARCHAR(30)        NOT NULL,
    email      VARCHAR(30) UNIQUE NOT NULL,
    tlf        VARCHAR(9) UNIQUE  NOT NULL,
    username   VARCHAR(20) UNIQUE NOT NULL,
    password   VARCHAR(255)       NOT NULL,
    CONSTRAINT user_PK PRIMARY KEY (userID)
);

create table Alertas
(
    alertID     INT AUTO_INCREMENT,
    userID      INT,
    diarias     boolean default 0,
    horaDiaria  DATETIME,
    adversas    boolean default 1,
    geologicas  boolean default 1,
    temporales  boolean default 1,
    temp_inicio date,
    temp_fin    date,
    CONSTRAINT alert_PK PRIMARY KEY (alertID),
    CONSTRAINT alert_FK FOREIGN KEY (userID)
        REFERENCES User (userID)
);