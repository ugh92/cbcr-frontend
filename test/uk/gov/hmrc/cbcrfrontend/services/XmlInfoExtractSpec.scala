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

package uk.gov.hmrc.cbcrfrontend.services

import java.io.{File, FileInputStream}

import uk.gov.hmrc.play.test.UnitSpec

class XmlInfoExtractSpec extends UnitSpec {

  private def loadFile(filename: String) = new File(s"test/resources/$filename")

  "An XmlInfoExtract object" should {
    "provide an extract method what extracts the correct information from an xml" in {
      val f = loadFile("cbcr-valid-dup.xml")

      val xmlInfoExtract = new XmlInfoExtract()

      val e = xmlInfoExtract.extract(f)

      e.messageSpec.messageRefID shouldBe "GB2016RGXVCBC0000000056CBC40120170311T090000X"
      e.messageSpec.receivingCountry shouldBe "GB"
      e.messageSpec.reportingPeriod shouldBe "2016-03-31"
      e.messageSpec.timestamp shouldBe "2016-11-01T15:00:00"

      e.xmlEncoding.xmlEncodingVal shouldBe "UTF-8"
      e.cbcVal.cbcVer shouldBe "1.0"

      val re = e.reportingEntity
      re.name shouldBe "ABCCorp"
      re.tin shouldBe "7000000002"
      re.docSpec.docType shouldBe "OECD1"
      re.docSpec.docRefId shouldBe "String_DocRefId1"
      re.docSpec.corrDocRefId shouldBe Some("String_CorrDocRefId")

      val r = e.cbcReport

      r.docSpec.docType shouldBe "OECD1"
      r.docSpec.docRefId shouldBe "MyDocRefId"
      r.docSpec.corrDocRefId shouldBe Some("String_CorrDocRefId")

      val a = e.additionalInfo

      a.docSpec.docType shouldBe "OECD1"
      a.docSpec.docRefId shouldBe "MyDocRefId"
      a.docSpec.corrDocRefId shouldBe Some("MyCorrDocRefId")

    }
  }

}
