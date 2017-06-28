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

import java.time.{LocalDateTime, Year}
import scala.util.control.Exception._
import play.api.libs.json._


/** These models represent the raw data extracted from the XML file*/
sealed trait RawXmlFields extends Product with Serializable

case class RawAdditionalInfo(docSpec: RawDocSpec) extends RawXmlFields
case class RawCbcReports(docSpec: RawDocSpec) extends RawXmlFields
case class RawDocSpec(docType:String, docRefId:String, corrDocRefId:Option[String]) extends RawXmlFields
case class RawMessageSpec(messageRefID: String,
                          receivingCountry:String,
                          sendingEntityIn:String,
                          timestamp:String,
                          reportingPeriod:String) extends RawXmlFields
case class RawReportingEntity(reportingRole: String,
                              docSpec:RawDocSpec,
                              tin: String,
                              name:String) extends RawXmlFields
case class RawXMLInfo(messageSpec: RawMessageSpec,
                      reportingEntity: RawReportingEntity,
                      cbcReport: RawCbcReports,
                      additionalInfo: RawAdditionalInfo) extends RawXmlFields

/** These models represent the type-validated data, derived from the raw data */
case class DocRefId(id:String)
object DocRefId { implicit val format = Json.format[DocRefId] }

case class AdditionalInfo(docSpec: DocSpec)
object AdditionalInfo { implicit val format = Json.format[AdditionalInfo] }

case class CbcReports(docSpec: DocSpec)
object CbcReports{ implicit val format = Json.format[CbcReports] }

case class DocSpec(docType:DocTypeIndic, docRefId:DocRefId, corrDocRefId:Option[DocRefId])
object DocSpec { implicit val format = Json.format[DocSpec] }

case class MessageSpec(messageRefID: MessageRefID, receivingCountry:String, sendingEntityIn:CBCId, timestamp:LocalDateTime, reportingPeriod:Year)
object MessageSpec{
  implicit val yearFormat = new Format[Year] {
    override def reads(json: JsValue): JsResult[Year] = json match {
      case JsString(year) => (nonFatalCatch either Year.parse(year)).fold(
        _     => JsError(s"Failed to parse $year as a Year"),
        year  => JsSuccess(year)
      )
      case other => JsError(s"Failed to parse $other as a Year")
    }
    override def writes(o: Year): JsValue = JsString(o.toString)
  }
  implicit val format = Json.format[MessageSpec]
}

case class ReportingEntity(reportingRole: ReportingRole, reportingEntityDocSpec:DocSpec, tin: Utr, name: String)
object ReportingEntity{ implicit val format = Json.format[ReportingEntity] }

case class XMLInfo( messageSpec: MessageSpec, reportingEntity: ReportingEntity, cbcReport:CbcReports, additionalInfo:AdditionalInfo)
object XMLInfo { implicit val format = Json.format[XMLInfo] }