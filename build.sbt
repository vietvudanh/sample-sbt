name := "ArangoQuery"

version := "0.1"
scalaVersion := "2.13.1"

// copy libs to target
retrieveManaged := true

lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  organization := "vn.vietvu.arango.App",
  scalaVersion := "2.13.1",
  test in assembly := {}
)

libraryDependencies ++= Seq(
  "com.arangodb" % "arangodb-java-driver" % "5.0.0"
)

lazy val app = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    mainClass in assembly := Some("vn.vietvu.arango.ImportGraphData")
    // more settings here ...
  )
