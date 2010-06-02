import sbt._

class GrowlPluginProject(info: ProjectInfo) extends PluginProject(info) with posterous.Publish {
  val stSnapshots = "Scala Tools Snapshots" at "http://www.scala-tools.org/repo-snapshots/"
  val specs = "org.scala-tools.testing" % "specs" % "1.6.2.1" % "test"
  
  val lessRepo = "lessis repo" at "http://repo.lessis.me"
  val meow = "me.lessis" %% "meow" % "0.1.0-SNAPSHOT"
  
  override def managedStyle = ManagedStyle.Maven
  lazy val publishTo = Resolver.file("publish repo", new java.io.File("/var/www/repo"))
}