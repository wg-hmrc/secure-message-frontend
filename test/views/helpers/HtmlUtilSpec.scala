/*
 * Copyright 2021 HM Revenue & Customs
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

package views.helpers

import models.{ ConversationHeader, FirstReaderInformation }
import org.joda.time.DateTime
import org.scalatestplus.play.PlaySpec
import views.helpers.HtmlUtil._

@SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
class HtmlUtilSpec extends PlaySpec {

  "conversation readableTime function returns correct readable timestamp" in {
    readableTime(DateTime.parse("2021-02-19T10:29:47.275Z")) must be("19 February 2021 at 10:29am")
  }

  "readMessageConversationText function returns correct read message text" in {
    val testTime = DateTime.now
    readMessageConversationText must be(s"this on ${readableTime(testTime)}")
  }

  "firstReadMessageConversationText function returns correct first read message text" in {
    val testTime = DateTime.now
    val firstReader = Some(FirstReaderInformation(Some("Name"), testTime))
    firstReadMessageConversationText(firstReader) must be(Some(s"on ${readableTime(testTime)}"))
  }

  "decodeBase64String function should return decoded string" in {
    decodeBase64String("V2hhdCBhIGRheSE=") mustBe "What a day!"
  }

  "conversation Inbox creation date" must {
    "return correct date if date is not today" in {
      getMessageDate(ConversationHeader("", "", "", DateTime.parse("2021-02-19T10:29:47.275Z"), None, false, 1)) must be(
        "19 February 2021")
    }

    "return just time if its today today" in {
      val dateTime = DateTime.parse(s"${DateTime.now().toLocalDate}T05:29:47.275Z")
      getMessageDate(ConversationHeader("", "", "", dateTime, None, false, 1)) mustBe ("5:29am")
    }
  }
}
