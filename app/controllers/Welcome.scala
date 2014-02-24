package controllers

import play.api.mvc._
import models.User
import utilities.Authenticated

/**
 * Welcome controller which controls the main content that can only be accessed by authenticated users
 */
object Welcome extends Controller {
  /**
   * Action that shows the content for the authenticated user
   */
  def renderPage = Authenticated { implicit request =>
    Ok(views.html.welcome(User.readById(request.userSession.userId).get))
  }
}