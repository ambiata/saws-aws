import sbt._
import Keys._

object Mappings {
  val dont =
    Seq("-dontoptimize", "-dontshrink", "-dontpreverify")

  val keepAttributesAndParameters =
    Seq("-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod"
      , "-keepparameternames"
      , "-adaptresourcefilenames    **.properties,**.gif,**.jpg,**.handlers,**.handler2s"
      , "-adaptresourcefilecontents **.properties,**.handlers,**.handler2s,META-INF/MANIFEST.MF")

  def preshim(update: UpdateReport) = {
    Seq(update.select(module = moduleFilter(organization = "com.amazonaws")).map(f => s"-injars $f(!META-INF/MANIFEST.MF)").mkString("\n")) ++
    Seq(update.select(module = moduleFilter(organization = "org.apache.httpcomponents")).map(f => s"-injars $f(!META-INF/MANIFEST.MF)").mkString("\n")) ++
    Seq(update.select(module = moduleFilter(organization = "com.fasterxml.jackson.core")).map(f => s"-injars $f(!META-INF/MANIFEST.MF)").mkString("\n")) ++
    keepAttributesAndParameters ++
    Seq("-ignorewarnings"
      , "-printmapping mappings.map"
      , "-keep class com.amazonaws.** { *; }"
      , "-keepclassmembers class com.amazonaws.** { *; }"
      , "-keep class org.apache.http.** { *; }"
      , "-keepclassmembers class org.apache.http.** { *; }"
      , "-keep class com.fasterxml.jackson.** { *; }"
      , "-keepclassmembers class com.fasterxml.jackson.** { *; }"
      , "-libraryjars <java.home>/lib/rt.jar") ++
    Seq(update.select().map(f => s"-libraryjars $f(!META-INF/MANIFEST.MF)").mkString("\n")) ++
    Seq("-outjars empty.jar") ++
    dont
  }

  def shim(name: String, version: String, update: UpdateReport, file: File) = {
    val r = IO.readLines(file.getParentFile / "proguard" / "mappings.map")
    val s = r.filter(!_.startsWith(" "))
    val aws = s.map(_.replace("-> com.amazonaws", "-> com.ambiata.com.amazonaws"))
    val http = aws.map(_.replace("-> org.apache.http", "-> com.ambiata.org.apache.http"))
    val xml = http.map(_.replace("-> com.fasterxml.jackson", "-> com.ambiata.com.fasterxml.jackson"))
    val t = xml
    IO.writeLines(file.getParentFile / "proguard" / "aws.map", t)
    Seq(update.select(module = moduleFilter(organization = "com.amazonaws")).map(f => s"-injars $f(!META-INF/MANIFEST.MF)").mkString("\n")) ++
    Seq(update.select(module = moduleFilter(organization = "org.apache.httpcomponents")).map(f => s"-injars $f(!META-INF/MANIFEST.MF)").mkString("\n")) ++
    Seq(update.select(module = moduleFilter(organization = "com.fasterxml.jackson.core")).map(f => s"-injars $f(!META-INF/MANIFEST.MF)").mkString("\n")) ++
    keepAttributesAndParameters ++
    Seq("-ignorewarnings"
      , "-keep class com.amazonaws.** { *; }"
      , "-keepclassmembers class com.amazonaws.** { *; }"
      , "-keep class org.apache.http.** { *; }"
      , "-keepclassmembers class org.apache.http.** { *; }"
      , "-keep class com.fasterxml.jackson.** { *; }"
      , "-keepclassmembers class com.fasterxml.jackson.** { *; }"
      , "-applymapping aws.map"
      , "-libraryjars <java.home>/lib/rt.jar") ++
    Seq(update.select().map(f => s"-libraryjars $f(!META-INF/MANIFEST.MF)").mkString("\n")) ++
    Seq(s"-outjars ${name}-proguard-${version}.jar") ++
    dont
  }

}
