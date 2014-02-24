import TestHelpers.UserSessionHelpers
import anorm._
import controllers._
import models.UserSession
import org.specs2.mutable._
import play.api.db.DB
import play.api.test._
import play.api.test.Helpers._

/**
 * Functional tests and specifications for Logout
 */
class LogoutSpec extends Specification
{
  "Logout.logout()" should {

    "log out and redirect to index page with valid credentials" in new WithApplication with UserSessionHelpers.InsertingAndDeleting {
      val result = controllers.Logout.logout()(FakeRequest()
        .withSession("logged_user" -> sessionId))

      status(result) must equalTo(SEE_OTHER)
      redirectLocation(result) must beSome.which(_ == routes.Application.renderPage().toString())

      DB.withConnection { implicit c =>
        SQL("select sessionid, userid from usersessions where sessionid={sessionid} limit 1")
          .on('sessionid -> sessionId).as(UserSession.userSession *).headOption must beNone
      }
    }

    "redirect to index with invalid credentials" in new WithApplication {
      val result = controllers.Logout.logout()(FakeRequest())

      status(result) must equalTo(FORBIDDEN)
    }
  }
}