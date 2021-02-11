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

import cats.implicits.catsSyntaxEq
import com.google.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{ Action, AnyContent, MessagesControllerComponents, Request }
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.securemessagefrontend.models.FakeData
import uk.gov.hmrc.securemessagefrontend.views.html.partials.{ message, messageContent }
import javax.inject.Singleton

@Singleton
class MessagesController @Inject()(
  controllerComponents: MessagesControllerComponents,
  messageContent: messageContent,
  message: message)
    extends FrontendController(controllerComponents) with I18nSupport {

  def display(clientService: String, client: String, conversationId: String): Action[AnyContent] = Action {
    implicit request =>
      Ok(message(messagePartial(client), clientService: String, conversationId: String))
  }

  def saveReply(clientService: String, client: String, conversationId: String): Action[AnyContent] = Action {

    Created(s"Saved reply successfull with client $clientService client $client and conversationId $conversationId")
  }

  def response(clientService: String, client: String, conversationId: String): Action[AnyContent] = Action {

    Ok(s"$clientService with client $client with conversationId $conversationId")
  }

  private def messageContent(messageId: String) = FakeData.messages.find(_.id === messageId).map(_.content)
  private def messagePartial(id: String)(implicit request: Request[_]) = messageContent(messageContent(id))
}
