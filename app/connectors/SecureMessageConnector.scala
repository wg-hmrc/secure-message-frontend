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
import javax.inject.Inject
import models.{ Conversation, ConversationHeader }
import org.joda.time.DateTime
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{ HeaderCarrier, HttpClient, HttpResponse }
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import models.ReadTime

import scala.concurrent.{ ExecutionContext, Future }

class SecureMessageConnector @Inject()(httpClient: HttpClient, servicesConfig: ServicesConfig) {

  private val secureMessageBaseUrl = servicesConfig.baseUrl("secure-message")

  def getConversationList(
    enrolmentKeys: Option[List[String]],
    customerEnrolments: Option[List[CustomerEnrolment]],
    tags: Option[List[Tag]])(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[List[ConversationHeader]] = {
    val queryParams = queryParamsBuilder(enrolmentKeys, customerEnrolments, tags)
    httpClient
      .GET[List[ConversationHeader]](
        s"$secureMessageBaseUrl/secure-messaging/conversations",
        queryParams.getOrElse(List()))
  }

  private def queryParamsBuilder(
    enrolmentKeys: Option[List[String]],
    customerEnrolments: Option[List[CustomerEnrolment]],
    tags: Option[List[Tag]]): Option[Seq[(String, String)]] =
    for {
      keysQueryParams: List[(String, String)] <- enrolmentKeys.map(keys => keys.map(ek => ("enrolmentKey", ek)))
      enrolmentsQueryParams: List[(String, String)] <- customerEnrolments
                                                        .map(enrols =>
                                                          enrols.map(ce =>
                                                            ("enrolment", s"${ce.key}~${ce.name}~${ce.value}")))
      tagsQueryParams: List[(String, String)] <- tags.map(t => t.map(tag => ("tag", s"${tag.key}~${tag.value}")))
    } yield (keysQueryParams union enrolmentsQueryParams union tagsQueryParams)

  def getConversation(clientName: String, conversationId: String)(
    implicit ec: ExecutionContext,
    hc: HeaderCarrier): Future[Conversation] =
    httpClient.GET[Conversation](
      s"$secureMessageBaseUrl/secure-messaging/conversation/$clientName/$conversationId/HMRC-CUS-ORG/EORINumber")

  def recordReadTime(client: String, conversationId: String)(
    implicit ec: ExecutionContext,
    hc: HeaderCarrier): Future[HttpResponse] = {
    val dateTime = ReadTime(DateTime.now)
    httpClient.POST[ReadTime, HttpResponse](
      s"$secureMessageBaseUrl/secure-messaging/conversation/$client/$conversationId/read-time",
      dateTime)
  }

}
