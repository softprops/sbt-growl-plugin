# Sbt Growl Plugin

An [sbt](http://code.google.com/p/simple-build-tool/) plugin that growls test results

## Install

Depends on [meow](http://github.com/softprops/meow/) for notifications.

`maven` hosting is on its way soon. for now you can install both locally via

    ./sbt
    update
    publish-local
    
To install, create a `Plugins.scala` file under your `project/plugins/` in your sbt project 

    import sbt._
    class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
      val a = "me.lessis" % "sbt-growl-plugin" % "0.0.1"
    }

Then decorate your project definition under `project/build/` with `GrowlPlugin`

    import sbt._
    class YourProject(info: ProjectInfo) extends DefaultProject(info) with GrowlPlugin

For more information about [sbt](http://code.google.com/p/simple-build-tool/) plugins see the sbt [plugin wiki page](http://code.google.com/p/simple-build-tool/wiki/SbtPlugins).


## todo

    * publish to a hosted maven repo
    * assign a GrowlTestReportListener.defaultGrowlImagePath
    * integration with http://github.com/jstrachan/webbytest
    * tests 

2010 Doug Tangren (softprops)