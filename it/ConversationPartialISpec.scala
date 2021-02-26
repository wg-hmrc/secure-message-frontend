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
import controllers.Assets.{ CREATED, OK }
import net.codingwell.scalaguice.ScalaModule
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.http.{ ContentTypes, HeaderNames }
import play.api.inject.guice.GuiceableModule
import play.api.libs.json.{ Json, Reads }
import play.api.libs.ws.WSClient
import uk.gov.hmrc.integration.ServiceSpec
import java.io.File

@SuppressWarnings(Array("org.wartremover.warts.All"))
class ConversationPartialISpec extends PlaySpec with ServiceSpec with MockitoSugar with BeforeAndAfterEach {
  override def externalServices: Seq[String] = Seq("auth-login-api", "secure-message", "secure-message-frontend")

  private val mockSecureMessageConnector = mock[SecureMessageConnector]

  private val wsClient = app.injector.instanceOf[WSClient]

  override def additionalOverrides: Seq[GuiceableModule] =
    Seq(new AbstractModule with ScalaModule {
      override def configure(): Unit =
        bind[SecureMessageConnector].toInstance(mockSecureMessageConnector)
    })
  "Given a conversation from secure message" must {
    "return conversation partial" in {

      lazy val secureMessagePort = externalServicePorts("secure-message")
      lazy val createConversationUrl =
        s"http://localhost:$secureMessagePort/secure-messaging/conversation/cdcm/SMF123456789"
      lazy val responseFromSecureMessage =
        wsClient
          .url(createConversationUrl)
          .withHttpHeaders((HeaderNames.CONTENT_TYPE, ContentTypes.JSON))
          .put(new File("./it/resources/create-conversation.json"))
          .futureValue
      responseFromSecureMessage.status mustBe (CREATED)

      lazy val secureMessageFrontendPort = externalServicePorts("secure-message-frontend")
      lazy val getConverstionUrl =
        s"http://localhost:$secureMessageFrontendPort/secure-message-frontend/whatever/conversation/cdcm/SMF123456789"
      lazy val responseFromSecureMessageFrontend = wsClient
        .url(getConverstionUrl)
        .withHttpHeaders(AuthUtil.buildEoriToken)
        .get()
        .futureValue
      responseFromSecureMessageFrontend.status mustBe (OK)
      lazy val pageContent = responseFromSecureMessageFrontend.body
      pageContent must include("Back")
      pageContent must include("govuk-back-link")
      pageContent must include("This subject needs action")
      pageContent must include(
        "<span class=\"govuk-caption-m-!-govuk-body govuk-!-font-weight-bold\">CDS Exports Team sent</span>  this message on ")
      pageContent must include(
        "<span class=\"govuk-caption-m-!-govuk-body govuk-!-font-weight-bold\">You read</span>      this message on")
      pageContent must include("govuk-body")
      pageContent must include("Message body!!")
      val deleteResponse = wsClient
        .url(s"http://localhost:$secureMessagePort/delete/conversation/SMF123456789/cdcm")
        .withHttpHeaders((HeaderNames.CONTENT_TYPE, ContentTypes.JSON))
        .delete
        .futureValue

      deleteResponse.status mustBe (OK)

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
