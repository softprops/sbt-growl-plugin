sbtPlugin := true

name := "sbt-growl-plugin"

organization := "me.lessis"

version <<= sbtVersion(v =>
  if (v.startsWith("0.11") || v.startsWith("0.12")) "0.1.3"
  else error("unsupported sbt version %s" format v)
)

resolvers += Opts.resolver.sonatypeReleases

libraryDependencies += "me.lessis" %% "meow" % "0.1.0"

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
