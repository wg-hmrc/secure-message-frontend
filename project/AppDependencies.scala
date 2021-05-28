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

import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"             %% "auth-client"                % "4.0.0-play-27",
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-27" % "4.0.0",
    "uk.gov.hmrc"             %% "play-frontend-hmrc"         % "0.41.0-play-27",
    "uk.gov.hmrc"             %% "play-frontend-govuk"        % "0.60.0-play-27",
    "uk.gov.hmrc"             %% "play-language"              % "4.10.0-play-27",
    "uk.gov.hmrc"             %% "url-builder"                % "3.4.0-play-27",
    "com.typesafe.play"       %% "play-json-joda"             % "2.9.1",
    "com.iheart"              %% "play-swagger"               % "0.10.5",
    "org.typelevel"           %% "cats-core"                  % "2.6.1",
    "org.scala-lang.modules"  %% "scala-xml"                  % "2.0.0",
    "com.beachape"            %% "enumeratum-play"            % "1.5.17",
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-27"    % "4.0.0"           % Test,
    "org.scalatest"           %% "scalatest"                 % "3.0.0"           % Test,
    "org.jsoup"               % "jsoup"                      % "1.13.1"          % Test,
    "com.typesafe.play"       %% "play-test"                 % current           % Test,
    "uk.gov.hmrc"             %% "service-integration-test"  % "0.13.0-play-27"  % "test, it",
    "org.scalatestplus.play"  %% "scalatestplus-play"        % "4.0.3"           % "test, it",
    "org.jsoup"               % "jsoup"                      % "1.13.1"          % "test, it",
    "org.mockito"             % "mockito-core"               % "3.10.0"           % "test, it",
    "com.vladsch.flexmark"    % "flexmark-all"               % "0.36.8"          % "test, it",
    "org.pegdown"             % "pegdown"                    % "1.6.0"           % "test, it",
    "net.codingwell"          %% "scala-guice"               % "5.0.1"           % "test, it"
  )

  val dependencyOverrides = Seq(
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.8" // swagger requires an older version of jackson than alpakka...
  )
}
