package models

import anorm._
import anorm.SqlParser._
import utilities.{DatabaseOperations, Generator}
import play.api.Logger

/**
 * A model for keeping a user session
 *
 * @param sessionId   Id generated for the session
 * @param userId      Id of the user
 */
case class UserSession(sessionId: String, userId: Int)

/**
 * Companion object of UserSession acting as data access layer
 */
object UserSession {
  /**
   * A result set parser for user session records in database, maps records to a UserSession object
   */
  val userSession = {
    get[String]("sessionid") ~ get[Int]("userid") map {
      case sessionId ~ userId => UserSession(sessionId, userId)
    }
  }

  /**
   * Creates a user session for given user id in the database
   *
   * @param userId  Id of the user for whom to create a user session
   *
   * @return  An optional UserSession if successful
   */
  def create(userId: Int): Option[UserSession] = {
    User.readById(userId) match {
      case Some(user: User) =>
        val sessionId = Generator.generateUUID
        val insertSql = SQL("insert into usersessions (sessionid, userid) values ({sessionid}, {userid})")
          .on('sessionid -> sessionId, 'userid -> userId)
        val obtainSql = SQL("select sessionid, userid from usersessions where sessionid={sessionid} limit 1")
          .on('sessionid -> sessionId)
        DatabaseOperations.create[UserSession](insertSql, obtainSql, userSession, "UserSession")
      case _ =>
        Logger.error(s"UserSession.create() - Cannot create a user session for user that doesn't exist with id $userId!")
        None
    }
  }

  /**
   * Reads a user session with given session id from the database
   *
   * @param sessionId  Id of the user session
   *
   * @return  An optional UserSession if successful
   */
  def read(sessionId: String): Option[UserSession] = {
    val readSql = SQL("select sessionid, userid from usersessions where sessionid={sessionid} limit 1")
      .on('sessionid -> sessionId)
    DatabaseOperations.read[UserSession](readSql, userSession, "UserSession")
  }

  /**
   * Deletes a user session with given session id from the database
   *
   * @param sessionId  Id of the user session to delete
   *
   * @return  true if successful, false otherwise
   */
  def delete(sessionId: String): Boolean = {
    val deleteSql = SQL("delete from usersessions where sessionid={sessionid}").on('sessionid -> sessionId)
    DatabaseOperations.delete[UserSession](deleteSql, "UserSession")
  }
}