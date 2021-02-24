drop database if exists empath;

create database empath;

use empath;

create table user (
id int unsigned not null auto_increment,
username varchar(255) unique,
password varchar(255),
emotion varchar(255) not null,
primary key (id)
);

create table song (
id int unsigned not null auto_increment,
artist varchar(255),
album varchar(255),
title varchar(255),
song_url varchar(255),
song_art_url varchar(255),
is_favorite boolean,
emotion varchar(255) not null,
primary key (id),
unique `artist_album_title` (`artist`, `album`, `title`)
);

DELIMITER $$
USE `empath`$$
CREATE PROCEDURE `insert_user` (
IN p_username VARCHAR(255), 
IN p_password VARCHAR(255),
IN p_emotion VARCHAR(255))
BEGIN
insert into user (username, password, emotion)
values (p_username, p_password, p_emotion);
END$$

CREATE PROCEDURE `insert_song` (
IN p_artist VARCHAR(255), 
IN p_album VARCHAR(255),  
IN p_title VARCHAR(255), 
IN p_song_url VARCHAR(255), 
IN p_song_art_url VARCHAR(255),
IN p_is_favorite BOOLEAN,
IN p_emotion VARCHAR(255))
BEGIN
insert into song (artist, album, title, song_url, song_art_url, is_favorite, emotion)
values (p_artist, p_album, p_title, p_song_url, p_song_art_url, p_is_favorite, p_emotion);
END$$

CREATE PROCEDURE `update_user` (
IN p_id INT,
IN p_username VARCHAR(255), 
IN p_password VARCHAR(255),
IN p_emotion VARCHAR(255))
BEGIN
update user set 
username = p_username, 
password = p_password, 
emotion = p_emotion
where id = p_id;
END$$

CREATE PROCEDURE `update_song` (
IN p_id INT,
IN p_artist VARCHAR(255), 
IN p_album VARCHAR(255),  
IN p_title VARCHAR(255), 
IN p_song_url VARCHAR(255), 
IN p_song_art_url VARCHAR(255), 
IN p_is_favorite BOOLEAN,
IN p_emotion VARCHAR(255))
BEGIN
update song set 
artist = p_artist, 
album = p_album, 
title = p_title,
song_url = p_song_url,
song_art_url = p_song_art_url,
is_favorite = p_is_favorite,
emotion = p_emotion
where id = p_id;
END$$

CREATE PROCEDURE `update_song_emotion` (
IN p_id INT,
IN p_emotion VARCHAR(255))
BEGIN
update song set
emotion = p_emotion
where id = p_id;
END$$

CREATE PROCEDURE `update_song_location` (
IN p_id INT,
IN p_location VARCHAR(255))
BEGIN
update song set
song_url = p_location
where id = p_id;
END$$

CREATE PROCEDURE `update_art_location` (
IN p_id INT,
IN p_location VARCHAR(255))
BEGIN
update song set
song_art_url = p_location
where id = p_id;
END$$

CREATE PROCEDURE `select_users` ()
BEGIN
select * from user;
END$$

CREATE PROCEDURE `select_user` (
IN p_id INT)
BEGIN
select * from user
where id = p_id;
END$$

CREATE PROCEDURE `select_songs` ()
BEGIN
select * from song;
END$$

CREATE PROCEDURE `select_songs_by_emotion` (
IN p_emotion VARCHAR(255))
BEGIN
select * from song
where emotion = p_emotion;
END$$

CREATE PROCEDURE `select_song` (
IN p_id INT)
BEGIN
select * from song
where id = p_id;
END$$

CREATE PROCEDURE `select_song_by_title_and_artist` (
IN p_title VARCHAR(255),
IN p_artist VARCHAR(255))
BEGIN
select * from song
where title = p_title
and artist = p_artist;
END$$

CREATE PROCEDURE `delete_user` (
IN p_id INT)
BEGIN
delete from user 
where id = p_id;
END$$

CREATE PROCEDURE `delete_song` (
IN p_id INT)
BEGIN
delete from song 
where id = p_id;
END$$

DELIMITER ;

call empath.insert_user('Johnathan', 'password', 'happiness');
call empath.insert_song('Boston', 'Boston', 'More Than a Feeling', 'somewhere', 'somewhere else', false, 'HAPPINESS');
call empath.update_user(1, 'jransbottom', 'P@ssw0rd', 'anger');
call empath.update_song(1, 'Cold', '13 Ways to Bleed on Stage', 'Outerspace', 'blah', 'blah blah', true, 'ANGER');
/*call empath.select_users();
call empath.select_user(1);
call empath.select_songs();
call empath.select_song(1);
call empath.delete_user(1);
call empath.delete_song(1);*/