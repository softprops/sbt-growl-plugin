package growl

import sbt._
import org.scalatools.testing.{Logger => TLogger, Event => TEvent, Result => TResult}

/** Encapsulates info about a group's test results
 *  @param name name of test group
 *  @param status final Result.Value for a test group
 */
case class GroupResult(
 name: String, status: TestResult.Value
)

/** Encapsulates info about all test results
 *  @param status the final result of the tests
 *  @param count total number of tests in a uni
 *  @param failures number of test failures
 *  @param errors number of test errors
 *  @param passed number of tests passed
 *  @param skipped number of skipped tests
 */
case class AggregateResult(
  status: TestResult.Value, count: Int, failures: Int, errors: Int, passed: Int, skipped: Int
)

/** Output formatting info for growl notification
 *  @param id optional unique display id
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

/** GrowlTestReportListener will Growl test result notification */
class GrowlingTestsListener(
  groupFormatter: GroupResult => GrowlResultFormat,
  exceptionFormatter:(String, Throwable) => GrowlResultFormat,
  aggregateFormatter: AggregateResult => GrowlResultFormat,
  defaultImagePath: String, log: sbt.Logger) extends TestsListener {
  import meow._

  private var skipped, errors, passed, failures = 0

  def doInit {
    skipped = 0
    errors = 0
    passed = 0
    failures = 0
  }

  /** called for each class or equivalent grouping */
  def startGroup(name: String) = { }

  /** called for each test method or equivalent */
  def testEvent(event: TestEvent) = event.detail.foreach(count)

  private def count(event: TEvent): Unit =
    event.result match {
      case TResult.Error => errors += 1
      case TResult.Success => passed += 1
      case TResult.Failure => failures += 1
      case TResult.Skipped => skipped += 1
    }

  /** called when test group is completed */
  def endGroup(name: String, result: TestResult.Value) =
    notify(groupFormatter(GroupResult(name, result)))

  /** called when all tests are complete */
  def doComplete(status: TestResult.Value) = {
		val all = failures + errors + skipped + passed
    notify(aggregateFormatter(AggregateResult(status, all, failures, errors, passed, skipped)))
	}

  /** called if there was an error during test */
  def endGroup(name: String, t: Throwable) =
    notify(exceptionFormatter(name, t))

  /** Sends growl notifications */
  protected def notify(f: GrowlResultFormat) = {
    val img = f.imagePath.getOrElse(defaultImagePath)
    val base = Growl title(f.title) identifier(f.id.getOrElse(f.title)) message(f.message)
    val rich = if(img.isEmpty) base else base.image(img)
    (if(f.sticky) rich.sticky() else rich).meow
  }
}
