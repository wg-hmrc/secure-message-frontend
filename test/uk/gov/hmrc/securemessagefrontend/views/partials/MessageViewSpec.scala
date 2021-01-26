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
import uk.gov.hmrc.govukfrontend.views.html.components.{GovukBackLink, GovukPanel}
import uk.gov.hmrc.securemessagefrontend.config.AppConfig
import uk.gov.hmrc.securemessagefrontend.views.html.Layout
import uk.gov.hmrc.securemessagefrontend.views.html.partials.{message, messageContent}

class MessageViewSpec extends PlaySpec with GuiceOneAppPerSuite {
  "Message template" must {
    "have all mandatory information" in new TestClass {
      val messageBody = "<p class=\"govuk-body\">Dear Customer</p>\n                <p class=\"govuk-body\">I found missing information for Customs Declaration MRN 20GB16046891253600 for a shipment of fireworks.</p>\n                <p class=\"govuk-body\">Please upload this form before your shipment can be cleared for export:</p>\n                <p class=\"govuk-body\">C672 form. “This form must accompany shipments of waste as mentioned in Regulation 1013/2006 (OJ L 190) – Article 18 and Annex VII.”</p>\n                <p class=\"govuk-body\">Robin Newman</p>\n                </div>"
      val htmlBody =  messageBodyHtml.render(Some(messageBody), request, messages, appConfig)
      val messagePage =  messageHtml.render(htmlBody, request, messages, appConfig).toString

      messagePage must include("MRN 20GB16046891253600 needs action")
      messagePage must include("HMRC sent this message on 20 January 2021 at 8:23am")
      messagePage must include("Dear Customer")
      messagePage must include("MRN 20GB16046891253600")
      messagePage must include("<a href=\"/secure-message-frontend/conversations\" class=\"govuk-back-link\">Back</a>")
    }
  }

  class TestClass {
    implicit val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
    val messagesApi = app.injector.instanceOf[MessagesApi]
    implicit val messages = MessagesImpl(Lang("en"), messagesApi)
    implicit val request = FakeRequest("GET", "/")
    val layout = mock[Layout]
    val messageBodyHtml = new messageContent(layout)
    val messageHtml = new message(layout, new GovukPanel, new GovukBackLink)
  }
}


