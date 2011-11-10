sbtPlugin := true

name := "sbt-growl-plugin"

organization := "me.lessis"

version <<= sbtVersion(v =>
  if(v.startsWith("0.11")) "0.1.3"
  else if (v.startsWith("0.10")) "0.1.3-%s".format(v)
  else error("unsupported sbt version %s" format v)
)

resolvers += "less is" at "http://repo.lessis.me"

libraryDependencies += "me.lessis" %% "meow" % "0.1.1"

publishTo :=  Some(Resolver.file("lessis repo", new java.io.File("/var/www/repo")))
