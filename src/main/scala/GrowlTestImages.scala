package growl

import sbt._

/** 
 * Container for growl test image paths.
 * TODO - Allow *way* more flexibility on icons... if possible.
 */
class GrowlTestImages(pass: Option[String], fail: Option[String], error: Option[String]) {
  import GrowlTestImages._
  // TODO - Assume we have an icon always, because of the hacky default below.
  def passIcon = Some(pass.getOrElse(defaultIcon))
  def failIcon = Some(fail.getOrElse(defaultIcon))
  def errorIcon = Some(error.getOrElse(defaultIcon))

  override def toString = "GrowlTestImages("+passIcon+","+failIcon+","+errorIcon+")"
}

object GrowlTestImages {
  def apply(pass: Option[String], fail: Option[String], error: Option[String]) =
    new GrowlTestImages(pass,fail,error)  

  /** The default icon location for everything. */
  lazy val defaultIcon = {
    // Note: The danger here is that these could *never* be cleaned up, leading to a huge and unwieldy /tmp directory.
    // We could also think about hiding in the ~/.sbt directory...
    val dir = IO.createTemporaryDirectory
    val file = dir / "scala-logo.png"
    IO.transfer(getClass.getClassLoader.getResourceAsStream("scala-logo.png"), file)
    file.deleteOnExit
    dir.deleteOnExit
    file.getAbsolutePath     
  }
  
}
