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

package uk.gov.hmrc.securemessagefrontend.views.partials

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{ Lang, MessagesApi, MessagesImpl }
import play.api.test.FakeRequest
import org.scalatestplus.mockito.MockitoSugar.mock
import uk.gov.hmrc.securemessagefrontend.config.AppConfig
import uk.gov.hmrc.securemessagefrontend.models.Conversation
import uk.gov.hmrc.securemessagefrontend.views.html.Layout
import uk.gov.hmrc.securemessagefrontend.views.html.partials.recentMessages

@SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
class ArchivedMessagesViewSpec extends PlaySpec with GuiceOneAppPerSuite {
  "Archived messages template" must {
    "have all mandatory information" in new TestClass {
      val conversations = Seq(
        Conversation(
          "111",
          "/secure-messaging/conversation-messages/111",
          "HMRC exports",
          "MRN 20GB16046891253600",
          "12 January 2021"))

      val htmlString = archivedMessagesHtml.render(conversations, request, messages).toString

      htmlString must include("Filter your messages")
      htmlString must include("Select topics you want to view")
      htmlString must include("Border force cargo and inspections")
      htmlString must include("HMRC exports")
      htmlString must include("HMRC imports")
      htmlString must include("Subject")
      htmlString must include("Date")
      htmlString must include(
        "<a href=/secure-messaging/conversation-messages/111 class=\"govuk-link\" >MRN 20GB16046891253600</a>        needs action")
      htmlString must include("12 January 2021")
    }
  }

  class TestClass {
    implicit val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
    val messagesApi = app.injector.instanceOf[MessagesApi]
    implicit val messages = MessagesImpl(Lang("en"), messagesApi)
    implicit val request = FakeRequest("GET", "/")
    val layout = mock[Layout]
    val archivedMessagesHtml = new recentMessages(layout)
  }
}
