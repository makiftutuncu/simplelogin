# --- !Ups
CREATE TABLE users (
    id INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(32) NOT NULL UNIQUE,
    email VARCHAR(48) NOT NULL UNIQUE,
    password CHAR(128) NOT NULL,
    salt CHAR(32) NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET=utf8;

INSERT INTO users (id, username, email, password, salt) values (NULL, "akif", "akif@linovi.com", "94375f1ec80a346b39d2323b1af9170fd05f99c1c27b7f4804a0cfc8f1563500e453675c8a8f160d4932de254a166f1a534c543fdc5da433f35383523983b37f", "2cc10c96fad34083aa7cab4d8232a16a");

CREATE TABLE usersessions (
    sessionid CHAR(32) NOT NULL PRIMARY KEY,
    userid INT(10) NOT NULL UNIQUE
) ENGINE = InnoDB DEFAULT CHARSET=utf8;

# --- !Downs
DROP TABLE users;
DROP TABLE usersessions;