import models.{UserSession, User}
import scala.Some
import utilities.Generator._
import anorm._
import play.api.test.WithApplication
import org.specs2.execute.AsResult
import play.api.db.DB

/**
 * Methods and traits that provide help for tests
 */
object TestHelpers {
  /**
   * Methods and traits that provide help for User tests
   */
  object UserHelpers {
    /**
     * Gives SQL for inserting a user to the database
     *
     * @param username  Name of the user
     * @param email     Email address of the user
     * @param password  Salted and hashed value of password of the user
     * @param salt      A random salt value to increase the security
     *
     * @return  Resulting SQL
     */
    def insertUserSQL(username: String, email: String, password: String, salt: String) = {
      SQL("insert into users (username, email, password, salt) values ({username}, {email}, {password}, {salt})")
        .on('username -> username, 'email -> email, 'password -> password, 'salt -> salt)
    }

    /**
     * Gives SQL for reading a user from the database
     *
     * @param username  Name of the user
     *
     * @return  Resulting SQL
     */
    def readUserSQL(username: String) = {
      SQL("select id, username, email, password, salt from users where username={username} limit 1")
        .on('username -> username)
    }

    /**
     * Gives SQL for deleting a user from the database
     *
     * @param id  Id of the user to delete
     *
     * @return  Resulting SQL
     */
    def deleteUserSQL(id: Int) = {
      SQL("delete from users where id={id}").on('id -> id)
    }

    /**
     * An around performing the test after inserting and before deleting the test user
     */
    trait Inserting extends WithApplication {
      val username: String = "USERNAME_" + generateRandomText
      val email: String = "EMAIL_" + generateRandomText
      val salt: String = generateUUID
      val hashedPassword: String = generateSHA512("123456")
      val password: String = generateSHA512(salt + hashedPassword)

      def getInsertedUser: Option[User] = {
        DB.withConnection { implicit c =>
          readUserSQL(username).as(User.user *).headOption
        }
      }

      override def around[T: AsResult](t: => T) = super.around {
        DB.withConnection { implicit c =>
          insertUserSQL(username, email, password, salt).executeUpdate()
        }
        getInsertedUser match {
          case Some(_) => // This is OK
          case _ => throw new Exception("Could not insert!")
        }
        AsResult(t)
      }
    }

    /**
     * An around performing the test before deleting the test user
     */
    trait Deleting extends WithApplication {
      val username: String = "USERNAME_" + generateRandomText
      val email: String = "EMAIL_" + generateRandomText
      val hashedPassword: String = generateSHA512("123456")

      def getInsertedUser: Option[User] = {
        DB.withConnection { implicit c =>
          readUserSQL(username).as(User.user *).headOption
        }
      }

      override def around[T: AsResult](t: => T) = super.around {
        val result = AsResult(t)
        getInsertedUser match {
          case Some(_) => // This is OK
          case _ => throw new Exception("Could not insert!")
        }
        DB.withConnection { implicit c =>
          deleteUserSQL(getInsertedUser.get.id).executeUpdate()
        }
        result
      }
    }

    /**
     * An around performing the test after inserting and before deleting the test user
     */
    trait InsertingAndDeleting extends WithApplication {
      val username: String = "USERNAME_" + generateRandomText
      val email: String = "EMAIL_" + generateRandomText
      val salt: String = generateUUID
      val hashedPassword: String = generateSHA512("123456")
      val password: String = generateSHA512(salt + hashedPassword)

      def getInsertedUser: Option[User] = {
        DB.withConnection { implicit c =>
          readUserSQL(username).as(User.user *).headOption
        }
      }

      override def around[T: AsResult](t: => T) = super.around {
        DB.withConnection { implicit c =>
          insertUserSQL(username, email, password, salt).executeUpdate()
        }
        getInsertedUser match {
          case Some(_) => // This is OK
          case _ => throw new Exception("Could not insert!")
        }
        val result = AsResult(t)
        DB.withConnection { implicit c =>
          deleteUserSQL(getInsertedUser.get.id).executeUpdate()
        }
        result
      }
    }
  }

  /**
   * Methods and traits that provide help for UserSession tests
   */
  object UserSessionHelpers {
    /**
     * Gives SQL for inserting a user session to the database
     *
     * @param sessionId   Id generated for the session
     * @param userId      Id of the user
     *
     * @return  Resulting SQL
     */
    def insertUserSessionSQL(sessionId: String, userId: Int) = {
      SQL("insert into usersessions (sessionid, userid) values ({sessionid}, {userid})")
        .on('sessionid -> sessionId, 'userid -> userId)
    }

    /**
     * Gives SQL for reading a user session from the database
     *
     * @param userId  User id of the uesr session
     *
     * @return  Resulting SQL
     */
    def readUserSessionSQL(userId: Int) = {
      SQL("select sessionid, userid from usersessions where userid={userid} limit 1")
        .on('userid -> userId)
    }

    /**
     * Gives SQL for deleting a user session from the database
     *
     * @param sessionId   Id of the user session to delete
     *
     * @return  Resulting SQL
     */
    def deleteUserSessionSQL(sessionId: String) = {
      SQL("delete from usersessions where sessionid={sessionid}").on('sessionid -> sessionId)
    }

    /**
     * An around performing the test after inserting and before deleting the test user session
     */
    trait Inserting extends UserHelpers.InsertingAndDeleting {
      val sessionId: String = generateUUID

      def getInsertedUserSession: Option[UserSession] = {
        DB.withConnection { implicit c =>
          readUserSessionSQL(getInsertedUser.get.id).as(UserSession.userSession *).headOption
        }
      }

      override def around[T: AsResult](t: => T) = super.around {
        DB.withConnection { implicit c =>
          insertUserSessionSQL(sessionId, getInsertedUser.get.id).executeUpdate()
        }
        getInsertedUserSession match {
          case Some(_) => // This is OK
          case _ => throw new Exception("Could not insert!")
        }
        AsResult(t)
      }
    }

    /**
     * An around performing the test before deleting the test user session
     */
    trait Deleting extends UserHelpers.InsertingAndDeleting {
      val sessionId: String = generateUUID

      def getInsertedUserSession: Option[UserSession] = {
        DB.withConnection { implicit c =>
          readUserSessionSQL(getInsertedUser.get.id).as(UserSession.userSession *).headOption
        }
      }

      override def around[T: AsResult](t: => T) = super.around {
        val result = AsResult(t)
        getInsertedUserSession match {
          case Some(_) => // This is OK
          case _ => throw new Exception("Could not insert!")
        }
        DB.withConnection { implicit c =>
          deleteUserSessionSQL(getInsertedUserSession.get.sessionId).executeUpdate()
        }
        result
      }
    }

    /**
     * An around performing the test after inserting and before deleting the test user session
     */
    trait InsertingAndDeleting extends UserHelpers.InsertingAndDeleting {
      val sessionId: String = generateUUID

      def getInsertedUserSession: Option[UserSession] = {
        DB.withConnection { implicit c =>
          readUserSessionSQL(getInsertedUser.get.id).as(UserSession.userSession *).headOption
        }
      }

      override def around[T: AsResult](t: => T) = super.around {
        DB.withConnection { implicit c =>
          insertUserSessionSQL(sessionId, getInsertedUser.get.id).executeUpdate()
        }
        getInsertedUserSession match {
          case Some(_) => // This is OK
          case _ => throw new Exception("Could not insert!")
        }
        val result = AsResult(t)
        DB.withConnection { implicit c =>
          deleteUserSessionSQL(getInsertedUserSession.get.sessionId).executeUpdate()
        }
        result
      }
    }
  }
}