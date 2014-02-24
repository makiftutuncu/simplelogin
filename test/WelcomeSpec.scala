import TestHelpers.UserSessionHelpers
import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

/**
 * Functional tests and specifications for Welcome
 */
class WelcomeSpec extends Specification
{
  "Welcome.renderPage()" should {

    "render the welcome page with valid credentials" in new WithApplication with UserSessionHelpers.InsertingAndDeleting {
      val welcome = controllers.Welcome.renderPage()(FakeRequest()
        .withSession("logged_user" -> sessionId))

      status(welcome) must equalTo(OK)
      contentType(welcome) must beSome.which(_ == "text/html")
      contentAsString(welcome) contains username mustEqual true
    }

    "redirect to index page with invalid credentials" in new WithApplication {
      val result = controllers.Welcome.renderPage()(FakeRequest())

      status(result) must equalTo(FORBIDDEN)
    }
  }
}