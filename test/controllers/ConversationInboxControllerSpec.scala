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

package controllers

import akka.util.Timeout
import config.AppConfig
import connectors.SecureMessageConnector
import models.ConversationHeader
import org.joda.time.DateTime
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.http.Status._
import play.api.i18n.{ Lang, Messages, MessagesApi, MessagesImpl }
import play.api.test.Helpers.{ GET, status }
import play.api.test.{ FakeRequest, Helpers }
import play.twirl.api.Html
import uk.gov.hmrc.http.HeaderCarrier
import views.html.partials.conversationInbox
import views.viewmodels.ConversationInbox

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }

@SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
class ConversationInboxControllerSpec extends PlaySpec with MockitoSugar with MockAuthConnector {

  "Conversations Controller" must {
    "show latest messages" in new TestCase {
      mockAuthorise[Unit]()(Future.successful(()))
      when(mockSecureMessageConnector.getConversationList()(any[ExecutionContext], any[HeaderCarrier]))
        .thenReturn(
          Future(
            List(
              ConversationHeader(
                "cdcm",
                "D-80542-20201120",
                "DMS7324874993",
                new DateTime(),
                Some("CDS Exports Team"),
                unreadMessages = true,
                1))))
      when(mockConversationsInboxPartial.apply(any[ConversationInbox])(any[Messages])).thenReturn(new Html("test"))
      private val controller = new ConversationInboxController(
        mockAppConfig,
        Helpers.stubMessagesControllerComponents(),
        mockConversationsInboxPartial,
        mockSecureMessageConnector,
        mockAuthConnector)
      private val result = controller.display("cds-frontend", None)(FakeRequest(method = GET, path = "/conversations"))
      status(result) mustBe OK
    }
  }

  class TestCase {
    implicit val duration: Timeout = 5 seconds
    implicit val mockAppConfig: AppConfig = mock[AppConfig]
    val mockMessagesApi: MessagesApi = mock[MessagesApi]
    implicit val messages: MessagesImpl = MessagesImpl(Lang("en"), mockMessagesApi)
    val mockConversationsInboxPartial: conversationInbox = mock[conversationInbox]
    val mockSecureMessageConnector: SecureMessageConnector = mock[SecureMessageConnector]

  }
}
