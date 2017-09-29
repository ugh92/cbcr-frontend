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

package uk.gov.hmrc.cbcrfrontend.connectors.test

import java.net.URL
import javax.inject.Inject

import uk.gov.hmrc.cbcrfrontend.WSHttp
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.http._
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.Future

trait TestRegistrationConnector {
  def insertSubscriptionData(jsonData: JsValue)(implicit hc: HeaderCarrier) : Future[HttpResponse]
}


object TestCBCRConnector extends TestRegistrationConnector with ServicesConfig{

//  val conf = config.underlying.get[Config]("microservice.services.cbcr").value
//
//  val url: String = (for {
//    proto <- conf.get[String]("protocol")
//    host <- conf.get[String]("host")
//    port <- conf.get[Int]("port")
//  } yield s"$proto://$host:$port/cbcr").value

  def insertSubscriptionData(jsonData: JsValue)(implicit hc: HeaderCarrier) : Future[HttpResponse] = {
    WSHttp.POST[JsValue, HttpResponse](s"http://localhost:9797/cbcr/test-only/insertSubscriptionData", jsonData)
  }
}


