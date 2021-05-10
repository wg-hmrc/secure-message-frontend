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

package views.partials

import models.{ Letter, Sender }
import org.joda.time.LocalDate
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.PlaySpec
import play.api.i18n.{ Lang, MessagesApi, MessagesImpl }
import play.api.mvc.Request
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.html.components.{ GovukBackLink, GovukPanel }
import views.helpers.HtmlUtil.readableDate
import views.html.Layout
import views.html.partials.letterView

@SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
class letterViewSpec extends PlaySpec {
  "letterView template" must {
    "have message content with subject and sent date" in new TestClass {
      private val localDate = LocalDate.now()
      private val conversationContent =
        new letterView(layout, new GovukPanel, new GovukBackLink)(
          Letter("MRN 123", "CDS message", None, Sender("HMRC", localDate), None)
        ).toString
      conversationContent must include("MRN 123")
      conversationContent must include("CDS message")
      conversationContent must include("HMRC")
      conversationContent must include(readableDate(localDate))
    }
  }

  class TestClass {
    implicit val messages: MessagesImpl = MessagesImpl(Lang("en"), mock[MessagesApi])
    implicit val request: Request[_] = FakeRequest("GET", "/")
    val layout: Layout = mock[Layout]
  }
}
