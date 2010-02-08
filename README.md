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
      val growl = "me.lessis" %% "sbt-growl-plugin" % "0.0.1"
    }

Then decorate your project definition under `project/build/` with `GrowlPlugin`

    import sbt._
    class YourProject(info: ProjectInfo) extends DefaultProject(info) with GrowlPlugin

For more information about [sbt](http://code.google.com/p/simple-build-tool/) plugins see the sbt [plugin wiki page](http://code.google.com/p/simple-build-tool/wiki/SbtPlugins).


## todo

    * assign a GrowlTestReportListener.defaultGrowlImagePath
    * integration with http://github.com/jstrachan/webbytest
    * tests 

2010 Doug Tangren (softprops)