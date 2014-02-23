# --- !Ups
CREATE TABLE users (
    id INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(32) NOT NULL,
    email VARCHAR(48) NOT NULL,
    password CHAR(128) NOT NULL,
    salt CHAR(32) NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE usersessions (
    sessionid CHAR(32) NOT NULL PRIMARY KEY,
    userid INT(10) NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET=utf8;

# --- !Downs
DROP TABLE users;
DROP TABLE usersessions;