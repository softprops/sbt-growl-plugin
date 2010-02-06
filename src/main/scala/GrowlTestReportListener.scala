import sbt._

/** GrowlTestReportListener will Growl test results and messages
 *  (Mostly taken from sbt.WriterReportListener sans the Writer)
 */
class GrowlTestReportListener extends TestReportListener {
  
  import meow._
  
  /** Encapsulates info about a group test result 
   *  @param name name of test group
   *  @param status final Result.Value for a test group
   *  @param count total number of tests in a uni
   *  @param failures number of test failures
   *  @param errors number of test errors
   *  @param skipped number of skipped tests
   *  @params messages aggregated tests info messages
   */
  case class GroupResult(
    name: String, status: Result.Value, 
    count: Int, failures: Int, errors: Int, skipped: Int,
    messages: Seq[String]
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
    notify(growlResultFormatter(
      GroupResult(name, result, groupCount, groupFailures, groupErrors, groupSkipped, groupMessages)
    ))
    groupMessages = Nil
  }
  
  /** called if there was an error during test */
  def endGroup(name: String, t: Throwable) = {
    groupMessages = Nil
    notify(growlExceptionFormatter(name, t))
  }
  
  /** Default implementation returns results in the format
   *   Exception in Test name, execption msg.
   *  Override this fn for custom exception formatting.
   *  @return a function that will format a test group name and Exception
   */
  val growlExceptionFormatter: (String, Throwable) => GrowlResultFormat =
   (name:String, t:Throwable) => {
     GrowlResultFormat(
       Some("%s Exception" format name), "Exception in Test: %s" format name, t.getMessage, true, None
     )
   }
  
  /** Default implementation returns results in the format
   *  `Status`ed name
   *   Tests n, Failed x, Errors y, Skipped z
   *   [stack of info]
   *  Override this fn for custom result formatting.
   *  @return a function that will format a GroupResult
   */
  val growlResultFormatter: GroupResult => GrowlResultFormat = 
    (res: GroupResult) =>
      GrowlResultFormat(
        Some(res.name),
        (res.status match {
          case Result.Error  => "Error %s"
          case Result.Passed => "Passed %s"
          case Result.Failed => "Failed %s"
        }) format res.name, 
        "Tests %s, Failed %s, Errors %s, Skipped %s".format(
          res.count, res.failures, res.errors, res.skipped
        ) + 
        "\n\n" + res.messages.mkString("\n"),  
        res.status match {
          case Result.Error | Result.Failed => true
          case _ => false
        },
        None
      )
      
  /** TODO Default image path used for growl notifications */    
  val defaultGrowlImagePath = ""
  
  /** Sends growl notifications */
  protected def notify(f: GrowlResultFormat) = {
    val g = Growl title(f.title) identifier(f.id.getOrElse(f.title)) message(f.message) image(f.imagePath.getOrElse(defaultGrowlImagePath))
    if(f.sticky) g.sticky() meow else g.meow
  } 
}