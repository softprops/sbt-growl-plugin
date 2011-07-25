# Growling Tests

An [sbt](https://github.com/harrah/xsbt#readme) [0.10.0](http://typesafe.artifactoryonline.com/typesafe/ivy-releases/org.scala-tools.sbt/sbt-launch/0.10.0/sbt-launch.jar) plugin that growls test results.

## Install

### via sbt

To install, create a `build.sbt` file under `project/plugins/` in your sbt project

    resolvers += "less is" at "http://repo.lessis.me"

    libraryDependencies += "me.lessis" %% "sbt-growl-plugin" % "0.1.0"

Then run your tests with the `test` task. If you have [growl](http://growl.info/) installed you should get test feedback as growl notifications

Failures will `stick` until all tests pass.

## todo

    * support like notifiers on other OS's

2010-2011 Doug Tangren (softprops)
