sbtPlugin := true

name := "sbt-growl-plugin"

organization := "me.lessis"

version := "0.1.0"

resolvers += "less is" at "http://repo.lessis.me"

libraryDependencies ++= Seq(
   "me.lessis" %% "meow" % "0.1.0"
)

publishTo :=  Some(Resolver.file("lessis repo", new java.io.File("/var/www/repo")))