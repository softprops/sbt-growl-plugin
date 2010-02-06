import sbt._

/** Mixin for Growl notifications */
trait GrowlPlugin extends BasicScalaProject {
  override def testListeners: Seq[TestReportListener] = 
    Seq(new GrowlTestReportListener()) ++ super.testListeners
}