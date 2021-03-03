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

import com.google.inject.AbstractModule
import connectors.SecureMessageConnector
import models.{ Conversation, Message, SenderInformation }
import net.codingwell.scalaguice.ScalaModule
import org.joda.time.DateTime
import org.mockito.ArgumentMatchers.{ any, anyString }
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.inject.guice.GuiceableModule
import play.api.libs.json.{ Json, Reads }
import play.api.libs.ws.WSClient
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.integration.ServiceSpec
import scala.concurrent.{ ExecutionContext, Future }

@SuppressWarnings(Array("org.wartremover.warts.All"))
class ConversationPartialISpec extends PlaySpec with ServiceSpec with MockitoSugar with BeforeAndAfterEach {
  override def externalServices: Seq[String] = Seq("auth-login-api")

  private val mockSecureMessageConnector = mock[SecureMessageConnector]

  private val wsClient = app.injector.instanceOf[WSClient]

  private val testTime = DateTime.parse("2021-02-19T10:29:47.275Z")

  override def additionalOverrides: Seq[GuiceableModule] =
    Seq(new AbstractModule with ScalaModule {
      override def configure(): Unit =
        bind[SecureMessageConnector].toInstance(mockSecureMessageConnector)
    })
  "Given a conversation from secure message" must {
    "return conversation partial" in {
      when(mockSecureMessageConnector.getConversation(anyString, anyString)(any[ExecutionContext], any[HeaderCarrier]))
        .thenReturn(
          Future.successful(
            Conversation(
              "client",
              "conversationId",
              "status",
              None,
              "subject",
              "en",
              List(Message(SenderInformation(Some(""), testTime, false), None, "TWVzc2FnZSBib2R5IQ==")))))
      val response = wsClient
        .url(resource("/secure-message-frontend/cdcm/conversation/client/1111"))
        .withHttpHeaders(AuthUtil.buildEoriToken)
        .get()
        .futureValue
      response.status mustBe 200
      val pageContent = response.body
      pageContent must include("Back")
      pageContent must include("govuk-back-link")
      pageContent must include("subject")
      pageContent must include(
        "<span class=\"govuk-caption-m-!-govuk-body govuk-!-font-weight-bold\"> sent</span>  this on 19 February 2021 at 10:29am")
      pageContent must include(
        "<span class=\"govuk-caption-m-!-govuk-body govuk-!-font-weight-bold\">You read</span>      this on")
      pageContent must include("govuk-body")
      pageContent must include("Message body!")

    }

    "return messages in cronological order of creation with latest being on top" in {
      val dateRegex = """(\d\d)\s(January|February|March)\s(2020|2021)""".r
      val messages = List(
        Message(
          SenderInformation(Some(""), DateTime.parse("2021-01-19T10:29:47.275Z"), false),
          None,
          "TWVzc2FnZSBib2R5IQ=="),
        Message(
          SenderInformation(Some(""), DateTime.parse("2021-03-19T10:29:47.275Z"), false),
          None,
          "TWVzc2FnZSBib2R5IQ=="),
        Message(
          SenderInformation(Some(""), DateTime.parse("2021-02-19T10:29:47.275Z"), false),
          None,
          "TWVzc2FnZSBib2R5IQ==")
      )

      when(mockSecureMessageConnector.getConversation(anyString, anyString)(any[ExecutionContext], any[HeaderCarrier]))
        .thenReturn(
          Future.successful(Conversation("client", "conversationId", "status", None, "subject", "en", messages)))
      val response = wsClient
        .url(resource("/secure-message-frontend/cdcm/conversation/client/1111"))
        .withHttpHeaders(AuthUtil.buildEoriToken)
        .get()
        .futureValue
      response.status mustBe 200

      dateRegex.findAllIn(response.body).size mustBe (3)
      dateRegex.findFirstIn(response.body).get mustBe ("19 March 2021")
    }
  }

  object AuthUtil {

    private val wsClient = app.injector.instanceOf[WSClient]

    val payload = ""

    wsClient
      .url(s"http://localhost:$ggAuthPort/government-gateway/session/login")
      .withHttpHeaders(("Content-Type", "application/json"))
      .post(payload)
      .futureValue

    lazy val ggAuthPort: Int = externalServicePorts("auth-login-api")

    implicit val deserialiser: Reads[GatewayToken] = Json.reads[GatewayToken]

    case class GatewayToken(gatewayToken: String)

    private val NO_EORI_USER_PAYLOAD =
      """
        | {
        |  "credId": "1235",
        |  "affinityGroup": "Organisation",
        |  "confidenceLevel": 100,
        |  "credentialStrength": "none",
        |  "enrolments": []
        |  }
     """.stripMargin

    private val EORI_USER_PAYLOAD =
      """
        | {
        |  "credId": "1235",
        |  "affinityGroup": "Organisation",
        |  "confidenceLevel": 200,
        |  "credentialStrength": "none",
        |  "enrolments": [
        |      {
        |        "key": "HMRC-CUS-ORG",
        |        "identifiers": [
        |          {
        |            "key": "EORINumber",
        |            "value": "GB1234567890"
        |          }
        |        ],
        |        "state": "Activated"
        |      }
        |    ]
        |  }
     """.stripMargin

    private def buildUserToken(payload: String): (String, String) = {
      val response = wsClient
        .url(s"http://localhost:$ggAuthPort/government-gateway/session/login")
        .withHttpHeaders(("Content-Type", "application/json"))
        .post(payload)
        .futureValue

      ("Authorization", response.header("Authorization").getOrElse(""))
    }

    def buildEoriToken: (String, String) = buildUserToken(EORI_USER_PAYLOAD)
    def buildNonEoriToken: (String, String) = buildUserToken(NO_EORI_USER_PAYLOAD)
  }

}
