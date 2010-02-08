import sbt._

class GrowlPluginProject(info: ProjectInfo) extends PluginProject(info) {
	override def crossScalaVersions = Set("2.7.3", "2.7.4", "2.7.5", "2.7.6", "2.7.7") 
  val stSnapshots = "Scala Tools Snapshots" at "http://www.scala-tools.org/repo-snapshots/"
  val specs = "org.scala-tools.testing" % "specs" % "1.6.2-SNAPSHOT" % "test"
  
  val lessRepo = "lessis repo" at "http://repo.lessis.me"
  val meow = "me.lessis" %% "meow" % "0.0.1"
  
  override def managedStyle = ManagedStyle.Maven
  lazy val publishTo = Resolver.file("publish repo", new java.io.File("/var/www/repo"))
}