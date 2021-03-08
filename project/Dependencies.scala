import sbt._

object Dependencies {

  lazy final val ScalaVersion = "2.13.3"

  lazy val akkaVersion = "2.6.5"
  lazy val alpakkaVersion = "2.0.2"
  lazy val akkaHttpVersion = "10.1.12"
  lazy val scalaTestVersion = "3.2.0"
  lazy val scalaLoggingVersion = "3.9.2"
  lazy val playJsonVersion = "2.9.0"
  lazy val playWsVersion = "2.1.2"
  lazy val enumeratumVersion = "1.6.0"
  lazy val monocleVersion = "2.0.0" //For testing
  lazy val prometheusVersion = "0.9.0"
  lazy val catsVersion = "2.0.0"
  lazy val flywayVersion = "6.5.0"
  lazy val awsSdkVersion = "2.14.5"
  lazy val skuberVersion = "2.6.0"

  lazy val dependencies = Seq(
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.beachape" %% "enumeratum-play-json" % enumeratumVersion,
    "com.typesafe.play" %% "play-ahc-ws-standalone" % playWsVersion,
    "com.typesafe.play" %% "play-ws-standalone-json" % playWsVersion,
    "com.typesafe" % "config" % "1.4.0",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    "com.softwaremill.macwire" %% "macros" % "2.3.6" % "provided"
  )


  lazy val testDependencies = Seq(
    "com.github.julien-truffaut" %% "monocle-core" % monocleVersion,
    "com.github.julien-truffaut" %% "monocle-macro" % monocleVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
    "org.scalatest" %% "scalatest" % scalaTestVersion,
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion,
    "org.mockito" %% "mockito-scala" % "1.13.6"
  ).map(_ % Test)
}
