# --- !Ups
CREATE TABLE users (
    id INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(32) NOT NULL,
    email VARCHAR(32) NOT NULL,
    password CHAR(128) NOT NULL,
    salt CHAR(128) NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE usersessions (
    sessionkey VARCHAR(128) NOT NULL PRIMARY KEY,
    userid INT(10) NOT NULL,
    FOREIGN KEY (userid) REFERENCES users(id)
) ENGINE = InnoDB DEFAULT CHARSET=utf8;

# --- !Downs
DROP TABLE users;
DROP TABLE usersessions;