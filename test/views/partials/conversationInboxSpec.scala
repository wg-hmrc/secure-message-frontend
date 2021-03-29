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

package views.partials

import play.twirl.api.HtmlFormat
import views.html.partials.conversationInbox
import views.viewmodels.ConversationInbox

import scala.util.{ Success, Try }

@SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
class conversationInboxSpec extends TemplateUnitSpec[ConversationInbox] {

  "A conversation inbox template" must {
    "have all mandatory information" in {
      render(ConversationInbox("cds-frontend", "test", 5, 1, List.empty)) match {
        case Success(component) => {
          val pageContent = component.body
          pageContent must include("""<h1 class="govuk-heading-xl">test</h1>""")
          pageContent must include("""<span class="govuk-visually-hidden"> 1 unread</span>""")
          pageContent must include("""<span  class="govuk-visually-hidden"> 5 in total </span>""")
          pageContent must include(
            """<div class="header-status"><span aria-hidden="true" class="govuk-visually-hidden" ></span></div>""")
          pageContent must include("""<div class="govuk-!-font-weight-bold header-subject"></div>""")
          pageContent must include("""<div class="govuk-!-font-weight-bold header-date"></div>""")
        }
        case _ => fail("There was a problem reading the test output")
      }
    }
  }

  /**
    * Calls the Twirl template with the given parameters and returns the resulting markup
    *
    * @param templateParams
    * @return [[Try[HtmlFormat.Appendable]]] containing the markup
    */
  override def render(templateParams: ConversationInbox): Try[HtmlFormat.Appendable] = {
    val inbox = new conversationInbox
    Try(inbox(templateParams))
  }

}
