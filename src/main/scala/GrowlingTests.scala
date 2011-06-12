package growl

import sbt._
import Keys._
import sbt.Project.Initialize

object GrowlingTests extends Plugin {

  val growlTestImages = SettingKey[GrowlTestImages]("growl-test-images", "Object defining paths of test images used for growl notification.")
  val growlExceptionFormatter = SettingKey[(String, Throwable) => GrowlResultFormat]("growl-exception-formatter", "Function used to format test exception.")
  val growlResultFormatter = SettingKey[(GroupResult => GrowlResultFormat)]("growl-result-formatter", "Function used to format a test group result.")
  val growlDefaultImagePath = SettingKey[String]("growl-default-image-path", "Default path used to resolve growl test images.")

  private def growlingTestListenerTask: Initialize[sbt.Task[sbt.TestReportListener]] =
    (streams, growlResultFormatter, growlExceptionFormatter, growlDefaultImagePath) map {
      (out, resFmt, expFmt, defaultPath) =>
        new GrowlingTestReportListener(resFmt, expFmt, defaultPath)
    }

  override def settings = Seq(
    growlTestImages := GrowlTestImages(None, None, None),
    growlExceptionFormatter := { (name: String, t: Throwable) =>
      GrowlResultFormat(
        Some("%s Exception" format name),
        "Exception in Test: %s" format name,
        t.getMessage, true, None
      )
    },
    growlResultFormatter <<= (growlTestImages) {
      (imgs) =>
        (res: GroupResult) =>
          GrowlResultFormat(
            Some(res.name),
            (res.status match {
              case TestResult.Error  => "Error in %s"
              case TestResult.Passed => "Passed %s"
              case TestResult.Failed => "Failed %s"
            }) format res.name,
            "Tests %s, Failed %s, Errors %s, Skipped %s".format(
              res.count, res.failures, res.errors, res.skipped
            ),
            res.status match {
              case TestResult.Error | TestResult.Failed => true
              case _ => false
            },
            res.status match {
              case TestResult.Error  => imgs.error
              case TestResult.Passed => imgs.pass
              case TestResult.Failed => imgs.fail
            }
          )
    },
    growlDefaultImagePath := "",
    testListeners <+= growlingTestListenerTask
  )
}
