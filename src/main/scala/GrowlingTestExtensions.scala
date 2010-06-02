package growl

import sbt._

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

/** Mixin for Growl plugin extensions */
trait GrowlingTestExtensions {
  
  /** Default test images */
  val growlTestImages = GrowlTestImages(None, None, None)

  /** Default implementation returns results in the format
  *   Exception in Test name, execption msg.
  *  Override this fn for custom exception formatting.
  *  @return a function that will format a test group name and Exception
  */
  val growlExceptionFormatter: (String, Throwable) => GrowlResultFormat =
    (name:String, t:Throwable) =>
      GrowlResultFormat(
        Some("%s Exception" format name), "Exception in Test: %s" format name, t.getMessage, true, None
      )

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
          case Result.Error  => "Error in %s"
          case Result.Passed => "Passed %s"
          case Result.Failed => "Failed %s"
        }) format res.name, 
        "Tests %s, Failed %s, Errors %s, Skipped %s".format(
          res.count, res.failures, res.errors, res.skipped
        ),  
        res.status match {
          case Result.Error | Result.Failed => true
          case _ => false
        },
        res.status match {
          case Result.Error  => growlTestImages.error
          case Result.Passed => growlTestImages.pass
          case Result.Failed => growlTestImages.fail
        }
      )

  /** TODO Default image path used for growl notifications */    
  val defaultGrowlImagePath = ""
}