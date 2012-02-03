# Growling Tests

An [sbt](https://github.com/harrah/xsbt#readme) 0.10.* plugin that growls/notifies test results.

## Install

### On Mac

Install the [growl](http://growl.info/) extra [growlnotify](http://growl.info/extras.php#growlnotify) commandline client, make sure it's in your path and you have growl turned on.

### On Ubuntu

Install the `libnotify-bin` package.

    sudo apt-get install libnotify-bin

### Project Configuration

To install on a per-project basis, add the following to your plugin definition file

    resolvers += "less is" at "http://repo.lessis.me"

    addSbtPlugin("me.lessis" % "sbt-growl-plugin" % "0.1.3")

To install globally, create a `Build.scala` file under `~/.sbt/plugins/project` directory and add the following

    import sbt._
    object PluginDef extends Build {
      override def projects = Seq(root)
      lazy val root = Project("plugins", file(".")) dependsOn(growl)
      lazy val growl = uri("git://github.com/softprops/sbt-growl-plugin.git#0.1.3")
    }

Run your tests with the sbt `test` task and you'll see the magikz.

## Configuring Icons

By default the growl plugin looks for icons in `~/.sbt/growl/icons/`.  Specifically, it looks for:

* `pass.png` - used when tests pass
* `fail.png` - used when tests fail
* `error.png` - used for catastrophic failures

If an icon is not found for Growl, the plugin extracts a default icon and places it in `~/.sbt/growl/icons/`.

The directory in which Growl looks for icons can be configured by adding this to your `build.sbt` file:

    defaultImagePath in Growl := "/my/better/path"

You can configure images individually by reconfiguring the GrowlTestImages class.  e.g.

    (GrowlKeys.images in GrowlKeys.Growl) <<= (GrowlKeys.images in GrowlKeys.Growl)(i => i.copy(fail = Some("/better/fail/icon.png")))


## todo

* support like notifiers on other OS's

2010-2011 Doug Tangren (softprops) + Josh Suereth (jsuereth)
