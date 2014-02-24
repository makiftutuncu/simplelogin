package utilities

import play.api.mvc.{Request, Cookie}
import play.api.Logger
import models.UserSession

/**
 * A utility object for helping authentication check
 */
object AuthenticationHelper {
  /**
   * A general purpose authentication check for each request,
   * it checks cookies for a session id matching a user.
   *
   * @param request Request of the action
   * @tparam T      Type of the request
   *
   * @return  An optional user session if authentication is successful
   */
  def getUserSessionForRequest[T](request: Request[T]): Option[UserSession] = {
    // Look for cookie first
    request.cookies.get("logged_user") map {
      cookie: Cookie =>
        Logger.debug(s"AuthenticationHelper.getUserSessionForRequest() - Cookie found with sessionId ${cookie.value}")
        UserSession.read(cookie.value) map {
          userSession: UserSession =>
            Logger.debug(s"AuthenticationHelper.getUserSessionForRequest() - Authentication successful as sessionId ${userSession.sessionId}")
            Option(userSession)
        } getOrElse {
          Logger.error(s"AuthenticationHelper.getUserSessionForRequest() - Authentication failed for cookie with sessionId ${cookie.value}! Possible attack!")
          None
        }
    } getOrElse {
      // Look for session cookie
      request.session.get("logged_user") map {
        sessionId: String =>
          Logger.debug(s"AuthenticationHelper.getUserSessionForRequest() - Session cookie found with sessionId $sessionId")
          UserSession.read(sessionId) map {
            userSession: UserSession =>
              Logger.debug(s"AuthenticationHelper.getUserSessionForRequest() - Authentication successful as sessionId ${userSession.sessionId}")
              Option(userSession)
          } getOrElse {
            Logger.error(s"AuthenticationHelper.getUserSessionForRequest() - Authentication failed for session cookie with sessionId $sessionId! Possible attack!")
            None
          }
      } getOrElse {
        Logger.debug("AuthenticationHelper.getUserSessionForRequest() - No credentials found, not logged in.")
        None
      }
    }
  }
}