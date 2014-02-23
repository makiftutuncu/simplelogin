import TestHelpers._
import models.UserSession
import utilities.Generator._
import org.specs2.mutable._
import play.api.test.WithApplication

/**
 * Integration tests and specifications for UserSession
 */
class UserSessionSpec extends Specification {

  "UserSession.create()" should {

    "create test user session" in new WithApplication with UserSessionHelpers.Deleting {
      UserSession.create(getInsertedUser.get.id) mustEqual getInsertedUserSession
    }

    "not be able to create user session with same user id" in new WithApplication with UserSessionHelpers.InsertingAndDeleting {
      UserSession.create(getInsertedUser.get.id) must beNone
    }
  }

  "UserSession.read()" should {

    "read test user session" in new WithApplication with UserSessionHelpers.InsertingAndDeleting {
      UserSession.read(getInsertedUserSession.get.sessionId) mustEqual getInsertedUserSession
    }

    "not be able to read user session with random session id" in new WithApplication {
      UserSession.read(generateRandomText) must beNone
    }
  }

  "UserSession.delete()" should {

    "delete test user session" in new WithApplication with UserSessionHelpers.Inserting {
      UserSession.delete(getInsertedUserSession.get.sessionId) must beTrue
    }

    "not be able to delete user session with random session id" in new WithApplication {
      UserSession.delete(generateRandomText) must beFalse
    }
  }
}