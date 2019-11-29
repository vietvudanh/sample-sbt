name := "sampleApp"

version := "0.1"
scalaVersion := "2.13.1"

// copy libs to target
retrieveManaged := true

lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  organization := "com.example",
  scalaVersion := "2.13.1",
  test in assembly := {}
)

lazy val app = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    mainClass in assembly := Some("com.example.App")
    // more settings here ...
  )
