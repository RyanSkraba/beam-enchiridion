The Enchiridion: Beam Core 
==========================

Examples for the beam core Java SDK.

The `pom.xml` relies on `beam-sdks-java-core` but does not have any of the necessary runner 
libraries that are necessary to execute the jobs in any given framework.

This illustrates how to create and test runner-independent Beam model objects like PTransform, 
DoFn, side inputs, etc