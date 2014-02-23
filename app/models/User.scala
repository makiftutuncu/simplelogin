package models

import anorm._
import anorm.SqlParser._
import utilities.{DatabaseOperations, Generator}

/**
 * A model for keeping a user
 *
 * @param id        Id of the user which is an auto incremented number
 * @param username  Name of the user
 * @param email     Email address of the user
 * @param password  Salted and hashed value of password of the user
 * @param salt      A random salt value to increase the security
 */
case class User(id: Int, username: String, email: String, password: String, salt: String)

/**
 * Companion object of User acting as data access layer
 */
object User {
  /**
   * A result set parser for user records in database, maps records to a User object
   */
  val user = {
    get[Int]("id") ~ get[String]("username") ~ get[String]("email") ~ get[String]("password") ~ get[String]("salt") map {
      case id ~ username ~ email ~ password ~ salt => User(id, username, email, password, salt)
    }
  }

  /**
   * Creates a user for given information in the database
   *
   * @param username        Name of the user
   * @param email           Email address of the user
   * @param hashedPassword  Hashed value of the password user entered
   *
   * @return  An optional User if successful
   */
  def create(username: String, email: String, hashedPassword: String): Option[User] = {
    val salt = Generator.generateUUID
    val password = Generator.generateSHA512(salt + hashedPassword) // I put my salt before I start to eat. ;)
    val insertSql = SQL("insert into users (username, email, password, salt) values ({username}, {email}, {password}, {salt})")
      .on('username -> username, 'email -> email, 'password -> password, 'salt -> salt)
    val obtainSql = SQL("select id, username, email, password, salt from users where username={username} and password={password} limit 1")
      .on('username -> username, 'password -> password)
    DatabaseOperations.create[User](insertSql, obtainSql, user, "User")
  }

  /**
   * Reads a user with given id from the database
   *
   * @param id  Id of the user
   *
   * @return  An optional User if successful
   */
  def readById(id: Int): Option[User] = {
    val readSql = SQL("select id, username, email, password, salt from users where id={id} limit 1")
      .on('id -> id)
    DatabaseOperations.read[User](readSql, user, "User")
  }

  /**
   * Reads a user with given username from the database
   *
   * @param username  Name of the user
   *
   * @return  An optional User if successful
   */
  def readByUsername(username: String): Option[User] = {
    val readSql = SQL("select id, username, email, password, salt from users where username={username} limit 1")
      .on('username -> username)
    DatabaseOperations.read[User](readSql, user, "User")
  }

  /**
   * Reads a user with given email from the database
   *
   * @param email  Name of the user
   *
   * @return  An optional User if successful
   */
  def readByEmail(email: String): Option[User] = {
    val readSql = SQL("select id, username, email, password, salt from users where email={email} limit 1")
      .on('email -> email)
    DatabaseOperations.read[User](readSql, user, "User")
  }

  /**
   * Deletes a user with given id from the database
   *
   * @param id  Id of the user to delete
   *
   * @return  true if successful, false otherwise
   */
  def delete(id: Int): Boolean = {
    val deleteSql = SQL("delete from users where id={id}").on('id -> id)
    DatabaseOperations.delete[User](deleteSql, "User")
  }
}