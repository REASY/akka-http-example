resolvers ++= Seq(
  Classpaths.sbtPluginReleases,
  "Artima Maven Repository" at "http://repo.artima.com/releases"
)

addSbtPlugin("io.spray" %% "sbt-revolver" % "0.9.0")
addSbtPlugin("com.typesafe.sbt" %% "sbt-native-packager" % "1.2.2")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
addSbtPlugin("com.artima.supersafe" % "sbtplugin" % "1.1.2")