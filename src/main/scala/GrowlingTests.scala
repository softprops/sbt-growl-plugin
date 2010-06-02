package growl

import sbt._

/** Mixin for Growl notifications */
trait GrowlingTests extends BasicScalaProject with GrowlingTestExtensions {
  override def testListeners: Seq[TestReportListener] = 
    Seq(new GrowlingTestReportListener(this)) ++ super.testListeners
}