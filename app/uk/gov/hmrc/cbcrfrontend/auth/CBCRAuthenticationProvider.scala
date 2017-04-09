/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.cbcrfrontend.auth

import javax.inject.{Inject, Singleton}

import play.api.Configuration
import play.api.mvc.Action
import play.api.mvc.Results.Redirect
import play.api.mvc.{AnyContent, Request, Result}

import scala.concurrent.Future
import uk.gov.hmrc.cbcrfrontend.controllers.{AsyncUserRequest, UserRequest}
import uk.gov.hmrc.play.frontend.auth._
import uk.gov.hmrc.play.frontend.auth.connectors.AuthConnector


trait SecuredActions extends Actions {
  def AuthenticatedAction(r: UserRequest): Action[AnyContent]
  def AsyncAuthenticatedAction(r: AsyncUserRequest): Action[AnyContent]
}

@Singleton
class SecuredActionsImpl @Inject()(configuration: Configuration, val authConnector: AuthConnector) extends SecuredActions {

  private val authenticatedBy = AuthenticatedBy(new CBCRAuthenticationProvider(configuration), CBCRPageVisibilityPredicate)

  override def AuthenticatedAction(r: UserRequest) = authenticatedBy(r)

  override def AsyncAuthenticatedAction(r: AsyncUserRequest) = authenticatedBy.async(r)

}

class CBCRAuthenticationProvider (configuration: Configuration) extends GovernmentGateway {

  val cbcrFrontendBaseUrl = configuration.getString("cbcr-frontend-base-url").getOrElse("")
  val governmentGatewaySignInUrl = configuration.getString("government-gateway-sign-in-url").getOrElse("")

  override def redirectToLogin(implicit request: Request[_]): Future[Result] = {

    val queryStringParams = Map("continue" -> Seq(cbcrFrontendBaseUrl + request.uri))
    Future.successful(Redirect(loginURL, queryStringParams))
  }

  def continueURL: String = "not used since we override redirectToLogin"

  def loginURL: String = governmentGatewaySignInUrl
}


object CBCRPageVisibilityPredicate extends PageVisibilityPredicate {
  def apply(authContext: AuthContext, request: Request[AnyContent]): Future[PageVisibilityResult] =
    Future.successful(PageIsVisible)
}

