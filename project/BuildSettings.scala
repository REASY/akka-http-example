import com.typesafe.sbt.packager.MappingsHelper.directory
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging.autoImport.scriptClasspath
import com.typesafe.sbt.packager.archetypes._
import com.typesafe.sbt.packager.docker.DockerPlugin
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport.Universal
import sbt.Keys._
import sbt._
import scoverage.ScoverageKeys.{coverageFailOnMinimum, coverageMinimum}

object BuildSettings {
  def versionSettings: Seq[Def.Setting[_]] = Seq(
    javaOptions ++= Seq(
      s"-Dversion=${version.value}",
      s"-DappName=${name.value}"
    )
  )

  def codeCoverageSettings: Seq[Setting[_]] = Seq(
    coverageMinimum := 75,
    coverageFailOnMinimum := false,
  )

  def commonSettings: Seq[Setting[_]] = Seq(
    scalaVersion := "2.12.4",
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding", "UTF-8",
      "-feature",
      "-language:existentials",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-unchecked",
      "-Xlint",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard",
      "-Xfuture",
      "-Ywarn-unused-import"
//      "-Xfatal-warnings"
    ),
    resolvers ++= Seq(
      "Ficus Bintray" at "https://dl.bintray.com/iheartradio/maven/",
      "Artima Maven Repository" at "http://repo.artima.com/releases"
    )
  )

  object SbtLibraryProject {
    def apply(name: String): Project = Project(name, file(name))
      .settings(commonSettings: _*)
      .configs(IntegrationTest)
      .settings(Defaults.itSettings: _*)
      .settings(
        parallelExecution in IntegrationTest := false,
        fork in IntegrationTest := true,
        parallelExecution in Test := false,
        fork in Test := true
      )
  }

  object SbtAppProject {
    def apply(name: String): Project = SbtLibraryProject(name)
      .enablePlugins(JavaAppPackaging)
      .enablePlugins(DockerPlugin)
      .settings(versionSettings: _*)
      .settings(inConfig(Universal)(versionSettings): _*)
      .settings(
        scriptClasspath := "../conf/" +: scriptClasspath.value,
        mappings in Universal ++= directory(baseDirectory.value / "conf"),
        unmanagedResourceDirectories in Compile += baseDirectory.value / "conf",
        mappings in(Compile, packageBin) := {
          val packageBinMappings = (mappings in(Compile, packageBin)).value
          val conf = baseDirectory.value / "conf"
          val copied = (copyResources in(Compile, packageBin)).value
          val toExclude = copied.collect {
            case (source, dest) if source.getParentFile == conf => dest
          }.toSet
          packageBinMappings.filterNot {
            case (file, _) => toExclude(file)
          }
        }
      )
  }
}