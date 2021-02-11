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

package views.viewmodels

import models.ConversationHeader
import play.api.libs.functional.syntax._
import play.api.libs.json.{ Json, OWrites, Reads, __ }

final case class ConversationInbox(
  clientService: String,
  heading: String,
  conversationHeaders: List[ConversationHeader])
object ConversationInbox {

  def defaultObject: ConversationInbox = ConversationInbox("", "", List.empty[ConversationHeader])

  implicit def jsonReads: Reads[ConversationInbox] =
    (
      (__ \ "clientService").readWithDefault[String](defaultObject.clientService) and
        (__ \ "heading").readWithDefault[String](defaultObject.heading) and
        (__ \ "conversationHeaders").readWithDefault[List[ConversationHeader]](defaultObject.conversationHeaders)
    )(ConversationInbox.apply _)

  implicit def jsonWrites: OWrites[ConversationInbox] = Json.writes[ConversationInbox]

}
