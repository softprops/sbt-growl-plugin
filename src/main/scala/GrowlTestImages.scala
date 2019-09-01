package growl

import sbt._

/** 
 * Container for growl test image paths.
 * TODO - Allow *way* more flexibility on icons... if possible.
 */
class GrowlTestImages(pass: Option[String], fail: Option[String], error: Option[String]) {
  import GrowlTestImages._
  // TODO - Assume we always have icons, because of the hacky defaults below.
  def passIcon = Some(pass.getOrElse(defaultPass))
  def failIcon = Some(fail.getOrElse(defaultFail))
  def errorIcon = Some(error.getOrElse(defaultError))

  final def copy(pass: Option[String] = passIcon, fail: Option[String] = failIcon, error: Option[String] = errorIcon): GrowlTestImages = 
    new GrowlTestImages(pass,fail,error)

  override def toString = "GrowlTestImages("+passIcon+","+failIcon+","+errorIcon+")"
}

object GrowlTestImages {
  def apply(pass: Option[String], fail: Option[String], error: Option[String]) =
    new GrowlTestImages(pass,fail,error)  

  def iconPath(iconFilename: String): String = {
    val iconImg = file(System.getProperty("user.home")) / ".sbt" / "growl" / "icons" / iconFilename
    if (!iconImg.exists) {
      IO.createDirectory(iconImg.getParentFile)
      IO.transfer(getClass.getClassLoader.getResourceAsStream(iconFilename), iconImg)
    }
    iconImg.getAbsolutePath
  }

  lazy val defaultPass = iconPath("pass.png")
  lazy val defaultFail = iconPath("fail.png")
  lazy val defaultError = iconPath("error.png")

}
