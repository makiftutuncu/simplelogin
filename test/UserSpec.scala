import TestHelpers._
import models.User
import utilities.Generator._
import org.specs2.mutable._
import play.api.test.WithApplication

/**
 * Integration tests and specifications for User
 */
class UserSpec extends Specification {

  "User.create()" should {

    "create test user" in new WithApplication with UserHelpers.Deleting {
      User.create(username, email, hashedPassword) mustEqual getInsertedUser
    }

    "not be able to create user with same name" in new WithApplication with UserHelpers.InsertingAndDeleting {
      User.create(username, email, hashedPassword) must beNone
    }
  }

  "User.readById()" should {

    "read test user" in new WithApplication with UserHelpers.InsertingAndDeleting {
      User.readById(getInsertedUser.get.id) mustEqual getInsertedUser
    }

    "not be able to read user with invalid id" in new WithApplication {
      User.readById(-1) must beNone
    }
  }

  "User.readByUsername()" should {

    "read test user" in new WithApplication with UserHelpers.InsertingAndDeleting {
      User.readByUsername(username) mustEqual getInsertedUser
    }

    "not be able to read user with random name" in new WithApplication {
      User.readByUsername(generateRandomText) must beNone
    }
  }

  "User.readByEmail()" should {

    "read test user" in new WithApplication with UserHelpers.InsertingAndDeleting {
      User.readByEmail(email) mustEqual getInsertedUser
    }

    "not be able to read user with random email" in new WithApplication {
      User.readByEmail(generateRandomText) must beNone
    }
  }

  "User.delete()" should {

    "delete test user" in new WithApplication with UserHelpers.Inserting {
      User.delete(getInsertedUser.get.id) must beTrue
    }

    "not be able to delete user with invalid id" in new WithApplication {
      User.delete(-1) must beFalse
    }
  }
}