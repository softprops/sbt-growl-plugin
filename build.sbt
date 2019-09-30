sbtPlugin := true

name := "sbt-growl-plugin"

organization := "me.lessis"

version <<= sbtVersion(v =>
  if (v.startsWith("0.11") || v.startsWith("0.12") || v.startsWith("0.13")) "0.1.4-SNAPSHOT"
  else error("unsupported sbt version %s" format v)
)

scalacOptions ++= Seq("-feature", "-deprecation")

sbtVersion in Global := "0.13.6"

scalaVersion in Global := "2.10.4"

resolvers += Opts.resolver.sonatypeReleases

libraryDependencies += "me.lessis" %% "meow" % "0.1.1"

publishTo := Some(Classpaths.sbtPluginReleases)

publishMavenStyle := false

publishArtifact in Test := false

licenses <<= version(v => Seq("MIT" -> url(
  "https://github.com/softprops/sbt-growl-plugin/blob/%s/LICENSE" format v)))

pomExtra := (
  <scm>
    <url>git@github.com:softprops/sbt-growl-plugin.git</url>
    <connection>scm:git:git@github.com:softprops/sbt-growl-plugin.git</connection>
  </scm>
  <developers>
    <developer>
      <id>softprops</id>
      <name>Doug Tangren</name>
      <url>https://github.com/softprops</url>
    </developer>
  </developers>
)
