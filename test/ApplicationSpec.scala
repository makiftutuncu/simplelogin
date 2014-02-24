import TestHelpers.UserSessionHelpers
import controllers._
import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

/**
 * Functional tests and specifications for Application
 */
class ApplicationSpec extends Specification
{
  "Application.renderPage()" should {

    "render the index page without valid credentials" in new WithApplication {
      val index = controllers.Application.renderPage()(FakeRequest())

      status(index) must equalTo(OK)
      contentType(index) must beSome.which(_ == "text/html")
      contentAsString(index) contains "Welcome!" mustEqual true
    }

    "redirect to welcome page with valid credentials" in new WithApplication with UserSessionHelpers.InsertingAndDeleting {
      val result = controllers.Application.renderPage()(FakeRequest()
        .withSession("logged_user" -> sessionId))

      status(result) must equalTo(SEE_OTHER)
      redirectLocation(result) must beSome.which(_ == routes.Welcome.renderPage().toString())
    }
  }
}