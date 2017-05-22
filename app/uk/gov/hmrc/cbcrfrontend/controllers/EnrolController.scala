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

package uk.gov.hmrc.cbcrfrontend.controllers

import javax.inject.{Inject, Singleton}

import com.typesafe.config.Config
import configs.syntax._
import play.api.libs.ws.WSClient
import play.api.{Configuration, Logger}
import uk.gov.hmrc.cbcrfrontend.WSHttp
import uk.gov.hmrc.cbcrfrontend.auth.SecuredActions
import uk.gov.hmrc.cbcrfrontend.connectors.AuthConnector
import uk.gov.hmrc.cbcrfrontend.model.{CBCId, CBCKnownFacts, Organisation, Utr}
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.xml.Elem
/**
  * Created by max on 10/05/17.
  */
@Singleton
class EnrolController @Inject()(val sec: SecuredActions, val config:Configuration, ws:WSClient, auth:AuthConnector) extends FrontendController {

  val conf = config.underlying.get[Config]("microservice.services.gg-proxy").value

  val url: String = (for {
    host    <- conf.get[String]("host")
    port    <- conf.get[Int]("port")
    service <- conf.get[String]("url")
  } yield s"http://$host:$port/$service").value

  private def createBody(kf:CBCKnownFacts): Elem =
    <GsoAdminDeEnrolPrincipalXmlInput xmlns="urn:GSO-SystemServices:external:2.10:GsoAdminDeEnrolPrincipalXmlInput">
      <PortalIdentifier>Default</PortalIdentifier>
      <ServiceName>HMRC-CBC-ORG</ServiceName>
      <Identifiers>
        <Identifier IdentifierType="cbcId">{kf.cBCId.value}</Identifier>
        <Identifier IdentifierType="UTR">{kf.utr.value}</Identifier>
      </Identifiers>
      <KeepAgentAllocations>false</KeepAgentAllocations>
    </GsoAdminDeEnrolPrincipalXmlInput>

  private def createKF(cbcId:String,utr:String): Option[CBCKnownFacts] = for {
    id <- CBCId(cbcId)
    u  <- if(Utr(utr).isValid){ Some(Utr(utr)) } else { None }
  } yield CBCKnownFacts(u,id)

  def deEnrol(cbcId:String,utr:String) = sec.AsyncAuthenticatedAction(Some(Organisation)) { authContext => implicit request =>
    createKF(cbcId,utr) match {
      case None     => Future.successful(BadRequest)
      case Some(kf) => WSHttp.POSTString(url,createBody(kf).toString(),Seq("Content-Type" -> "application/xml")).map { response =>
        Logger.error(response.toString)
        Ok
      }.recover{
        case NonFatal(e) => InternalServerError(e.getMessage)
      }
    }
  }

  def getEnrolments = sec.AsyncAuthenticatedAction(){ authContext => implicit request =>
    auth.getEnrolments.map(Ok(_))
  }

}
