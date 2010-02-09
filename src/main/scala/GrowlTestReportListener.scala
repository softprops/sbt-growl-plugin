import sbt._

/** GrowlTestReportListener will Growl test results and messages
 *  (Mostly taken from sbt.WriterReportListener sans the Writer)
 */
class GrowlTestReportListener(extensions: GrowlPluginExtensions) extends TestReportListener {
  
  import meow._
  
  protected case class Summary(
    count: Int, failures: Int, errors: Int, skipped: Int, 
    message: Option[String], cause: Option[Throwable]
  ) extends NotNull
  
  private var groupCount = 0
  private var groupFailures = 0
  private var groupErrors = 0
  private var groupSkipped = 0
  private var groupMessages: Seq[String] = Nil
  
  protected val passedEventHandler: TestEvent => Summary = 
    (event: TestEvent) => event match {
      case SpecificationReportEvent(
        successes, failures, errors, skipped, desc, systems, subSpecs
      ) => Summary(successes, failures, errors, skipped, None, None)
      case IgnoredEvent(
        name, Some(message)
      ) => Summary(1, 0, 0, 1, Some(message), None)
      case IgnoredEvent(name, None) => Summary(1, 0, 0, 1, None, None)
      case _ => Summary(1, 0, 0, 0, None, None)
    }
  
  protected val failedEventHandler: TestEvent => Summary = 
    (event: TestEvent) => event match {
      case FailedEvent(
        name, msg
      ) => Summary(1, 1, 0, 0, Some("! " + name + ": " + msg), None)
      case TypedErrorEvent(
        name, event, Some(msg), cause
      ) => Summary(1, 1, 0, 0, Some(event + " - " + name + ": " + msg), cause)
      case TypedErrorEvent(
        name, event, None, cause
      ) => Summary(1, 1, 0, 0, Some(event + " - " + name), cause)
      case ErrorEvent(msg) => Summary(1, 1, 0, 0, Some(msg), None)
      case SpecificationReportEvent(
        successes, failures, errors, skipped, desc, systems, subSpecs
      ) => Summary(successes + failures + errors + skipped, failures, errors, skipped, Some(desc), None)
      case _ => Summary(1, 1, 0, 0, None, None)
    }
  
  protected val errorEventHandler: TestEvent => Summary = 
    (event: TestEvent) => event match {
      case FailedEvent(
        name, msg
      ) => Summary(1, 0, 1, 0, Some("! " + name + ": " + msg), None)
      case TypedErrorEvent(
        name, event, Some(msg), cause
      ) => Summary(1, 0, 1, 0, Some(event + " - " + name + ": " + msg), cause)
      case TypedErrorEvent(
        name, event, None, cause
      ) => Summary(1, 0, 1, 0, Some(event + " - " + name), cause)
      case ErrorEvent(msg) => Summary(1, 0, 1, 0, Some(msg), None)
      case SpecificationReportEvent(
        successes, failures, errors, skipped, desc, systems, subSpecs
      ) => Summary(successes + failures + errors + skipped, failures, errors, skipped, Some(desc), None)
      case _ => Summary(1, 0, 1, 0, None, None)
    }
          
  /** called for each class or equivalent grouping */
  def startGroup(name: String) = {
    groupCount = 0
    groupFailures = 0
    groupErrors = 0
    groupSkipped = 0
    groupMessages = Nil
  }
  
  /** called for each test method or equivalent */
  def testEvent(event: TestEvent) = {
    event.result match {
      case Some(result) => {
        val Summary(count, failures, errors, skipped, msg, cause) = 
          result match {
            case Result.Passed => passedEventHandler(event)
            case Result.Failed => failedEventHandler(event)
            case Result.Error => errorEventHandler(event)
          }
        groupCount += count
        groupFailures += failures
        groupErrors += errors
        groupSkipped += skipped
        groupMessages ++= msg.toList
      }
      case None => {}
    }
  }
  
  /** called if test completed */
  def endGroup(name: String, result: Result.Value) = {
    notify(extensions.growlResultFormatter(
      GroupResult(name, result, groupCount, groupFailures, groupErrors, groupSkipped, groupMessages)
    ))
    groupMessages = Nil
  }
  
  /** called if there was an error during test */
  def endGroup(name: String, t: Throwable) = {
    groupMessages = Nil
    notify(extensions.growlExceptionFormatter(name, t))
  }
  
  /** Sends growl notifications */
  protected def notify(f: GrowlResultFormat) = {
    val g = Growl title(f.title) identifier(f.id.getOrElse(f.title)) message(f.message) image(f.imagePath.getOrElse(extensions.defaultGrowlImagePath))
    if(f.sticky) g.sticky() meow else g.meow
  } 
}