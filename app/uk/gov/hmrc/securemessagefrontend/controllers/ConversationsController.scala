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

import play.api.i18n.I18nSupport
import play.api.mvc.{ MessagesControllerComponents, _ }
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.securemessagefrontend.models.FakeData
import uk.gov.hmrc.securemessagefrontend.views.html.partials.{ archivedMessages, conversations, recentMessages }
import javax.inject.{ Inject, Singleton }

@Singleton
class ConversationsController @Inject()(
  controllerComponents: MessagesControllerComponents,
  conversationsPartial: conversations,
  recentMessages: recentMessages,
  archivedMessages: archivedMessages)
    extends FrontendController(controllerComponents) with I18nSupport {

  def display(clientService: String): Action[AnyContent] = Action { implicit request =>
    Ok(conversationsPartial(latestMessagesPartial(clientService), archivedMessagesPartial(clientService)))
  }

  private def latestMessagesPartial(clientService: String)(implicit request: Request[_]) =
    recentMessages(FakeData.conversations(clientService))
  private def archivedMessagesPartial(clientService: String)(implicit request: Request[_]) =
    archivedMessages(FakeData.conversations(clientService))
}
