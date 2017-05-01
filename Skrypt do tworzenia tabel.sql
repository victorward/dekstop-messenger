create table ZaawJava.country
(
id int auto_increment primary key,
country_name varchar(30)
);

create table ZaawJava.language
(
id int auto_increment primary key,
language_name varchar(20)
);

create table ZaawJava.user
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
foreign key (country_id) references ZaawJava.country(id)
);

create table ZaawJava.user_language
(
user_id int,
language_id int,
foreign key (user_id) references ZaawJava.user(id),
foreign key (language_id) references ZaawJava.language(id)
);
