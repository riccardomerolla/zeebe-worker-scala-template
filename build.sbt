
ThisBuild / organization := "org.camunda.community"
ThisBuild / version := "1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.1.1"
lazy val root = (project in file("."))
  .settings(
    name := "zeebe-worker-scala-template",
    libraryDependencies ++= List(
      "io.camunda" % "zeebe-client-java" % props.zeebeVersion,
      "io.zeebe" % "zeebe-worker-java-testutils" % props.zeebeVersion % Test,
      "org.junit.jupiter" % "junit-jupiter-engine" % props.junitJupiterVersion % Test,
      "org.junit.jupiter" % "junit-jupiter-api" % props.junitJupiterVersion % Test
    )
  )

lazy val props = new {
  val zeebeVersion = "8.0.2"
  val junitJupiterVersion = "5.8.1"
}

