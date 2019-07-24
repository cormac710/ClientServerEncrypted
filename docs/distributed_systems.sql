DROP DATABASE IF EXISTS Albums;
CREATE DATABASE Albums;
USE Albums;
DROP TABLE IF EXISTS Album;
CREATE TABLE Album(
	ID				INT(3) primary key,
	Album_Name		VARCHAR (100),
	Album_Artist		VARCHAR(20),
	Num_Tracks			INT,
	Record_Label		VARCHAR(100),
	inStock				BOOLEAN
)ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=latin1;

INSERT INTO ALBUM VALUES(1, "Emperor of Sand", "Mastodon", 11, "Reprise",  false);
INSERT INTO ALBUM VALUES(2, "Crack the Skye", "Mastodon", 11, "Reprise",  true);
INSERT INTO ALBUM VALUES(3, "The Hunter", "Mastodon", "Reprise",  11,true);
INSERT INTO ALBUM VALUES(4, "Blood Mountain", "Mastodon", 11,"Reprise", false);
INSERT INTO ALBUM VALUES(5, "Leviathan", "Mastodon", 11,"Reprise",  true); 

ALTER TABLE ALBUM MODIFY COLUMN id INT auto_increment;

SELECT *
FROM Album;

DROP TABLE Authenticator_Table;
CREATE TABLE Authenticator_Table(
	client_id VARCHAR(100),
	Encoded_Key VARCHAR(100) 
);

INSERT INTO Authenticator_Table VALUES("client 001", "OSix5UrA8aQtcREe5f6PUpzn02Yk06dOOymplOdl4JI="); 

SELECT * FROM Authenticator_Table;