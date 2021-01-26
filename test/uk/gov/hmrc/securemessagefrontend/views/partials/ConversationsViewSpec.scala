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

import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Lang, MessagesApi, MessagesImpl}
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.html.components.{GovukAccordion, GovukBackLink}
import uk.gov.hmrc.securemessagefrontend.config.AppConfig
import uk.gov.hmrc.securemessagefrontend.models.Conversation
import uk.gov.hmrc.securemessagefrontend.views.html.Layout
import uk.gov.hmrc.securemessagefrontend.views.html.partials.{archivedMessages, conversations, recentMessages}



class ConversationsViewSpec extends PlaySpec with GuiceOneAppPerSuite {

  "Recent messages template" must {
    "have all mandatory information" in new TestClass {

      val recentConversations = Seq(Conversation("111", "/secure-messaging/conversation-messages/111",  "HMRC exports", "MRN 20GB16046891253600", "12 January 2021"))
      val archieveConversations = Seq(Conversation("222", "/secure-messaging/conversation-messages/222",  "HMRC exports", "MRN 30GB16046891253600", "13 January 2020"))

      val recentMessagesPartial =  recentMessagesHtml.render(recentConversations, request, messages, appConfig)
      val archieveMessagesPartial =  archiveMessagesHtml.render(archieveConversations, request, messages, appConfig)

      val htmlContent =  conversationsTemplate.render(recentMessagesPartial, archieveMessagesPartial, request, messages, appConfig).toString

      htmlContent must include("Messages between you and HMRC")
      htmlContent must include("Your messages")
      htmlContent must include("Recent and unread messages")
      htmlContent must include("Filter your messages")
      htmlContent must include("Select topics you want to view")
      htmlContent must include("Border force cargo and inspections")
      htmlContent must include("HMRC exports")
      htmlContent must include("HMRC imports")
      htmlContent must include("<a href=/secure-messaging/conversation-messages/111 class=\"govuk-link\" >MRN 20GB16046891253600</a>        needs action")
      htmlContent must include("<a href=/secure-messaging/conversation-messages/222 class=\"govuk-link\" >MRN 30GB16046891253600</a>        needs action")
      htmlContent must include("12 January 2021")
      htmlContent must include("13 January 2020")
    }
  }

  class TestClass {
    implicit val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
    val messagesApi = app.injector.instanceOf[MessagesApi]
    implicit val messages = MessagesImpl(Lang("en"), messagesApi)
    implicit val request = FakeRequest("GET", "/")
    val layout = mock[Layout]
    val recentMessagesHtml = new recentMessages(layout)
    val archiveMessagesHtml = new archivedMessages(layout)
    val conversationsTemplate = new conversations(layout, new GovukAccordion, new GovukBackLink)
  }
}
