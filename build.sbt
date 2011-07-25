sbtPlugin := true

name := "sbt-growl-plugin"

organization := "me.lessis"

posterousNotesVersion := "0.1.1"

version <<= (posterousNotesVersion,sbtVersion)("%s-%s-SNAPSHOT" format(_,_))

resolvers += "less is" at "http://repo.lessis.me"

libraryDependencies ++= Seq(
   "me.lessis" %% "meow" % "0.1.1-SNAPSHOT"
)

publishTo :=  Some(Resolver.file("lessis repo", new java.io.File("/var/www/repo")))
