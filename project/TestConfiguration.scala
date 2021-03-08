import sbt._

object TestConfiguration {

  lazy val CustomIntegrationTest = config("it") extend Test
  lazy val unitTestOptions = Seq(Tests.Filter(unitFilter), Tests.Argument("-oF"), Tests.Argument("-oD"))
  lazy val integrationTestOptions = Seq(Tests.Filter(integrationFilter))
  lazy val testConfigCommandLineOptions: Seq[String] =
    Seq(
      "-Dhollandsehoogte.apikey=123",
      "-Dadobe.awsAccessKeyId=access_key_123",
      "-Dadobe.awsSecretAccessKey=secret_access_key_123",
      "-Dadobe.awsRegion=eu-west-1",
      "-Dadobe.awsS3Endpoint=http://localhost:4572/",
      "-DadobeFeedbackImporter.baseUrl=https://feedbackIsGreat.eyeem",
      "-Dadobe.bucket=myBucketName",
      "-Dadobe.eyeemCorePath=path/to/core",
      "-Dadobe.eyeemPremiumPath=path/to/premium"
    )

  private def unitFilter(name: String): Boolean = {
    (name endsWith "Spec") && !integrationFilter(name)
  }

  private def integrationFilter(name: String): Boolean = name endsWith "ITSpec"
}
