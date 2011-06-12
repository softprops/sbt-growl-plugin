package growl

import sbt._
import org.scalatools.testing.{Logger => TLogger, Event => TEvent, Result => TResult}

/** Encapsulates info about a group test result
 *  @param name name of test group
 *  @param status final Result.Value for a test group
 *  @param count total number of tests in a uni
 *  @param failures number of test failures
 *  @param errors number of test errors
 *  @param skipped number of skipped tests
 *  @param messages aggregated tests info messages
 */
case class GroupResult(
 name: String, status: TestResult.Value,
 count: Int, failures: Int, errors: Int, skipped: Int
)

/** Output formatting info for growl notification
 *  @param id unique display display id
 *  @param title title of growl notification
 *  @param message body of growl notification
 *  @param sticky if true growl message will sticky
 *  @param imagePath optional absolute path to image to display
 */
case class GrowlResultFormat(
 id: Option[String], title: String, message: String, sticky: Boolean, imagePath: Option[String]
)

/** Container for growl test image paths */
case class GrowlTestImages(pass: Option[String], fail: Option[String], error: Option[String])

/** GrowlTestReportListener will Growl test results and messages */
class GrowlingTestReportListener(
  resultFormatter: GroupResult => GrowlResultFormat,
  exceptionFormatter:(String, Throwable) => GrowlResultFormat,
  defaultImagePath: String) extends TestReportListener {
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

  private def count(event: TEvent): Unit =
    event.result match {
      case TResult.Error => errors += 1
      case TResult.Success => passed += 1
      case TResult.Failure => failures += 1
      case TResult.Skipped => skipped += 1
    }

  /** called if test completed */
  def endGroup(name: String, result: TestResult.Value) = {
    val total = failures + errors + skipped + passed
    notify(resultFormatter(
      GroupResult(name, result, total, failures, errors, skipped)
    ))
  }

  /** called if there was an error during test */
  def endGroup(name: String, t: Throwable) =
    notify(exceptionFormatter(name, t))

  /** Sends growl notifications */
  protected def notify(f: GrowlResultFormat) = {
    val g = Growl title(f.title) identifier(f.id.getOrElse(f.title)) message(f.message) image(f.imagePath.getOrElse(defaultImagePath))
    if(f.sticky) g.sticky() meow else g.meow
  }
}
