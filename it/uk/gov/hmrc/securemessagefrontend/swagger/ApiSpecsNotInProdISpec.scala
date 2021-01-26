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

package uk.gov.hmrc.securemessagefrontend.swagger

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpClient

class ApiSpecsNotInProdISpec extends PlaySpec with GuiceOneAppPerSuite {

  override lazy val app = new GuiceApplicationBuilder()
    .configure(
      "play.filters.csrf.header.bypassHeaders.Csrf-Token" -> "nocheck",
      "play.http.router"                                  -> "prod.Routes"
    )
    .build()

  val http = app.injector.instanceOf[HttpClient]

  "calling the swagger route on production" must {

    "should not return an OK response" in {

      val fakeRequest = FakeRequest(GET, s"/secure-message-frontend/api/schema.json")
        .withHeaders("Csrf-Token" -> "nocheck")

      route(app, fakeRequest).map(status(_) must not be (OK))
    }
  }
}
