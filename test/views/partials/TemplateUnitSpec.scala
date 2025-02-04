/*
 * Copyright 2022 HM Revenue & Customs
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

package views.partials

import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.{ Lang, MessagesApi, MessagesImpl }
import play.api.libs.json.Reads
import views.JsoupHelpers

abstract class TemplateUnitSpec[T: Reads] extends PlaySpec with TwirlRenderer[T] with JsoupHelpers with MockitoSugar {

  val mockMessagesApi: MessagesApi = mock[MessagesApi]
  implicit val messages: MessagesImpl = MessagesImpl(Lang("en"), mockMessagesApi)
}
