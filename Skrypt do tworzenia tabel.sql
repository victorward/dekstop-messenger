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

create table ZaawJava.conversations
(
conversation_id int auto_increment primary key,
user1 int,
user2 int,
foreign key (user1) references ZaawJava.users(id),
foreign key (user2) references ZaawJava.users(id),
CONSTRAINT UQ_UserId_ContactID UNIQUE(user1, user2)
);

create table ZaawJava.private_messages
(
message_id int auto_increment primary key,
conversation_id int,
sender int,
content text,
send_date datetime,
foreign key (conversation_id) references ZaawJava.conversations(conversation_id)
);
CREATE INDEX Datetime_Index ON ZaawJava.private_messages (send_date);
