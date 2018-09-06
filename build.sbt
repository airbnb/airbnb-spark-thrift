// Meta Information
organization := "com.airbnb"
name := "airbnb-spark-thrift"
licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))
// TODO replace mslinn with your github id
homepage := Some(url("https://github.com/example/project"))
scmInfo := Some(
  ScmInfo(
    url(s"https://github.com/nwparker/$name"),
    s"git@github.com:mslinn/$name.git"
  )
)
version := "1.0.1-SNAPSHOT"

// Library Versions
val scalaVer = "2.11.8"
scalaVersion := scalaVer
val javaVersion = "1.8"
val sparkVersion = "2.0.0"
crossScalaVersions := Seq("2.10.7", "2.11.12")

libraryDependencies ++= Seq(
  "org.apache.thrift" % "libthrift" % "0.9.3",
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.scalatest" %% "scalatest" % "3.0.3" % Test,
  "org.scalacheck" %% "scalacheck" % "1.13.5" % Test
)

// Misc Settings
publishMavenStyle := true
cancelable := true
javacOptions ++= Seq(
  "-Xlint:deprecation",
  "-Xlint:unchecked",
  "-source", javaVersion,
  "-target", javaVersion,
  "-g:vars"
)
logLevel := Level.Warn
// Only show warnings and errors on the screen for compilations.
// This applies to both test:compile and compile and is Info by default
logLevel in compile := Level.Warn
// Level.INFO is needed to see detailed output when running tests
logLevel in test := Level.Info

scalacOptions ++= Seq( // From https://tpolecat.github.io/2017/04/25/scalac-flags.html
  "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
  "-encoding", "utf-8",                // Specify character encoding used by source files.
  "-explaintypes",                     // Explain type errors in more detail.
  "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials",            // Existential types (besides wildcard types) can be written and inferred
  "-language:experimental.macros",     // Allow macro definition (besides implementation and application)
  "-language:higherKinds",             // Allow higher-kinded types
  "-language:implicitConversions",     // Allow definition of implicit functions called views
  "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit",                       // Wrap field accessors to throw an exception on uninitialized access.
  "-Xfuture",                          // Turn on future language features.
  "-Yno-adapted-args",                 // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
  "-Ywarn-dead-code",                  // Warn when dead code is identified.
  "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
  "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
  "-Ywarn-numeric-widen",              // Warn when numerics are widened.
  "-Ywarn-value-discard"               // Warn when non-Unit expression results are unused.
)

// The REPL can’t cope with -Ywarn-unused:imports or -Xfatal-warnings so turn them off for the console
scalacOptions in (Compile, console) --= Seq("-Ywarn-unused:imports", "-Xfatal-warnings")

scalacOptions in (Compile, doc) ++= baseDirectory.map {
  (bd: File) => Seq[String](
     "-sourcepath", bd.getAbsolutePath, // todo replace my-new-project with the github project name, and replace mslinn with your github id
     "-doc-source-url", "https://github.com/mslinn/my-new-project/tree/master€{FILE_PATH}.scala"
  )
}.value

// Generate thrift classes and add compiled classes to project
Test / sourceGenerators += Def.task {
  import java.nio.file.{Files, Paths}
  import scala.collection.JavaConverters._
  import sys.process._

  Seq("thrift", "-o", sourceDirectory.in(Test).value.getAbsolutePath, "--gen", "java",
    s"${resourceDirectory.in(Test).value.getAbsoluteFile}/thrift/dummy.thrift")!

  Files.find(Paths.get(sourceDirectory.in(Test).value.getAbsolutePath, "gen-java"), 999, (_, bfa) => bfa.isRegularFile)
    .iterator().asScala.toList
    .map(_.toFile)
}

