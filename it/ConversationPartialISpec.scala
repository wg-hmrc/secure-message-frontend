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

import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.http.{ ContentTypes, HeaderNames }
import play.api.libs.json.{ Json, Reads }
import play.api.libs.ws.WSClient
import uk.gov.hmrc.integration.ServiceSpec
import java.io.File

import org.jsoup.Jsoup
import play.api.http.Status.{ BAD_REQUEST, CREATED, OK }

@SuppressWarnings(Array("org.wartremover.warts.All"))
class ConversationPartialISpec extends PlaySpec with ServiceSpec with MockitoSugar with BeforeAndAfterEach {
  override def externalServices: Seq[String] = Seq.empty
  val secureMessagePort: Int = 9051
  val overCharacterLimit: Int = 4001

  override protected def beforeEach() = {
    (wsClient
      .url(s"http://localhost:$secureMessagePort/test-only/delete/conversation/SMF123456789/CDCM")
      .withHttpHeaders((HeaderNames.CONTENT_TYPE, ContentTypes.JSON))
      .delete
      .futureValue)
    ()
  }

  private val wsClient = app.injector.instanceOf[WSClient]

  "Conversation partial" must {

    "return all messages within a conversation" in {

      val createConversationUrl =
        s"http://localhost:$secureMessagePort/secure-messaging/conversation/CDCM/SMF123456789"

      val responseFromSecureMessage =
        wsClient
          .url(createConversationUrl)
          .withHttpHeaders((HeaderNames.CONTENT_TYPE, ContentTypes.JSON))
          .put(new File("./it/resources/create-conversation.json"))
          .futureValue
      responseFromSecureMessage.status mustBe CREATED

      val response = wsClient
        .url(resource("/secure-message-frontend/whatever/conversation/CDCM/SMF123456789"))
        .withHttpHeaders(AuthUtil.buildEoriToken)
        .get()
        .futureValue
      response.status mustBe OK

      val pageContent = Jsoup.parse(response.body)
      pageContent
        .select("h1.govuk-heading-l.margin-top-small.margin-bottom-small")
        .text() mustBe "CDS-EXPORTS Subject"
      response.body must include("CDS Exports Team sent")
      pageContent.select("div.govuk-body").first().text() mustBe "Message body!!"
      pageContent
        .select("#reply-link > a[href]")
        .attr("href") mustBe "/whatever/conversation/CDCM/SMF123456789?showReplyForm=true#reply-form"
      pageContent
        .select("#reply-link > a[href]")
        .text() mustBe "Reply to this message"
    }

    "validates a reply text is less than 4000 characters" in {

      val createConversationUrl =
        s"http://localhost:$secureMessagePort/secure-messaging/conversation/CDCM/SMF123456789"

      val responseFromSecureMessage =
        wsClient
          .url(createConversationUrl)
          .withHttpHeaders((HeaderNames.CONTENT_TYPE, ContentTypes.JSON))
          .put(new File("./it/resources/create-conversation.json"))
          .futureValue
      responseFromSecureMessage.status mustBe CREATED

      val textLength4001: String = List.fill(overCharacterLimit)("a").mkString
      val longContent =
        s"""
           | {
           |  "content": "$textLength4001"
           |  }
        """.stripMargin
      val replyPostResponse = wsClient
        .url(resource("/secure-message-frontend/whatever/conversation/CDCM/SMF123456789"))
        .withHttpHeaders((HeaderNames.CONTENT_TYPE, ContentTypes.JSON))
        .withHttpHeaders(AuthUtil.buildEoriToken)
        .post(longContent)
        .futureValue
      replyPostResponse.status mustBe BAD_REQUEST

      val parsedContent = Jsoup.parse(replyPostResponse.body)
      parsedContent.select("span#reply-form-error").text() mustBe "Error: The message must be 4,000 characters or fewer"
      val errorSummaryList =
        parsedContent.select("div.govuk-error-summary__body ul.govuk-error-summary__list")
      errorSummaryList.tagName("li").size() mustBe 1
      errorSummaryList.select("li a").text() mustBe "The message must be 4,000 characters or fewer"
      errorSummaryList.select("li a").attr("href") mustBe "#reply-form"
    }

    "validates a reply text is non-empty" in {

      val createConversationUrl =
        s"http://localhost:$secureMessagePort/secure-messaging/conversation/CDCM/SMF123456789"

      val responseFromSecureMessage =
        wsClient
          .url(createConversationUrl)
          .withHttpHeaders((HeaderNames.CONTENT_TYPE, ContentTypes.JSON))
          .put(new File("./it/resources/create-conversation.json"))
          .futureValue
      responseFromSecureMessage.status mustBe CREATED

      val emptyContent =
        s"""
           | {
           |  "content": ""
           |  }
        """.stripMargin
      val replyEmptyPostReponse = wsClient
        .url(resource("/secure-message-frontend/whatever/conversation/CDCM/SMF123456789"))
        .withHttpHeaders((HeaderNames.CONTENT_TYPE, ContentTypes.JSON))
        .withHttpHeaders(AuthUtil.buildEoriToken)
        .post(emptyContent)
        .futureValue
      replyEmptyPostReponse.status mustBe BAD_REQUEST

      val parsedEmptyContent = Jsoup.parse(replyEmptyPostReponse.body)
      parsedEmptyContent.select("span#reply-form-error").text() mustBe "Error: You must write a message to reply"
      val errorSummaryListEmptyContent =
        parsedEmptyContent.select("div.govuk-error-summary__body ul.govuk-error-summary__list")
      errorSummaryListEmptyContent.tagName("li").size() mustBe 1
      errorSummaryListEmptyContent.select("li a").text() mustBe "You must write a message to reply"
      errorSummaryListEmptyContent.select("li a").attr("href") mustBe "#reply-form"
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

    lazy val ggAuthPort: Int = 8585

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
