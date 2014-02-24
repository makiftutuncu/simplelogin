package utilities

import play.api.mvc._
import models.UserSession
import scala.concurrent.Future

class AuthenticatedRequest[A](val userSession: UserSession, request: Request[A]) extends WrappedRequest[A](request)

/**
 * A custom Action for allowing requests with valid authentication credentials
 */
object Authenticated extends ActionBuilder[AuthenticatedRequest] with Results {
  def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[SimpleResult]) = {
    AuthenticationHelper.getUserSessionForRequest(request) map {
      userSession: UserSession =>
        block(new AuthenticatedRequest(userSession, request))
    } getOrElse {
      Future.successful(Forbidden(views.html.index()).discardingCookies(DiscardingCookie("logged_user")).withNewSession)
    }
  }
}