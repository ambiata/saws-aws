Saws-aws
========

Amazon `aws-java-sdk`

Reasoning
---------
Hadoop has an incompatible dependency on `aws-java-sdk` with the version in use here.
Therefore we are using Proguard to map the package names in `aws-java-sdk` in use
here to avoid runtime errros.
ProguardPre will create a mapping file of the `aws-java-sdk` and `Mappings.preshim`
will then filter and rename the mappings file before the final Proguard task runs the
mappings over the library.

Final mapping's { `com.amazonaws` -> `com.ambiata.com.amazonaws` }
