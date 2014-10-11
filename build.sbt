name := """twitviz"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs
)

libraryDependencies += "com.twitter" % "hbc-core" % "2.2.0"

libraryDependencies += "com.twitter" % "hbc" % "2.2.0"