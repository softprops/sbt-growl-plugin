# Growling Tests

An [sbt](https://github.com/harrah/xsbt#readme) 0.10.* plugin that growls test results.

## Install

To install, create a `build.sbt` file under `project/plugins/` in your sbt project

    resolvers += "less is" at "http://repo.lessis.me"

    libraryDependencies <+= sbtVersion(v => "me.lessis" %% "sbt-growl-plugin" % "0.1.1-%s".format(v))

Then run your tests with the `test` task. If you have [growl](http://growl.info/) installed you should get test feedback as growl notifications

Test failures will `stick` until all tests pass.

## todo

    * support like notifiers on other OS's

2010-2011 Doug Tangren (softprops)
