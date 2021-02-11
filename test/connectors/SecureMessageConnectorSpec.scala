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

import models.ConversationHeader
import org.joda.time.DateTime
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.test.Helpers._
import uk.gov.hmrc.http.{ HeaderCarrier, HttpClient, HttpReads }
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ ExecutionContext, Future }

@SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
class SecureMessageConnectorSpec extends PlaySpec with MockitoSugar {

  "SecureMessgaeConnector.getConversationList" must {
    "return a list with one conversation" in new TestCase {
      when(
        mockHttpClient
          .GET[List[ConversationHeader]](any[String], any[Seq[(String, String)]], any[Seq[(String, String)]])(
            any[HttpReads[List[ConversationHeader]]],
            any[HeaderCarrier],
            any[ExecutionContext]))
        .thenReturn(Future(List(ConversationHeader("cdcm", "123", "ABC", new DateTime(), None, true, 1))))
      private val result = await(connector.getConversationList())
      result.size mustBe 1
    }
  }

  trait TestCase {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    val mockHttpClient: HttpClient = mock[HttpClient]
    val mockServicesConfig: ServicesConfig = mock[ServicesConfig]
    val connector = new SecureMessageConnector(mockHttpClient, mockServicesConfig)
  }

}
