package growl

import sbt._
import org.scalatools.testing.{Logger => TLogger, Event => TEvent, Result => TResult}

/** RRAAAAAAAAAWWWWR -  Yes, it's that scary. */
trait Growler {
  /** Sends the message to the growling system. */
  def notify(msg: GrowlResultFormat): Unit
}

object Growler {
  def apply(defaultImagePath: String): Growler = {
    def isLibNotifyBinFriendly = try {
      Process("which notify-send").!! matches ".*notify-send\\s+"
    } catch {
      case e => false
    }
    // TODO - Is this enough or too strong?
    def isMac = System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0

    if(isMac) new MacGrowler(defaultImagePath)
    else if(isLibNotifyBinFriendly) new LibNotifyBinGrowler
    else new NullGrowler
  }
}

final class MacGrowler(defaultImagePath: String) extends Growler {
  override def notify(msg: GrowlResultFormat): Unit = {
    val img = msg.imagePath.getOrElse(defaultImagePath)
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
  // TODO - Load scala-logo.png into some kind of temporary directory for now.  This is a bit ugly.  This should
  // Be moved somewhere useful with different icons for passing and failing tests.  We should make use of the image
  // config and somehow load this as default in absence of user config...
  val defaultIcon = {
    // Note: The danger here is that these could *never* be cleaned up, leading to a huge and unwieldy /tmp directory.
    // We could also think about hiding in the ~/.sbt directory...
    val dir = IO.createTemporaryDirectory
    val file = dir / "scala-logo.png"
    IO.transfer(getClass.getClassLoader.getResourceAsStream("scala-logo.png"), file)
    file.deleteOnExit
    dir.deleteOnExit
    file.getAbsolutePath     
  }
  
  override def notify(msg: GrowlResultFormat): Unit = {
    val args = Seq(
      // TODO - Urgency
      // TODO - Categories
      // time-to-expire
      "-t", if(msg.sticky) "500" else "100",
      // icon - TODO - OS Specific options...
      "-i", msg.imagePath.getOrElse(defaultIcon),
      msg.title, msg.message
      )
    val sender = Process("notify-send" +: args)
    sender !
  }
  override def toString = "notify-send"
}


