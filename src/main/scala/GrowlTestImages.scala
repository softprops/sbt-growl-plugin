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

  final def copy(pass: Option[String] = passIcon, fail: Option[String] = failIcon, error: Option[String] = errorIcon): GrowlTestImages = 
    new GrowlTestImages(pass,fail,error)

  override def toString = "GrowlTestImages("+passIcon+","+failIcon+","+errorIcon+")"
}

object GrowlTestImages {
  def apply(pass: Option[String], fail: Option[String], error: Option[String]) =
    new GrowlTestImages(pass,fail,error)  

  /** The default icon location for everything. */
  lazy val defaultIcon = {
    val img = file(System.getProperty("user.home")) / ".sbt" / "growl" / "icons" / "scala-logo.png"
    if(!img.exists) {
      IO.createDirectory(img.getParentFile)
      IO.transfer(getClass.getClassLoader.getResourceAsStream("scala-logo.png"), img)
    }
    img.getAbsolutePath     
  }
  
}
