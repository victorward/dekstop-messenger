create user 'Jcom'@'localhost' identified by 'java';
create user 'Jcom'@'%' identified by 'java';
create database ZaawJava;
GRANT ALL PRIVILEGES ON * . * TO 'Jcom'@'localhost';
GRANT ALL PRIVILEGES ON * . * TO 'Jcom'@'%';
SET GLOBAL time_zone = '+2:00';
