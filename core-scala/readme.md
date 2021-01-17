The Enchiridion: Beam Core (Scala)
==================================

Examples for the beam core Java SDK in Scala.

**If you're using a lot of Scala, you should probably look to [scio][scio].**  It's cleaner, clearer and well-adapted to functional programming and scala idioms.

However: byte-code is byte-code.  You _can_ use the Java SDK with Scala, and this module demonstrates some of the techniques.

Notes (TL;DR)
-------------

- Explicitly use `java.lang.Boolean` types instead of `scala.Boolean`.
- Avoid inlining lambdas.  Declare your lambdas in a `val` with the exact type you need.
- Seriously, take a look at [scio][scio] if you're doing more than basic glue code!

[scio]: https://github.com/spotify/scio
