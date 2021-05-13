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

import models.{ FirstReaderInformation, MessageHeader, MessageType }
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

  "Inbox creation date" must {
    "return correct date if date is not today" in {
      getMessageDate(
        MessageHeader(
          MessageType.Conversation,
          "",
          "",
          DateTime.parse("2021-02-19T10:29:47.275Z"),
          None,
          false,
          1,
          Some(""),
          Some(""))) must be("19 February 2021")
    }

    "return just time if message creation is today" in {
      val dateTime = DateTime.parse(s"${DateTime.now().toLocalDate}T05:29:47.275Z")
      val messageDateOrTime =
        getMessageDate(MessageHeader(MessageType.Conversation, "", "", dateTime, None, false, 1, Some(""), Some("")))
      messageDateOrTime.takeRight(5) must be(":29am")
    }
  }

  "Inbox link url" must {
    val id = "60995694180000c223edb0b9"
    "return with clientName and  conversation for a conversation" in {
      getMessageUrl(
        "someclient",
        MessageHeader(
          MessageType.Conversation,
          id,
          "subject",
          DateTime.now(),
          None,
          false,
          1,
          Some("111"),
          Some("CDCM"))) mustBe "/someclient/conversation/CDCM/111"
    }
    "with id for letter" in {
      getMessageUrl(
        "someclient",
        MessageHeader(MessageType.Letter, id, "subject", DateTime.now(), None, false, 1, None, None)) mustBe s"/someclient/messages/$id"
    }
  }

}
