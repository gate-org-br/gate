DROP TABLE IF EXISTS Person;

CREATE TABLE Person (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name varchar(128) NOT NULL,
  birthdate date NOT NULL,
  contract__min date DEFAULT NULL,
  contract__max date DEFAULT NULL
);

INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 1', '2000-12-01', '2000-12-01', '2020-12-01');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 2', '2000-12-02', '2000-12-02', '2020-12-02');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 3', '2000-12-03', '2000-12-03', '2020-12-03');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 4', '2000-12-04', '2000-12-04', '2020-12-04');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 5', '2000-12-05', '2000-12-05', '2020-12-05');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 6', '2000-12-06', '2000-12-06', '2020-12-06');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 7', '2000-12-07', '2000-12-07', '2020-12-07');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 8', '2000-12-08', '2000-12-08', '2020-12-08');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 9', '2000-12-09', '2000-12-09', '2020-12-09');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 10', '2000-12-10', '2000-12-10', '2020-12-10');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 11', '2000-12-11', '2000-12-11', '2020-12-11');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 12', '2000-12-12', '2000-12-12', '2020-12-12');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 13', '2000-12-13', '2000-12-13', '2020-12-13');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 14', '2000-12-14', '2000-12-14', '2020-12-14');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 15', '2000-12-15', '2000-12-15', '2020-12-15');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 16', '2000-12-16', '2000-12-16', '2020-12-16');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 17', '2000-12-17', '2000-12-17', '2020-12-17');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 18', '2000-12-18', '2000-12-18', '2020-12-18');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 19', '2000-12-19', '2000-12-19', '2020-12-19');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 20', '2000-12-20', '2000-12-20', '2020-12-20');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 21', '2000-12-21', '2000-12-21', '2020-12-21');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 22', '2000-12-22', '2000-12-22', '2020-12-22');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 23', '2000-12-23', '2000-12-23', '2020-12-23');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 24', '2000-12-24', '2000-12-24', '2020-12-24');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 25', '2000-12-25', '2000-12-25', '2020-12-25');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 26', '2000-12-26', '2000-12-26', '2020-12-26');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 27', '2000-12-27', '2000-12-27', '2020-12-27');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 28', '2000-12-28', '2000-12-28', '2020-12-28');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 29', '2000-12-29', '2000-12-29', '2020-12-29');
INSERT INTO Person (name, birthdate, contract__min, contract__max) VALUES ('Person 30', '2000-12-30', '2000-12-30', '2020-12-30');

DROP TABLE IF EXISTS Contact;
CREATE TABLE Contact (
	id INT PRIMARY KEY AUTO_INCREMENT,
	Person$id INT NOT NULL,
        type varchar(64) NOT NULL,
	val varchar(64) NOT NULL
);

 
INSERT INTO Contact (Person$id, type, val) 
	select id, 'PHONE', id FROM Person
