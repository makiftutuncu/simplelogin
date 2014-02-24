import TestHelpers.{UserHelpers, UserSessionHelpers}
import controllers._
import models.User
import org.specs2.mutable._
import play.api.db.DB
import play.api.test._
import play.api.test.Helpers._
import anorm._

/**
 * Functional tests and specifications for Login
 */
class LoginSpec extends Specification
{
  "Login.renderPage()" should {

    "render the login page without valid credentials" in new WithApplication {
      val login = controllers.Login.renderPage()(FakeRequest())

      status(login) must equalTo(OK)
      contentType(login) must beSome.which(_ == "text/html")
      contentAsString(login) contains "Login" mustEqual true
    }

    "redirect to welcome page with valid credentials" in new WithApplication with UserSessionHelpers.InsertingAndDeleting {
      val result = controllers.Application.renderPage()(FakeRequest()
        .withSession("logged_user" -> sessionId))

      status(result) must equalTo(SEE_OTHER)
      redirectLocation(result) must beSome.which(_ == routes.Welcome.renderPage().toString())
    }
  }

  "Login.submitLoginForm()" should {

    "result in a bad request with invalid form data and show login page again" in new WithApplication {
      val login = controllers.Login.submitLoginForm()(FakeRequest().withFormUrlEncodedBody(
        "username" -> "",
        "password" -> ""
      ))

      status(login) must equalTo(BAD_REQUEST)
      contentType(login) must beSome.which(_ == "text/html")
      contentAsString(login) contains "Login" mustEqual true
    }

    "log user in and redirect to welcome for valid username/password" in new WithApplication with UserSessionHelpers.Deleting {
      val result = controllers.Login.submitLoginForm()(FakeRequest().withFormUrlEncodedBody(
        "username" -> username,
        "password" -> hashedPassword
      ))

      status(result) must equalTo(SEE_OTHER)
      redirectLocation(result) must beSome.which(_ == routes.Welcome.renderPage().toString())

      DB.withConnection { implicit c =>
        val maybeUser = SQL("select id, username, email, password, salt from users where username={username} limit 1")
          .on('username -> username).as(User.user *).headOption

        maybeUser must beSome[User]
        maybeUser mustEqual getInsertedUser
      }
    }

    "not be able to log user in since user is already logged in and redirect to index" in new WithApplication with UserSessionHelpers.InsertingAndDeleting {
      // At this point there will already be a session for the logged user
      val result = controllers.Login.submitLoginForm()(FakeRequest().withFormUrlEncodedBody(
        "username" -> username,
        "password" -> hashedPassword
      ))

      status(result) must equalTo(SEE_OTHER)
      redirectLocation(result) must beSome.which(_ == routes.Application.renderPage().toString())
    }

    "result in a bad request and show login page again for password that doesn't match" in new WithApplication with UserHelpers.InsertingAndDeleting {
      val login = controllers.Login.submitLoginForm()(FakeRequest().withFormUrlEncodedBody(
        "username" -> username,
        "password" -> "foobar"
      ))

      status(login) must equalTo(BAD_REQUEST)
      contentType(login) must beSome.which(_ == "text/html")
      contentAsString(login) contains "Login" mustEqual true
    }

    "result in a bad request and show login page again for username that doesn't exist" in new WithApplication with UserHelpers.InsertingAndDeleting {
      val login = controllers.Login.submitLoginForm()(FakeRequest().withFormUrlEncodedBody(
        "username" -> "foobar",
        "password" -> "foobar"
      ))

      status(login) must equalTo(BAD_REQUEST)
      contentType(login) must beSome.which(_ == "text/html")
      contentAsString(login) contains "Login" mustEqual true
    }
  }
}