import sbt._
import Keys._
import sbtassembly.AssemblyKeys
import AssemblyKeys._

object BobBotBuild extends Build {

  object V {

    val slime = "0.1.3-SNAPSHOT"
    
  }

  val projectName         = "bobbot"
  val projectVersion      = "1.0.0-SNAPSHOT"

  val projectDependencies = Seq(
    "com.cyberdolphins" %% "slime" % V.slime withSources()
      exclude("org.slf4j", "jcl-over-slf4j")
      exclude("commons-logging", "commons-logging"),
    "org.scalatest" %% "scalatest" % "2.2.4" % "test"
  )

  val projectSettings: Seq[Setting[_]] = Seq(
    version := projectVersion,
    organization := "com.cyberdolphins",
    libraryDependencies ++= projectDependencies,
    scalaVersion        := "2.11.4",
    resolvers           ++= Seq(Resolver.mavenLocal, "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"),
    scalacOptions       := Seq("-unchecked", "-deprecation", "-feature", "-language:implicitConversions", "-language:postfixOps"),
    fork in test := true,
    javaOptions in test += "-XX:MaxPermSize=512M -Xmx1024M -Xms1024M -Duser.timezone=UTC -Djava.library.path=/usr/local/lib",
    resourceDirectory in Test <<= baseDirectory apply {(baseDir: File) => baseDir / "test" / "resources"},
    mainClass in (Compile, run) := Some("com.cyberdolphins.bobbot.Run"),
    mainClass in assembly := Some("com.cyberdolphins.bobbot.Run")
  )

  val main = Project(projectName, file("."))
    .settings(projectSettings: _*)
}
