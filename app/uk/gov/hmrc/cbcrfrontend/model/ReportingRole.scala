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

package uk.gov.hmrc.cbcrfrontend.model

import cats.Show
import play.api.libs.json._

sealed trait ReportingRole

case object CBC701 extends ReportingRole
case object CBC702 extends ReportingRole
case object CBC703 extends ReportingRole

object ReportingRole {
  implicit val shows = Show.show[ReportingRole]{
    case CBC701 => "PRIMARY"
    case CBC702 => "VOLUNTARY"
    case CBC703 => "LOCAL"
  }

  implicit val format = new Format[ReportingRole] {

    override def writes(o: ReportingRole): JsValue = Json.obj("ReportingRole" -> o.toString)

    override def reads(json: JsValue): JsResult[ReportingRole] = json match {
      case o:JsObject => (for {
        rr <- o.value.get("ReportingRole")
        rs <- rr.asOpt[String]
      } yield rs match {
        case "CBC701" => JsSuccess(CBC701)
        case "CBC702" => JsSuccess(CBC702)
        case "CBC703" => JsSuccess(CBC703)
        case _        => JsError(s"Failed to parse $rs as ReportingRole")
      }).getOrElse(JsError(s"Failed to parse $json as ReportingRole"))
      case _          => JsError(s"Failed to parse $json as ReportingRole")
    }
  }

}


