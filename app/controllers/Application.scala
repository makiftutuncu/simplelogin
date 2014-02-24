package controllers

import play.api.mvc._
import utilities.AuthenticationHelper
import models.UserSession

/**
 * Main controller and the entry point of the application
 */
object Application extends Controller {
  /**
   * Entry point of the application, takes user to welcome page if authenticated,
   * shows index page otherwise.
   */
  def renderPage = Action { implicit request =>
    AuthenticationHelper.getUserSessionForRequest(request) map {
      userSession: UserSession =>
        Redirect(routes.Welcome.renderPage())
    } getOrElse {
      Ok(views.html.index())
    }
  }
}