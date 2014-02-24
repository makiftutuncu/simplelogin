package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.Logger
import models.{UserSession, User}
import utilities.{AuthenticationHelper, Generator}

/**
 * Login controller which controls everything about logging a user into the system
 */
object Login extends Controller {
  /**
   * A form matcher for the user log in form, maps the form data to a LoginFormUser object
   */
  val loginForm: Form[LoginFormUser] = Form(
    mapping(
      "username" -> text(3),
      "password" -> text(6),
      "keeploggedin" -> optional(text)
    )(LoginFormUser.apply)(LoginFormUser.unapply)
  )

  /**
   * Entry point of the login page, takes user to welcome page if authenticated,
   * shows login page otherwise.
   */
  def renderPage = Action { implicit request =>
    AuthenticationHelper.getUserSessionForRequest(request) map {
      userSession: UserSession =>
        Redirect(routes.Welcome.renderPage())
    } getOrElse {
      Ok(views.html.login())
    }
  }

  /**
   * Action that validates login data and performs login operation,
   * takes user to index page if not authorized
   */
  def submitLoginForm = Action {
    implicit request => loginForm.bindFromRequest.fold(
      errors =>
        giveFormError("Login.submitLoginForm() - Username or password in login form was invalid! " + errors.errorsAsJson, "username_password_invalid"),
      loginFormUser => {
        Logger.debug(s"Login.submitLoginForm() - Login form was valid for $loginFormUser.")
        User.readByUsername(loginFormUser.username) map {
          user: User =>
            val saltedPassword = Generator.generateSHA512(user.salt + loginFormUser.hashedpassword)
            if(user.password == saltedPassword) {
              UserSession.create(user.id) map {
                userSession: UserSession =>
                  loginFormUser.keeploggedin match {
                    case Some(keeploggedin: String) =>
                      Redirect(routes.Welcome.renderPage())
                        .withCookies(Cookie(name = "logged_user", value = userSession.sessionId, maxAge = Option(60 * 60 * 24 * 15)))
                    case _ =>
                      Redirect(routes.Welcome.renderPage()).withSession("logged_user" -> userSession.sessionId)
                  }
              } getOrElse {
                giveError(s"Login.submitLoginForm() - An error occurred while creating user session for ${loginFormUser.username}")
              }
            } else {
              giveFormError(s"Login.submitLoginForm() - Username and password doesn't match for $loginFormUser!", "username_password_mismatch")
            }
        } getOrElse {
          giveFormError(s"Login.submitLoginForm() - Username and password doesn't match for $loginFormUser!", "username_password_mismatch")
        }
      }
    )
  }

  /**
   * Generates a result for a form error, logs it and returns to login page with a bad request
   *
   * @param logMsg        Message to write to log
   * @param formErrorMsg  Message code for identifying error message in login page
   *
   * @return  A SimpleResult with a bad request for login page
   */
  private def giveFormError(logMsg: String, formErrorMsg: String): SimpleResult = {
    Logger.error(logMsg)
    BadRequest(views.html.login(error = formErrorMsg))
  }

  /**
   * Generates a result for an error, logs it and returns to index page with redirect
   *
   * @param logMsg  Message to write to log
   *
   * @return  A SimpleResult with a redirect for index page
   */
  private def giveError(logMsg: String): SimpleResult = {
    Logger.error(logMsg)
    Redirect(routes.Application.renderPage())
  }
}

/**
 * A model of the login form
 *
 * @param username        Name of the user
 * @param hashedpassword  Hashed value of the password user entered
 * @param keeploggedin    Flag to keep user logged in between sessions
 *                        (Value will be "on" if user checked "keep logged in" option)
 */
case class LoginFormUser(username: String, hashedpassword: String, keeploggedin: Option[String])