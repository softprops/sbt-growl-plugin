package growl

import sbt._
import Keys._
import sbt.Project.Initialize

object GrowlingTests extends Plugin {

  val Growl = config("growl") extend(Runtime)

  val images = SettingKey[GrowlTestImages]("images", "Object defining paths of test images used for growl notification.")
  val exceptionFormatter = SettingKey[(String, Throwable) => GrowlResultFormat]("exception-formatter", "Function used to format test exception.")
  val groupFormatter = SettingKey[(GroupResult => GrowlResultFormat)]("group-formatter", "Function used to format a test group result.")
  val aggregateFormatter = SettingKey[(AggregateResult => GrowlResultFormat)]("aggregate-formatter", "Function used to format an aggregation of test results.")
  val defaultImagePath = SettingKey[String]("default-image-path", "Default path used to resolve growl test images.")
  val growler = SettingKey[Growler]("growler", "Interface used to growl test results at users.  RRRRRRRRR!")

  override val settings = super.settings ++ posterousSettings

  private def growlingTestListenerTask: Initialize[sbt.Task[sbt.TestReportListener]] =
    (groupFormatter in Growl, exceptionFormatter in Growl, aggregateFormatter in Growl, growler in Growl, streams) map {
      (resFmt, expFmt, aggrFmt, growler, out) =>
        new GrowlingTestsListener(resFmt, expFmt, aggrFmt, growler, out.log)
    }

  def growlSettings: Seq[Setting[_]] = inConfig(Growl)(Seq(
    images := GrowlTestImages(None, None, None),
    exceptionFormatter := { (name: String, t: Throwable) =>
      GrowlResultFormat(
        Some("%s Exception" format name),
        "Exception in Test: %s" format name,
        t.getMessage, true, None
      )
    },
    growler <<= defaultImagePath(Growler.apply),
    groupFormatter <<= (images) {
      (imgs) =>
        (res: GroupResult) =>
          GrowlResultFormat(
            Some(res.name),
            res.name,
            res.status match {
              case TestResult.Error  => "Had Errors"
              case TestResult.Passed => "Passed"
              case TestResult.Failed => "Failed"
            },
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
    aggregateFormatter <<= (images) {
      (imgs) =>
        (res: AggregateResult) =>
          GrowlResultFormat(
            Some("All Tests"),
            (res.status match {
              case TestResult.Error  => "Oops"
              case TestResult.Passed => "Nice job"
              case TestResult.Failed => "Try harder"
            }),
            "%d Tests \n- %d Failed\n- %d Errors\n- %d Passed\n- %d Skipped" format(
              res.count, res.failures, res.errors, res.passed, res.skipped
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
    defaultImagePath := ""
  )) ++ Seq(
    testListeners <+= growlingTestListenerTask
  )
}
