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

import javax.inject.Inject
import models.ConversationHeader
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{ HeaderCarrier, HttpClient }
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import scala.concurrent.{ ExecutionContext, Future }

class SecureMessageConnector @Inject()(httpClient: HttpClient, servicesConfig: ServicesConfig) {

  private val secureMessageBaseUrl = servicesConfig.baseUrl("secure-message")

  def getConversationList()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[List[ConversationHeader]] =
    httpClient.GET[List[ConversationHeader]](
      s"$secureMessageBaseUrl/secure-messaging/conversations/HMRC-CUS-ORG/EORINumber")

}
