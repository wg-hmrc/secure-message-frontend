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

package uk.gov.hmrc.securemessagefrontend.controllers

import akka.util.Timeout
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.i18n.{Lang, MessagesApi, MessagesImpl}
import play.api.test.Helpers.{contentAsString, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.securemessagefrontend.config.AppConfig
import uk.gov.hmrc.securemessagefrontend.views.html.partials.{archivedMessages, conversations, recentMessages}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


class ConversationsControllerSpec extends PlaySpec with GuiceOneAppPerSuite{

  "Conversations Controller" must {
    "show latest messages" in new TestCase {
      val controller = new ConversationsController(Helpers.stubMessagesControllerComponents(), conversationsPartial, recentMessagesPartial, archivedMessagesPartial)

         val result = controller.display(FakeRequest("GET", "/conversations"))
          status(result) mustBe Status.OK
         val pageContent = contentAsString(result)
        }
      }

    class TestCase {
      implicit val duration: Timeout = 5 seconds
      implicit val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
      val messagesApi = app.injector.instanceOf[MessagesApi]
      implicit val messages = MessagesImpl(Lang("en"), messagesApi)
      val conversationsPartial: conversations = app.injector.instanceOf[conversations]
      val recentMessagesPartial: recentMessages = app.injector.instanceOf[recentMessages]
      val archivedMessagesPartial: archivedMessages = app.injector.instanceOf[archivedMessages]
    }
}
