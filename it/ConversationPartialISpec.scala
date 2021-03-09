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

//import com.google.inject.AbstractModule
//import connectors.SecureMessageConnector
//import com.google.inject.AbstractModule
import controllers.Assets.CREATED
//import play.api.inject.guice.GuiceableModule
//import net.codingwell.scalaguice.ScalaModule
//import play.api.inject.guice.GuiceableModule
//import models.{ Conversation, Message, SenderInformation }
//import net.codingwell.scalaguice.ScalaModule
//import org.joda.time.DateTime
//import org.mockito.ArgumentMatchers.{ any, anyString }
//import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.http.{ ContentTypes, HeaderNames }
//import play.api.inject.guice.GuiceableModule
import play.api.libs.json.{ Json, Reads }
import play.api.libs.ws.WSClient
//import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.integration.ServiceSpec

import java.io.File
//import scala.concurrent.{ ExecutionContext, Future }

@SuppressWarnings(Array("org.wartremover.warts.All"))
class ConversationPartialISpec extends PlaySpec with ServiceSpec with MockitoSugar with BeforeAndAfterEach {
  override def externalServices: Seq[String] = Seq("auth-login-api", "secure-message")
  val secureMessagePort = externalServicePorts("secure-message")

  override protected def beforeEach() = {
    (wsClient
      .url(s"http://localhost:$secureMessagePort/test-only/delete/conversation/SMF123456789/cdcm")
      .withHttpHeaders((HeaderNames.CONTENT_TYPE, ContentTypes.JSON))
      .delete
      .futureValue)
      .status mustBe 200
    ()
  }

  private val wsClient = app.injector.instanceOf[WSClient]

  "Conversation partial" must {
    "return all information" in {
      val createConversationUrl =
        s"http://localhost:$secureMessagePort/secure-messaging/conversation/cdcm/SMF123456789"

      val responseFromSecureMessage =
        wsClient
          .url(createConversationUrl)
          .withHttpHeaders((HeaderNames.CONTENT_TYPE, ContentTypes.JSON))
          .put(new File("./it/resources/create-conversation.json"))
          .futureValue
      responseFromSecureMessage.status mustBe (CREATED)

      val response = wsClient
        .url(resource("/secure-message-frontend/whatever/conversation/cdcm/SMF123456789"))
        .withHttpHeaders(AuthUtil.buildEoriToken)
        .get()
        .futureValue
      response.status mustBe 200

      val pageContent = response.body
      pageContent must include(
        "<h1 class=\"govuk-heading-l margin-top-small margin-bottom-small\">This subject needs action</h1>")
      pageContent must include("CDS Exports Team sent")
      pageContent must include("You read")
      pageContent must include("You read")
      pageContent must include("Message body!!")
      pageContent must include("reply-link")
      pageContent must include("Reply to this message")
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
