use ZaawJava;
create table ZaawJava.countries
(
id int auto_increment primary key,
country_name varchar(45)
);

create table ZaawJava.languages
(
id int auto_increment primary key,
language_name varchar(45)
);

create table ZaawJava.users
(
id int auto_increment primary key,
first_name varchar(20),
last_name varchar(20),
phone int,
gender enum('male','female'),
email varchar(50) NOT NULL UNIQUE,
user_password varchar(50) NOT NULL,
address varchar(50),
country_id int ,
birth_date date,
photo varchar(255),
foreign key (country_id) references ZaawJava.countries(id)
);

create table ZaawJava.users_languages
(
user_id int,
language_id int,
foreign key (user_id) references ZaawJava.users(id),
foreign key (language_id) references ZaawJava.languages(id)
);
