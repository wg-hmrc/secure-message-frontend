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

package connectors

import controllers.generic.models.{ CustomerEnrolment, Tag }
import models._
import org.joda.time.DateTime
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.{ any, anyString }
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Writes
import play.api.test.Helpers._
import uk.gov.hmrc.http.{ HeaderCarrier, HttpClient, HttpReads, HttpResponse }
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ ExecutionContext, Future }

@SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
class SecureMessageConnectorSpec extends PlaySpec with MockitoSugar {

  "SecureMessgaeConnector.getConversationList" must {
    "return a list with one conversation" in new TestCase {
      val expectedQueryParams = Seq(
        ("enrolmentKey", "HMRC-CUS-ORG"),
        ("enrolment", "HMRC-CUS-ORG~EORIName~GB7777777777"),
        ("tag", "notificationType~CDS Exports")
      )
      when(
        mockHttpClient
          .GET[List[MessageHeader]](any[String], ArgumentMatchers.eq(expectedQueryParams), any[Seq[(String, String)]])(
            any[HttpReads[List[MessageHeader]]],
            any[HeaderCarrier],
            any[ExecutionContext]))
        .thenReturn(
          Future(
            List(
              MessageHeader(
                "cdcm",
                "123",
                models.MessageType.Conversation,
                "ABC",
                new DateTime(),
                None,
                unreadMessages = true,
                1))))
      private val result = await(
        connector.getMessages(
          Some(List("HMRC-CUS-ORG")),
          Some(List(CustomerEnrolment("HMRC-CUS-ORG", "EORIName", "GB7777777777"))),
          Some(List(Tag("notificationType", "CDS Exports")))))
      result.size mustBe 1
    }
  }

  "SecureMessgaeConnector.getConversation" must {
    "return a conversation" in new TestCase {
      private val testDate = DateTime.now()
      when(
        mockHttpClient
          .GET[Conversation](any[String], any[Seq[(String, String)]], any[Seq[(String, String)]])(
            any[HttpReads[Conversation]],
            any[HeaderCarrier],
            any[ExecutionContext]))
        .thenReturn(
          Future(
            Conversation(
              "client",
              "conversationId",
              "status",
              None,
              "subject",
              "en",
              List(Message(SenderInformation(Some("name"), testDate, self = false), None, "content")))))
      private val result = await(connector.getConversation("client", "conversationId"))
      result mustBe Conversation(
        "client",
        "conversationId",
        "status",
        None,
        "subject",
        "en",
        List(Message(SenderInformation(Some("name"), testDate, self = false), None, "content"))
      )
    }
  }

  "SecureMessageConnector.recordReadTime" must {
    "return" in new TestCase {
      when(
        mockHttpClient.POST[ReadTime, HttpResponse](anyString, any[ReadTime], any[Seq[(String, String)]])(
          any[Writes[ReadTime]],
          any[HttpReads[HttpResponse]],
          any[HeaderCarrier],
          any[ExecutionContext])).thenReturn(Future.successful(HttpResponse(OK, "")))

      private val result = await(connector.recordReadTime("client", "conversationId"))
      result.status must be(OK)
    }
  }

  "SecureMessageConnector.postCustomerMessage" must {

    "return true when message sent successfully" in new TestCase {
      when(
        mockHttpClient.POST[CustomerMessage, HttpResponse](anyString, any[CustomerMessage], any[Seq[(String, String)]])(
          any[Writes[CustomerMessage]],
          any[HttpReads[HttpResponse]],
          any[HeaderCarrier],
          any[ExecutionContext])).thenReturn(Future.successful(HttpResponse(CREATED, "")))
      private val result = await(connector.postCustomerMessage(aClient, aConversationId, CustomerMessage("test")))
      result mustEqual true
    }

    "return false when message fails to send" in new TestCase {
      when(
        mockHttpClient.POST[CustomerMessage, HttpResponse](anyString, any[CustomerMessage], any[Seq[(String, String)]])(
          any[Writes[CustomerMessage]],
          any[HttpReads[HttpResponse]],
          any[HeaderCarrier],
          any[ExecutionContext])).thenReturn(Future.successful(HttpResponse(BAD_REQUEST, "")))
      private val result = await(connector.postCustomerMessage(aClient, aConversationId, CustomerMessage("test")))
      result mustEqual false
    }

  }

  trait TestCase {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    val aClient: String = "cdcm"
    val aConversationId: String = "D-80542-20210308"
    val mockHttpClient: HttpClient = mock[HttpClient]
    val mockServicesConfig: ServicesConfig = mock[ServicesConfig]
    val connector = new SecureMessageConnector(mockHttpClient, mockServicesConfig)
  }
}
