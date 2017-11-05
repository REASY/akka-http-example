import sbt._

object Dependencies {
  object Versions {
    val akkaHttp = "10.0.10"
    val scalaTest = "3.0.4"
    val slick = "3.2.1"
    val scalaLogging = "3.7.2"
    val config = "1.3.1"
    val logback = "1.2.3"
    val mockitoCore = "2.8.47"
    val akkaHttpPlayJson = "1.19.0-M2"
    val playJson = "2.6.6"
    val playWs = "1.1.3"
    val h2Database = "1.4.196"
    val jtds = "1.3.1"
  }

  val commonsDependencies = Seq(
    "com.typesafe.akka" %% "akka-http" % Versions.akkaHttp,
    "com.typesafe.akka" %% "akka-http-core" % Versions.akkaHttp,
    "com.typesafe.akka" %% "akka-http-testkit" % Versions.akkaHttp % "test,it",
    "com.typesafe.slick" %% "slick" % Versions.slick,
    "com.typesafe.slick" %% "slick-hikaricp" % Versions.slick,
    "com.typesafe" % "config" % Versions.config,
    "com.typesafe.scala-logging" %% "scala-logging" % Versions.scalaLogging,
    "ch.qos.logback" % "logback-classic" % Versions.logback,
    "org.scalatest" %% "scalatest" % Versions.scalaTest % "test,it",
    "de.heikoseeberger" %% "akka-http-play-json" % Versions.akkaHttpPlayJson,
    "org.mockito" % "mockito-core" % Versions.mockitoCore % "test,it",
    "com.typesafe.play" %% "play-json" % Versions.playJson,
    "com.typesafe.play" %% "play-ahc-ws-standalone" % Versions.playWs,
    "com.h2database" % "h2" % Versions.h2Database % "test,it",
    "net.sourceforge.jtds" % "jtds" % Versions.jtds,
  )
}
