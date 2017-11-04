import sbt._

object Dependencies {
  object Versions {
    val akkaHttp = "10.0.10"
    val scalaTest = "3.0.4"
    val slick = "3.2.1"
    val scalaLogging = "3.5.0"
    val config = "1.3.1"
    val logback = "1.2.3"
    val mockitoCore = "2.8.47"
  }

  val commonsDependencies = Seq(
    "com.typesafe.akka" %% "akka-http" % Versions.akkaHttp,
    "com.typesafe.akka" %% "akka-http-core" % Versions.akkaHttp,
    "com.typesafe.akka" %% "akka-http-testkit" % Versions.akkaHttp % "test,it",
    "com.typesafe.slick" %% "slick" % Versions.slick,
    "com.typesafe.slick" %% "slick-hikaricp" % Versions.slick,
    "ch.qos.logback" % "logback-classic" % Versions.logback,
    "com.typesafe.scala-logging" %% "scala-logging" % Versions.scalaLogging
      exclude("com.typesafe.scala-logging", "scala-logging-api_2.11")
      exclude("com.typesafe.scala-logging", "scala-logging-slf4j_2.11"),
    "com.typesafe" % "config" % Versions.config,
    "org.scalatest" %% "scalatest" % Versions.scalaTest % "test,it",
    "org.mockito" % "mockito-core" % Versions.mockitoCore % "test,it",
  )
}