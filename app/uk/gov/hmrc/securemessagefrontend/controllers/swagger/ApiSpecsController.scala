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

package uk.gov.hmrc.securemessagefrontend.controllers.swagger

import com.iheart.playSwagger.PrefixDomainModelQualifier
import javax.inject.{ Inject, Singleton }
import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

@Singleton
class ApiSpecsController @Inject()(
  mcc: MessagesControllerComponents
) extends FrontendController(mcc) {

  implicit val cl: ClassLoader = getClass.getClassLoader
  val domainPackage = "uk.gov.hmrc.securemessagefrontend"
  lazy val generator =
    com.iheart.playSwagger.SwaggerSpecGenerator(
      swaggerV3 = true,
      modelQualifier = PrefixDomainModelQualifier(domainPackage),
      apiVersion = Some(uk.gov.hmrc.securemessagefrontend.BuildInfo.version))

  lazy val swagger = Action { _ =>
    generator
      .generate("prod.routes")
      .fold(e => InternalServerError(s"Couldn't generate swagger. ${e.getMessage()}"), s => Ok(Json.prettyPrint(s)))
  }

  def specs: Action[AnyContent] = swagger
}
