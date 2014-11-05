import sbt._
import Keys._

object Mappings {
  val dont =
    Seq("-dontoptimize", "-dontshrink", "-dontpreverify")

  val keepAttributesAndParameters =
    Seq("-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod"
      , "-keepparameternames")

  def preshim(update: UpdateReport) = {
    Seq(update.select(module = moduleFilter(organization = "com.amazonaws")).map(f => s"-injars $f(!META-INF/MANIFEST.MF)").mkString("\n")) ++
    keepAttributesAndParameters ++
    Seq("-ignorewarnings"
      , "-printmapping mappings.map"
      , "-keep class com.amazonaws.** { *; }"
      , "-keepclassmembers class com.amazonaws.** { *; }"
      , "-libraryjars <java.home>/lib/rt.jar") ++
    Seq(update.select(module = moduleFilter(organization = "com.amazonaws")).map(f => s"-libraryjars $f(!META-INF/MANIFEST.MF)").mkString("\n")) ++
    Seq("-outjars empty.jar") ++
    dont
  }

  def shim(name: String, version: String, update: UpdateReport, file: File) = {
    val r = IO.readLines(file.getParentFile / "proguard" / "mappings.map")
    val s = r.filter(!_.startsWith(" "))
    val t = s.map(_.replace("-> com.amazonaws", "-> com.ambiata.com.amazonaws"))
    IO.writeLines(file.getParentFile / "proguard" / "aws.map", t)
    Seq(update.select(module = moduleFilter(organization = "com.amazonaws")).map(f => s"-injars $f(!META-INF/MANIFEST.MF)").mkString("\n")) ++
    keepAttributesAndParameters ++
    Seq("-ignorewarnings"
      , "-keep class com.amazonaws.** { *; }"
      , "-keepclassmembers class com.amazonaws.** { *; }"
      , "-applymapping aws.map"
      , "-libraryjars <java.home>/lib/rt.jar") ++
    Seq(update.select(module = moduleFilter(organization = "com.amazonaws")).map(f => s"-libraryjars $f(!META-INF/MANIFEST.MF)").mkString("\n")) ++
    Seq(s"-outjars ${name}-proguard-${version}.jar") ++
    dont
  }

}
