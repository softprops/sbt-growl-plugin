package growl

import sbt._
import Keys._
import sbt.Def.Initialize

object GrowlingTests extends sbt.Plugin {
  import GrowlKeys._

  object GrowlKeys {
    val images = SettingKey[GrowlTestImages]("images", "Object defining paths of test images used for growl notification.")
    val exceptionFormatter = SettingKey[(String, Throwable) => GrowlResultFormat]("exception-formatter", "Function used to format test exception.")
    val groupFormatter = SettingKey[(GroupResult => GrowlResultFormat)]("group-formatter", "Function used to format a test group result.")
    val aggregateFormatter = SettingKey[(AggregateResult => GrowlResultFormat)]("aggregate-formatter", "Function used to format an aggregation of test results.")
    val defaultImagePath = SettingKey[File]("default-image-path", "Default path used to resolve growl test images.")
    val growler = SettingKey[Growler]("growler", "Interface used to growl test results at users.  RRRRRRRRR!")
  }

  val Growl = config("growl") extend(Test)

  override lazy val projectSettings = growlSettings

  private def growlingTestListenerTask: Def.Initialize[sbt.Task[sbt.TestReportListener]] =
    (groupFormatter in Growl, exceptionFormatter in Growl, aggregateFormatter in Growl, growler in Growl, streams) map {
      (resFmt, expFmt, aggrFmt, growler, out) =>
        new GrowlingTestsListener(resFmt, expFmt, aggrFmt, growler, out.log)
    }

  val growlSettings: Seq[Setting[_]] = inConfig(Growl)(Seq(
    images <<= defaultImagePath apply { path =>
      def setIfExists(name: String) = {
        val file = path / name
        if(file.exists) Some(file.getAbsolutePath) else None
      }
      GrowlTestImages(setIfExists("pass.png"), setIfExists("fail.png"), setIfExists("error.png"))
    },
    exceptionFormatter := { (name: String, t: Throwable) =>
      GrowlResultFormat(
        Some("%s Exception" format name),
        "Exception in Test: %s" format name,
        t.getMessage, true, None
      )
    },
    growler := Growler(),
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
              case TestResult.Error  => imgs.errorIcon
              case TestResult.Passed => imgs.passIcon
              case TestResult.Failed => imgs.failIcon
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
              case TestResult.Error  => imgs.errorIcon
              case TestResult.Passed => imgs.passIcon
              case TestResult.Failed => imgs.failIcon
            }
          )
    },
    defaultImagePath := file(System.getProperty("user.home")) / ".sbt" / "growl" / "icons"
  )) ++ Seq(
    testListeners <+= growlingTestListenerTask
  )
}
