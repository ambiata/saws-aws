scalacOptions += "-deprecation"

resolvers += "Era7 maven releases" at "http://releases.era7.com.s3.amazonaws.com"

resolvers += Resolver.url("ambiata-oss", new URL("https://ambiata-oss.s3.amazonaws.com"))(Resolver.ivyStylePatterns)

resolvers += Resolver.sonatypeRepo("releases")

resolvers += Resolver.typesafeRepo("releases")

addSbtPlugin("com.ambiata" % "promulgate" % "0.11.0-20141014013725-80c129f")

addSbtPlugin("com.typesafe.sbt" % "sbt-proguard" % "0.2.2" )
