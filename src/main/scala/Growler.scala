package growl

import sbt._
import org.scalatools.testing.{Logger => TLogger, Event => TEvent, Result => TResult}

/** RRAAAAAAAAAWWWWR -  Yes, it's that scary. */
trait Growler {
  /** Sends the message to the growling system. */
  def notify(msg: GrowlResultFormat): Unit
}

object Growler {
  def apply(): Growler = {
    def isLibNotifyBinFriendly = try {
      Process("which notify-send").!! matches ".*notify-send\\s+"
    } catch {
      case e: Exception => false
    }

    def isGrowlNotifyFriendly = try {
      (Process("where growlnotify").!!).replaceAll("[\n\r]", " ") matches ".*growlnotify.*"
    } catch {
      case e: Exception => false
    }
    // TODO - Is this enough or too strong?
    def isMac = System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0

    if(isMac) new MacGrowler
    else if(isLibNotifyBinFriendly) new LibNotifyBinGrowler
    else if(isGrowlNotifyFriendly) new GrowlNotifyGrowler
    else new NullGrowler
  }
}

final class MacGrowler extends Growler {
  override def notify(msg: GrowlResultFormat): Unit = {
    val img = msg.imagePath.getOrElse("")
    val base = meow.Growl title(msg.title) identifier(msg.id.getOrElse(msg.title)) message(msg.message)
    val rich = if(img.isEmpty) base else base.image(img)
    (if(msg.sticky) rich.sticky() else rich).meow    
  }
  override def toString = "growl"
}

final class NullGrowler extends Growler {
  override def notify(msg: GrowlResultFormat): Unit = ()
  override def toString = "<no growler found on this system>"
}

// Note: This class uses notify-send which requires libnotify-bin to be installed on Ubuntu.
final class LibNotifyBinGrowler extends Growler {
  override def notify(msg: GrowlResultFormat): Unit = {
    val args = Seq(
      // TODO - Urgency
      // TODO - Categories
      // time-to-expire
      "-t", if(msg.sticky) "500" else "100",
      // icon - TODO - Ubuntu default icon thing.
      "-i", msg.imagePath.getOrElse(""),
      msg.title, msg.message
      )
    val sender = Process("notify-send" +: args)
    sender.!
  }
  override def toString = "notify-send"
}

// Note: This class uses growlnotify which may be installed on windows
final class GrowlNotifyGrowler extends Growler {
  override def notify(msg: GrowlResultFormat): Unit = {
    val args = Seq(
      "/t:sbt test",
      "/silent:true",
      "/s:" + msg.sticky.toString,
      msg.title + " " + msg.message
      )
    val sender = Process("growlnotify.exe" +: args)
    sender.!
  }
  override def toString = "growlnotify"
}


