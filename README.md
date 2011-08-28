# Growling Tests

An [sbt](https://github.com/harrah/xsbt#readme) 0.10.* plugin that growls/notifies test results.

## Install

### On Mac

To install, create a `build.sbt` file under `project/plugins/` in your sbt project

    resolvers += "less is" at "http://repo.lessis.me"

    libraryDependencies <+= sbtVersion(v => "me.lessis" %% "sbt-growl-plugin" % "0.1.1-%s".format(v))

Then run your tests with the `test` task. If you have [growl](http://growl.info/) installed you should get test feedback as growl notifications

Test failures will `stick` until all tests pass.

### On Ubuntu

To install, first ensure that `libnotify-bin` is installed.

    sudo apt-get install libnotify-bin

Then create a `Build.scala` file under `~/.sbt/plugins/project` directory

    import sbt._
    object PluginDef extends Build {
      override def projects = Seq(root)
      lazy val root = Project("plugins", file(".")) dependsOn(growl)
      lazy val growl = uri("git://github.com/jsuereth/xsbt-growl-plugin.git")
    }

Now all your projects will be outfitted with libnotify abilities.  Just run the `test` task and you'll see the magikz.


## Configuring Icons

By default the growl plugin looks for icons in `~/.sbt/growl/icons/`.  Specifically, it looks for:

* `pass.png` - used when tests pass
* `fail.png` - used when tests fail
* `error.png` - used for catastrophic failures

If an icon is not found for Growl, the plugin extracts a Scala logo and places it in `~/.sbt/growl/icons/scala-logo.png` to use for notifications.

The directory in which Growl looks for icons can be configured by adding this to your `build.sbt` file:

    defaultImagePath in Growl := "/my/better/path"

You can configure images individually by reconfiguring the GrowlTestImages class.  e.g.

    images in Growl <<= (images in Growl)(i => i.copy(fail = Some("/better/fail/icon.png")))


## todo

* support like notifiers on other OS's

2010-2011 Doug Tangren (softprops) + Josh Suereth (jsuereth)
