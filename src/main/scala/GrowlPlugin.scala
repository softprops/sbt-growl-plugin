import sbt._

/** Mixin for Growl notifications */
trait GrowlPlugin extends BasicScalaProject with GrowlPluginExtensions {
  override def testListeners: Seq[TestReportListener] = 
    Seq(new GrowlTestReportListener(this)) ++ super.testListeners
}