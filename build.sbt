name := "growme-weatherchecker"

version := "0.1"

scalaVersion := "2.13.1"


libraryDependencies += "com.lightbend.akka" %% "akka-stream-alpakka-mqtt" % "1.1.2"
libraryDependencies += "com.typesafe.akka" %% "akka-http"   % "10.1.11"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.26"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.11"
