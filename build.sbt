import BuildInfo.buildInfoSettings
import Dependencies._
import TestConfiguration._

lazy val root = (project in file("."))
  .settings(
    organization := "com.eyeem",
    name := "groceries",
    organizationName := "eyeem"
  )
  .enablePlugins(BuildInfoPlugin)
  .configs(CustomIntegrationTest)
  .settings(
    scalaVersion := Dependencies.ScalaVersion,
    fork := true,
    javaOptions in Test ++= testConfigCommandLineOptions,
    testOptions in Test := unitTestOptions,
    testOptions in CustomIntegrationTest := integrationTestOptions,
    libraryDependencies ++=
      dependencies ++
        testDependencies
  ).settings(buildInfoSettings)

