import BuildSettings._
import Dependencies._


lazy val server = SbtLibraryProject("server")
  .settings(
    libraryDependencies ++= commonsDependencies
  )
  .configs(IntegrationTest extend Test)
  .settings(inConfig(IntegrationTest extend Test)(Defaults.testSettings) : _*)

lazy val root = Project("dummy-googler", file("."))
  .settings(commonSettings: _*)
  .settings(codeCoverageSettings: _*)
  .dependsOn(server)
  .aggregate(server)