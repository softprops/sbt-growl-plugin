sbtPlugin := true

name := "sbt-growl-plugin"

organization := "me.lessis"

version <<= sbtVersion(v =>
  if(v.startsWith("0.11")) "0.2.0-SNAPSHOT"
  else if (v.startsWith("0.10")) "0.2.0-%s-SNAPSHOT".format(v)
  else error("unsupported sbt version %s" format v)
)

resolvers += "less is" at "http://repo.lessis.me"

libraryDependencies += "me.lessis" %% "meow" % "0.1.1"

publishTo :=  Some(Resolver.file("lessis repo", new java.io.File("/var/www/repo")))
