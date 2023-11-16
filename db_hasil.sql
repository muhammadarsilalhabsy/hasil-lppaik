drop database db_hasil;
create database db_hasil;

use db_hasil;

create table activities (
id varchar(255) not null primary key,
title varchar(255),
mandatory bit(1),
date date,
link text,
start_time time,
end_time time,
description mediumtext,
location varchar(255)
)engine innodb;

create table images (
id int not null auto_increment primary key,
path text,
data longblob,
type varchar(100)
)engine innodb;

create table activity_images (
id varchar(255) not null primary key,
activity_id varchar(255),
image mediumtext,
constraint fk_activities_images foreign key (activity_id) references activities (id)
)engine innodb;

create table majors(
id varchar(255) not null primary key,
name VARCHAR(255) not null
)engine innodb;

create table users (
username varchar(10) not null primary key,
name varchar(255) not null,
password varchar(255) not null,
email varchar(255) not null,
avatar varchar(255),
completed bit(1),
gender varchar(20),
major varchar(255),
motto mediumtext,
token varchar(255),
token_expired_at bigint,
unique key email_uni (email),
unique key token_uni (token),
constraint fk_users_major foreign key (major) references majors (id)
)engine innodb;

create table user_activities (
user_username varchar(10) NOT NULL,
activity_id varchar(255),
PRIMARY KEY (`user_username`,`activity_id`),
CONSTRAINT `fk_user_activities_activity` FOREIGN KEY (`activity_id`) REFERENCES `activities` (`id`),
CONSTRAINT `fk_user_activities_user` FOREIGN KEY (`user_username`) REFERENCES `users` (`username`)
)engine innodb;


create table roles (
id varchar(255) not null primary key,
name enum('MAHASISWA','KATING','DOSEN','ADMIN','TUTOR')
)engine innodb;

create table user_roles (
user_username varchar(10) not null,
role_id varchar(255) not null,
primary key (user_username, role_id),
constraint fk_user_roles foreign key (user_username) references users (username),
constraint fk_roles_users foreign key (role_id) references roles (id)
);

create table certificates(
id varchar(255) not null primary key,
user_username varchar(10) not null,
unique key username_uni (user_username),
constraint fk_certificate_user foreign key (user_username) references users (username)
)engine innodb;

drop table control_book_details ;

create table control_book_details(
id varchar(255) not null primary key,
user_username varchar(10) not null,acti
tutor varchar(10) not null,
date date,
lesson text,
description text,
constraint fk_cbd_user_u foreign key (user_username) references users (username),
constraint fk_cbd_user_t foreign key (tutor) references users (username)
)engine innodb;

-- DATA TEMP for testing
insert into activities 
(obj_id, title, date, start_time, end_time, description, location)
values
('obj_id_1', 'title 1', '2019-12-02', '10:19:00', "11:19:00", 'simple description', 'location 1'),
('obj_id_2', 'title 2', '2021-12-02', '10:19:00', "11:19:00", 'simple description', 'location 2'),
('obj_id_3', 'title 3', '2020-12-02', '10:19:00', "11:19:00", 'simple description', 'location 3'),
('obj_id_4', 'title 4', '2022-12-02', '10:19:00', "11:19:00", 'simple description', 'location 4'),
('obj_id_5', 'title 5', '2023-12-02', '10:19:00', "11:19:00", 'simple description', 'location 5');

insert into majors (id, name) values
('m001', 'HUKUM');
-- temp
('j_id_2', 'simple jurusan 2'),
('j_id_3', 'simple jurusan 3'),
('j_id_4', 'simple jurusan 4'),
('j_id_5', 'simple jurusan 5');

insert into users 
(username, name, password, email, avatar, gender, token)
values
("admin", "admin", "admin", "admin@email.com", "admin", "MALE", "admin-token");

-- TEMP
("username_2", "name_2", "password_2", "email2@email.com", "avatar_2", "MALE", "j_id_1", "token_2"),
("username_3", "name_3", "password_3", "email3@email.com", "avatar_3", "MALE", "j_id_2", "token_3"),
("username_4", "name_4", "password_4", "email4@email.com", "avatar_4", "MALE", "j_id_3", "token_4"),
("username_5", "name_5", "password_5", "email5@email.com", "avatar_5", "MALE", "j_id_2", "token_5");

insert into user_activities (user_username, activity_obj_id) values
("username_1", 'obj_id_1'),
("username_1", 'obj_id_2'),
("username_1", 'obj_id_3'),
("username_2", 'obj_id_1'),
("username_3", 'obj_id_1');

insert into roles (id, name) values
('ROO1', 'ADMIN'),
('ROO2', 'MAHASISWA'),
('ROO3', 'TUTOR'),
('ROO4', 'DOSEN'),
('ROO5', 'KATING');

insert into user_roles (user_username, role_id) values
("admin", 'ROO1');
--temp
("username_1", 'role_id_2'),
("username_1", 'role_id_3'),
("username_2", 'role_id_1'),
("username_3", 'role_id_1');

insert into certificates (id, user_username) values
('c_1', 'username_1'),
('c_2', 'username_2'),
('c_3', 'username_3');

insert into images (path, type) values
('/test4-image', '.png'),
('/test1-image', '.png'),
('/test2-image', '.jpg'),
('/test3-image', '.jpeg');

insert into activity_images (activity_obj_id, image_id) values
('obj_id_1', 1),
('obj_id_1', 2),
('obj_id_2', 1),
('obj_id_2', 3),
('obj_id_2', 1),user_username
('obj_id_2', 1);

insert into control_book_details
(user_username, tutor, date, lesson, description) 
values
('username_2', 'username_1', '2023-12-02', 'iqro 1', 'belum bisa baca dengan baik'),
('username_2', 'username_1', '2023-12-05', 'iqro 2', 'belum bisa baca dengan baik'),
('username_3', 'username_1', '2023-12-03', 'iqro 1', 'belum bisa baca dengan baik'),
('username_3', 'username_1', '2023-12-04', 'iqro 1', 'belum bisa baca dengan baik');

-- ----------------------------JOIN------------------------


select * from users;

-- JOIN users with jurusan
select u.name, u.email, u.avatar, j.name as jurusan, u.token, u.token_expired_at from users as u
join jurusan as j on (j.id = u.jurusan);

-- JOIN users with activities
select u.name as username, u.email, a.title, a.date from user_activities as u_a
join users as u on (u.username = u_a.user_username)
join activities as a on (a.obj_id = u_a.activity_obj_id);

-- JOIN users with roles
select u.name as username, r.name as has_role from user_roles as u_r
join users as u on (u.username = u_r.user_username)
join roles as r on (r.id = u_r.role_id);

-- JOIN activity and image with activity_images
select a.title, i.path as image_path from image_activities as a_i
join activities as a on (a.obj_id = a_i.activity_obj_id)
join images as i on (i.id = image_id);

-- JOIN user and certificates
select u.name, c.id from certificates as c
join users as u on (u.username = c.user_username);

-- JOIN users and control_book_details;
select u.name as mahasiswa, 
cbd.date,
ut.name as tutor,
cbd.lesson,
cbd.description 
from control_book_details as cbd
join users as u on (u.username = cbd.user_username)
join users as ut on (ut.username = cbd.tutor);


-- step by bte delete

delete from activity_images;
delete from user_activities;
delete from activities;
delete from certificates;
delete from user_roles;
delete from roles;
delete from control_book_details;
delete from users;
delete from images;
delete from majors;















