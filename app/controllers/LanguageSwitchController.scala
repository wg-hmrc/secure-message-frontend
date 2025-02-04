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

package controllers

import com.google.inject.Inject
import javax.inject.Singleton
import play.api.i18n.Lang
import play.api.mvc._
import config.AppConfig
import models.Language
import uk.gov.hmrc.play.language.{ LanguageController, LanguageUtils }

@Singleton
class LanguageSwitchController @Inject()(appConfig: AppConfig, languageUtils: LanguageUtils, cc: ControllerComponents)
    extends LanguageController(languageUtils, cc) {
  import appConfig._

  override def fallbackURL: String =
    "https://www.gov.uk/government/organisations/hm-revenue-customs"

  def selectLanguage(language: Language): Action[AnyContent] = super.switchToLanguage(language.lang.code)

  override protected def languageMap: Map[String, Lang] =
    if (appConfig.languageTranslationEnabled) {
      Map(en -> Lang(en), cy -> Lang(cy))
    } else {
      Map(en -> Lang(en))
    }
}
