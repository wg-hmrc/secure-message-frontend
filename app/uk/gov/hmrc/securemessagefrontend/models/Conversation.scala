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

package uk.gov.hmrc.securemessagefrontend.models

final case class Conversation(id: String, url: String, topic: String, mrn: String, date: String)

final case class ConversationMessage(id: String, content: String)

object FakeData {
  val conversations: Seq[Conversation] = List(
    Conversation("111", "/conversation-message/111", "HMRC exports", "MRN 20GB16046891253600", "12 January 2021"),
    Conversation("222", "/conversation-message/222", "HMRC exports", "MRN 20GB16046891253601", "13 January 2021"),
    Conversation("333", "/conversation-message/222", "HMRC exports", "MRN 20GB16046891253601", "13 January 2021"),
    Conversation("444", "/conversation-message/222", "HMRC exports", "MRN 20GB16046891253601", "13 January 2021"),
    Conversation("555", "/conversation-message/222", "HMRC exports", "MRN 20GB16046891253601", "13 January 2021"),
    Conversation("666", "/conversation-message/222", "HMRC exports", "MRN 20GB16046891253601", "13 January 2021")
  )

  val messages: Seq[ConversationMessage] = List(
    ConversationMessage(
      "111",
      "<div>\n <p class=\"govuk-body\">Dear Customer</p>\n         " +
        " <p class=\"govuk-body\">I found missing information for Customs Declaration MRN 20GB16046891253600" +
        " for a shipment of fireworks.</p>\n" +
        "<p class=\"govuk-body\">Please upload this form before your shipment can be cleared for export:</p>\n  " +
        "              <p class=\"govuk-body\">C672 form. “This form must accompany shipments of waste as mentioned in" +
        " Regulation 1013/2006 (OJ L 190) – Article 18 and Annex VII.”</p>\n       " +
        "         <p class=\"govuk-body\">Robin Newman</p>\n                </div>"
    ),
    ConversationMessage(
      "222",
      "           <div>\n   " +
        "             <p class=\"govuk-body\">Dear Customer</p>\n        " +
        "        <p class=\"govuk-body\">I found missing information for Customs Declaration " +
        "MRN 20GB16046891253600 for a shipment of fireworks.</p>\n          " +
        "      <p class=\"govuk-body\">Please upload this form before your shipment can be cleared for export:</p>\n   " +
        "             <p class=\"govuk-body\">C672 form. “This form must accompany shipments" +
        " of waste as mentioned in Regulation 1013/2006 (OJ L 190) – Article 18 and Annex VII.”</p>\n     " +
        "           <p class=\"govuk-body\">Robin Newman</p>\n                </div>"
    ),
    ConversationMessage(
      "333",
      "           <div>\n           " +
        "     <p class=\"govuk-body\">Dear Customer</p>\n      " +
        "          <p class=\"govuk-body\">I found missing information for Customs Declaration " +
        "MRN 20GB16046891253600 for a shipment of fireworks.</p>\n     " +
        "           <p class=\"govuk-body\">Please upload this form before your shipment can be cleared for export:</p>\n " +
        "               <p class=\"govuk-body\">C672 form. " +
        "“This form must accompany shipments of waste as mentioned in Regulation 1013/2006 (OJ L 190)" +
        " – Article 18 and Annex VII.”</p>\n                <p class=\"govuk-body\">Robin Newman</p>\n  " +
        "              </div>"
    ),
    ConversationMessage(
      "444",
      "           <div>\n  " +
        "              <p class=\"govuk-body\">Dear Customer</p>\n " +
        "               <p class=\"govuk-body\">I found missing information for Customs Declaration MRN " +
        "20GB16046891253600 for a shipment of fireworks.</p>\n            " +
        "    <p class=\"govuk-body\">Please upload this form before your shipment can be cleared for export:</p>\n    " +
        "            <p class=\"govuk-body\">C672 form. “This form must accompany shipments of waste " +
        "as mentioned in Regulation 1013/2006 (OJ L 190) – Article 18 and Annex VII.”</p>\n   " +
        "             <p class=\"govuk-body\">Robin Newman</p>\n                </div>"
    ),
    ConversationMessage(
      "555",
      "           <div>\n     " +
        "           <p class=\"govuk-body\">Dear Customer</p>\n     " +
        "           <p class=\"govuk-body\">I found missing information for Customs Declaration MRN 20GB16046891253600" +
        " for a shipment of fireworks.</p>\n          " +
        "      <p class=\"govuk-body\">Please upload this form before your shipment can be cleared for export:</p>\n   " +
        "             <p class=\"govuk-body\">C672 form. “This form must accompany shipments of waste as mentioned " +
        "in Regulation 1013/2006 (OJ L 190) – Article 18 and Annex VII.”</p>\n       " +
        "         <p class=\"govuk-body\">Robin Newman</p>\n                </div>"
    ),
    ConversationMessage(
      "666",
      "           <div>\n         " +
        "       <p class=\"govuk-body\">Dear Customer</p>\n    " +
        "            <p class=\"govuk-body\">I found missing information for Customs " +
        "Declaration MRN 20GB16046891253600 for a shipment of fireworks.</p>\n     " +
        "           <p class=\"govuk-body\">Please upload this form before your shipment can be cleared " +
        "for export:</p>\n                <p class=\"govuk-body\">C672 form." +
        " “This form must accompany shipments of waste as mentioned in Regulation 1013/2006 (OJ L 190) " +
        "– Article 18 and Annex VII.”</p>\n                <p class=\"govuk-body\">Robin Newman</p>\n     " +
        "           </div>"
    )
  )

}
