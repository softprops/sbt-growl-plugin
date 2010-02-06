import sbt._

class GrowlPluginProject(info: ProjectInfo) extends PluginProject(info) {
	override def crossScalaVersions = Set("2.7.6","2.7.6", "2.7.7", "2.8.0.Beta1-RC1") 
  
  val stSnapshots = "Scala Tools Snapshots" at "http://www.scala-tools.org/repo-snapshots/"
  val specs = "org.scala-tools.testing" % "specs" % "1.6.2-SNAPSHOT" % "test"
  
	override def managedStyle = ManagedStyle.Maven
  val localRepo = "Local Maven Repository" at "file://" + Path.userHome + "/.m2/repository"
  val publishTo = localRepo
  
  val meow = "me.lessis" %% "meow" % "0.0.1"
}