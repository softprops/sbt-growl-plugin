# Sbt Growl Plugin

An [sbt](http://code.google.com/p/simple-build-tool/) plugin that growls test results

## Install

Sbt Growl Plugin is [cross built](http://code.google.com/p/simple-build-tool/wiki/CrossBuild) over the following verions versions of scala.

    2.7.3, 2.7.4, 2.7.5, 2.7.6, 2.7.7
   
### via sbt
 
To install, create a `Plugins.scala` file under your `project/plugins/` in your sbt project 

    import sbt._
    class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
      val lessRepo = "lessis repo" at "http://repo.lessis.me"
      val growl = "me.lessis" % "sbt-growl-plugin" % "0.0.3"
    }

Then decorate your project definition under `project/build/` with `GrowlPlugin`

    import sbt._
    class YourProject(info: ProjectInfo) extends DefaultProject(info) with GrowlPlugin 

To provide notification images

    import sbt._
    class YourProject(info: ProjectInfo) extends DefaultProject(info) with GrowlPlugin {
      override val growlTestImages = GrowlTestImages(
        Some("/path/to/pass.png"), 
        Some("/path/to/fail.png"), 
        Some("/path/to/error.png")
      )
    }
  
To provide your own notification formatting
    
    import sbt._
    class YourProject(info: ProjectInfo) extends DefaultProject(info) with GrowlPlugin {
      override val growlResultFormatter = 
       (res: GroupResult) =>
         GrowlResultFormat(
           Some(res.name),
           (res.status match {
             case Result.Error  => ":( %s"
             case Result.Passed => ":)  %s"
             case Result.Failed => ":`( %s"
           }) format res.name, 
           "Total %s, F %s, E %s, S %s".format(
             res.count, res.failures, res.errors, res.skipped
           ),  
           res.status match {
             case Result.Error | Result.Failed => true
             case _ => false
           },
           res.status match {
             case Result.Error  => growlTestImages.error
             case Result.Passed => Some("/path/to/pass.png")
             case Result.Failed => growlTestImages.fail
           }
         )
    }

For more information about [sbt](http://code.google.com/p/simple-build-tool/) plugins see the sbt [plugin wiki page](http://code.google.com/p/simple-build-tool/wiki/SbtPlugins).


## todo

    * assign a GrowlTestReportListener.defaultGrowlImagePath
    * integration with http://github.com/jstrachan/webbytest
    * tests 

2010 Doug Tangren (softprops)