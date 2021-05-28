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

package controllers

import com.google.inject.Inject
import connectors.SecureMessageConnector
import forms.MessageFormProvider
import javax.inject.Singleton
import models.{ Conversation, CustomerMessage, Message }
import play.api.Logging
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{ Action, AnyContent, MessagesControllerComponents, Request, Result }
import play.twirl.api.Html
import uk.gov.hmrc.auth.core.{ AuthConnector, AuthorisedFunctions }
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.panel.Panel
import uk.gov.hmrc.http.{ HeaderCarrier, NotFoundException }
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import views.helpers.HtmlUtil._
import views.html.partials.{ conversationView, letterView, messageContent, messageReply, messageResult }
import views.viewmodels.{ ConversationView, MessageReply, MessageView }
import scala.concurrent.{ ExecutionContext, Future }

@SuppressWarnings(Array("org.wartremover.warts.All"))
@Singleton
class ConversationController @Inject()(
  controllerComponents: MessagesControllerComponents,
  secureMessageConnector: SecureMessageConnector,
  messageContent: messageContent,
  messageReply: messageReply,
  messageResult: messageResult,
  conversationView: conversationView,
  letterView: letterView,
  val authConnector: AuthConnector,
  formProvider: MessageFormProvider)(implicit ec: ExecutionContext)
    extends FrontendController(controllerComponents) with I18nSupport with AuthorisedFunctions with Logging {

  def display(
    clientService: String,
    client: String,
    conversationId: String,
    showReplyForm: Boolean
  ): Action[AnyContent] = Action.async { implicit request =>
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequest(request)
    val replyFormActionUrl = s"/$clientService/conversation/$client/$conversationId"
    val replyFormUrl = s"$replyFormActionUrl?showReplyForm=true#reply-form"
    authorised() {
      secureMessageConnector
        .getConversation(client, conversationId)
        .flatMap { conversation =>
          val messages = {
            messagePartial(conversation.messages)
          }
          val firstMessage = messages.headOption.getOrElse(
            throw new NotFoundException("There can't be a conversation without a message"))
          val replyForm =
            messageReply(
              MessageReply(showReplyForm, replyFormActionUrl, getReplyIcon(replyFormUrl), Seq.empty[String], ""))
          Future.successful(
            Ok(
              conversationView(
                ConversationView(
                  conversation.subject,
                  firstMessage,
                  replyForm,
                  messages.tail,
                  Seq.empty[String]
                ))))
        }
    }
  }

  private def getConversationView(conversation: Conversation, replyForm: Html)(
    implicit request: Request[_]): ConversationView = {

    val messages = {
      messagePartial(conversation.messages)
    }
    val firstMessage =
      messages.headOption.getOrElse(throw new NotFoundException("There can't be a conversation without a message"))
    ConversationView(
      conversation.subject,
      firstMessage,
      replyForm,
      messages.tail,
      Seq.empty[String]
    )
  }

  val form: Form[CustomerMessage] = formProvider()

  def saveReply(clientService: String, client: String, conversationId: String): Action[AnyContent] = Action.async {
    implicit request =>
      implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequest(request)
      val replyFormActionUrl = s"/$clientService/conversation/$client/$conversationId"
      val replyFormUrl = s"$replyFormActionUrl?showReplyForm=true#reply-form"
      authorised() {
        form
          .bindFromRequest()
          .fold(
            form => {
              secureMessageConnector
                .getConversation(client, conversationId)
                .flatMap {
                  conversation =>
                    val messages = {
                      messagePartial(conversation.messages)
                    }
                    val firstMessage = messages.headOption.getOrElse(
                      throw new NotFoundException("There can't be a conversation without a message"))
                    val replyForm =
                      messageReply(
                        MessageReply(
                          showReplyForm = true,
                          replyFormActionUrl,
                          getReplyIcon(replyFormUrl),
                          form.errors.map(_.message),
                          content = form.data.getOrElse("content", "")))
                    Future.successful(
                      BadRequest(
                        conversationView(
                          ConversationView(
                            conversation.subject,
                            firstMessage,
                            replyForm,
                            messages.tail,
                            form.errors.map(_.message)))))
                }

            },
            message =>
              secureMessageConnector.postCustomerMessage(client, conversationId, message).map { sent =>
                val redirectURL = s"/$clientService/conversation/$client/$conversationId/result"
                if (sent) Ok(redirectURL) else BadGateway("Failed to send message")
            }
          )
      }
  }

  def result(clientService: String, client: String, conversationId: String): Action[AnyContent] = Action {
    // FIXME - log statement is just to keep compiler happy - do we really need these parameters?
    logger.debug(s"service: $clientService, client: $client, conversation: $conversationId")
    Ok(
      messageResult(
        s"/$clientService/messages",
        Panel(
          title = Text("Message sent"),
          content = Text("We received your message")
        )))
  }

  def displayMessage(
    clientService: String,
    rawId: String
  ): Action[AnyContent] = Action.async { implicit request =>
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequest(request)
    logger.debug(s"service: $clientService")

    def contentHelper[A](content: String => Future[A], view: A => Html): Future[Result] =
      content(rawId).map { a =>
        Ok(view(a))
      }

    rawId match {
      case Id("letter", _) =>
        authorised() { contentHelper(secureMessageConnector.getLetterContent _, letterView.apply _) }
      case Id("conversation", _) =>
        authorised() {
          contentHelper((s: String) => {
            secureMessageConnector.getConversationContent(s).map(getConversationView(_, Html("")))
          }, conversationView.apply _)
        }
      case _ => Future.successful(BadRequest("Invalid URL path"))
    }

  }

  private[controllers] def messagePartial(messages: List[Message])(implicit request: Request[_]) =
    messages
      .sortBy(_.senderInformation.sent.getMillis)(Ordering[Long].reverse)
      .map(message =>
        messageContent(MessageView(
          senderName(message.senderInformation),
          sentMessageConversationText(readableTime(message.senderInformation.sent, "en")),
          firstReadMessageConversationText(message.firstReader),
          readMessageConversationText,
          decodeBase64String(message.content),
          message.senderInformation.self
        )))

  object Id {
    def apply(messageType: String, id: String): String =
      encodeBase64String(s"$messageType/$id")

    def unapply(s: String): Option[(String, String)] = decodeBase64String(s).split("/").toList match {
      case messageType :: id :: _ => Some((messageType, id))
      case _                      => None
    }
  }

}
