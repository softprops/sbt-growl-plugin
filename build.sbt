sbtPlugin := true

name := "sbt-growl-plugin"

organization := "me.lessis"

version <<= sbtVersion("0.1.1-%s-SNAPSHOT" format _)

resolvers += "less is" at "http://repo.lessis.me"

libraryDependencies ++= Seq(
   "me.lessis" %% "meow" % "0.1.0"
)

publishTo :=  Some(Resolver.file("lessis repo", new java.io.File("/var/www/repo")))
