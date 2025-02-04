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

resolvers += MavenRepository("HMRC-open-artefacts-maven2", "https://open.artefacts.tax.service.gov.uk/maven2")
resolvers += Resolver.url("HMRC-open-artefacts-ivy", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(
  Resolver.ivyStylePatterns)

addSbtPlugin("uk.gov.hmrc"       % "sbt-auto-build"        % "3.0.0")
addSbtPlugin("uk.gov.hmrc"       % "sbt-distributables"    % "2.1.0")
addSbtPlugin("com.typesafe.play" % "sbt-plugin"            % "2.8.7")
addSbtPlugin("org.scalastyle"    % "scalastyle-sbt-plugin" % "1.0.0")
addSbtPlugin("com.lucidchart"    % "sbt-scalafmt"          % "1.16")
addSbtPlugin("org.scoverage"     % "sbt-scoverage"         % "1.9.2")
addSbtPlugin("org.irundaia.sbt"  % "sbt-sassify"           % "1.4.11")
addSbtPlugin("uk.gov.hmrc"       % "sbt-settings"          % "4.5.0")
addSbtPlugin("com.timushev.sbt"  % "sbt-updates"           % "0.5.1")
addSbtPlugin("uk.gov.hmrc"       % "sbt-service-manager"   % "0.8.0")
addSbtPlugin("uk.gov.hmrc"       % "sbt-bobby"             % "3.2.0")
