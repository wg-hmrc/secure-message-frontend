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

package models

import org.scalatest.matchers.must.Matchers
import org.scalatest.EitherValues
import org.scalatestplus.play.PlaySpec
import play.api.mvc.PathBindable

class LanguageSpec extends PlaySpec with Matchers with EitherValues {

  "Language" should {
    val pathBindable = implicitly[PathBindable[Language]]

    "bind Cymraeg from a URL" in {
      val result = pathBindable.bind("language", Language.Cymraeg.toString)
      result.right.value mustEqual Language.Cymraeg
    }

    "bind English from a URL" in {
      val result = pathBindable.bind("language", Language.English.toString)
      result.right.value mustEqual Language.English
    }

    "unbind Cymraeg" in {
      val result = pathBindable.unbind("language", Language.Cymraeg)
      result mustEqual Language.Cymraeg.toString
    }

    "unbind English" in {
      val result = pathBindable.unbind("language", Language.English)
      result mustEqual Language.English.toString
    }

    "return invalid language" in {
      val result = pathBindable.bind("langfuagfdsfse", "french")
      result mustEqual Left("Invalid language")
    }
  }
}
