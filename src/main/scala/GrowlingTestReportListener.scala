package growl

import sbt._
import org.scalatools.testing.{Logger => TLogger, Event => TEvent, Result => TResult}

/** GrowlTestReportListener will Growl test results and messages */
class GrowlingTestReportListener(extensions: GrowlingTestExtensions) extends TestReportListener {
  import meow._
  
  private var skipped, errors, passed, failures = 0 
  
  /** called for each class or equivalent grouping */
  def startGroup(name: String) = {
    errors = 0
    passed = 0
    failures = 0
    skipped = 0
  }
  
  /** called for each test method or equivalent */
  def testEvent(event: TestEvent) = event.detail.foreach(count)
  
  private def count(event: TEvent): Unit = {
    event.result match {
      case TResult.Error => errors +=1
      case TResult.Success => passed +=1 
      case TResult.Failure => failures +=1 
      case TResult.Skipped => skipped += 1
    }
  }
  
  /** called if test completed */
  def endGroup(name: String, result: Result.Value) = {
    val total = failures + errors + skipped + passed
    notify(extensions.growlResultFormatter(
      GroupResult(name, result, total, failures, errors, skipped)
    ))
  }
  
  /** called if there was an error during test */
  def endGroup(name: String, t: Throwable) = {
    notify(extensions.growlExceptionFormatter(name, t))
  }
  
  /** Sends growl notifications */
  protected def notify(f: GrowlResultFormat) = {
    val g = Growl title(f.title) identifier(f.id.getOrElse(f.title)) message(f.message) image(f.imagePath.getOrElse(extensions.defaultGrowlImagePath))
    if(f.sticky) g.sticky() meow else g.meow
  } 
}