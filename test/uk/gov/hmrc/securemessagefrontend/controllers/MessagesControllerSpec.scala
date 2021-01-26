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
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, status, stubMessagesControllerComponents}
import uk.gov.hmrc.securemessagefrontend.config.AppConfig
import uk.gov.hmrc.securemessagefrontend.views.html.partials.{message, messageContent}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

class MessagesControllerSpec extends PlaySpec with GuiceOneAppPerSuite{

  "Message Controller" must {
    "show message partial" in new TestCase {
      val controller  =  new MessagesController(stubMessagesControllerComponents(),messageContent, message)
      val result = controller.display("111")(FakeRequest("GET", "/conversation-message/111"))

     status(result) mustBe Status.OK
     val pageContent = contentAsString(result)
      pageContent must include("MRN 20GB16046891253600 needs action")
      pageContent must include("HMRC sent this message on 20 January 2021 at 8:23am")
      pageContent must include("Dear Customer")
    }
  }

  class TestCase {

    implicit val duration: Timeout = 5 seconds
    val messageContent = app.injector.instanceOf[messageContent]
    val message = app.injector.instanceOf[message]
    implicit val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

  }

}
