sbtPlugin := true

name := "sbt-growl-plugin"

organization := "me.lessis"

version <<= sbtVersion(v =>
  if(v.startsWith("0.11")) "0.1.3"
  else error("unsupported sbt version %s" format v)
)

resolvers += "less is" at "http://repo.lessis.me"

libraryDependencies += "me.lessis" %% "meow" % "0.1.1"

publishTo := Some(Resolver.url("sbt-plugin-releases", url(
  "http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"
))(Resolver.ivyStylePatterns))

publishMavenStyle := false

publishArtifact in Test := false

licenses := Seq("MIT" -> url(
  "https://github.com/softprops/sbt-growl-plugin/blob/0.1.9/LICENSE"))

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
