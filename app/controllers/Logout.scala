package controllers

import play.api.mvc._
import play.api.Logger
import models.UserSession
import utilities.Authenticated

/**
 * Logout controller which controls everything about logging a user out of the system
 */
object Logout extends Controller {
  /**
   * Action that performs logout operation for the authenticated user and takes user to index page
   */
  def logout = Authenticated { implicit request =>
    Logger.debug(s"Logout.logout() - Logging user with id ${request.userSession.userId} out...")
    if(!UserSession.delete(request.userSession.sessionId)) {
      Logger.error(s"Logout.logout() - Logging user with id ${request.userSession.userId} failed!")
    }
    Redirect(routes.Application.renderPage()).discardingCookies(DiscardingCookie(name = "logged_user")).withNewSession
  }
}