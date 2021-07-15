The Beam Enchiridion
==============================================================================

![Java CI](https://github.com/RyanSkraba/beam-enchiridion/workflows/Java%20CI/badge.svg)

_[**Enchiridion**](https://en.wikipedia.org/wiki/Enchiridion): **A small manual or handbook.**_  It's a bit like a tech [cook book](https://www.oreilly.com/search/?query=cookbook), but a bigger, fancier, SEO-optimizabler word.

<!-- 2020/05/25: 920 O'Reilly results
     2020/06/05: 4758 O'Reilly results (but changed the search URL)
     2020/07/30: 5043 O'Reilly results -->

This project describes how to do many common tasks using [Beam](https://beam.apache.org).

Topics
------------------------------------------------------------------------------

| I want to...                 | See...                                                                                                                                                                                                                                                                                     |
|------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| write a simple ParDo         | [ThrowDart](core/src/main/java/com/skraba/beam/enchiridion/core/pi/ThrowDart.java), [MeasureDistanceDoFn](core/src/main/java/com/skraba/beam/enchiridion/core/pi/MeasureDistanceDoFn.java) ([_test_](core/src/test/java/com/skraba/beam/enchiridion/core/pi/MeasureDistanceDoFnTest.java)) |  |
| write a composite PTransform | [MonteCarloPiTransform](core/src/main/java/com/skraba/beam/enchiridion/core/pi/MonteCarloPiTransform.java) ([_test_](core/src/test/java/com/skraba/beam/enchiridion/core/pi/MonteCarloPiTransformTest.java))                                                                               |
| write a source               |                                                                                                                                                                                                                                                                                            |  |
| write a sdf                  |                                                                                                                                                                                                                                                                                            |  |
| side input                   |                                                                                                                                                                                                                                                                                            |  |
| state                        |                                                                                                                                                                                                                                                                                            |  |
| metrics                      |                                                                                                                                                                                                                                                                                            |  |
| streaming                    |                                                                                                                                                                                                                                                                                            |  |
| my own windowing function    |                                                                                                                                                                                                                                                                                            |

Modules
------------------------------------------------------------------------------

| module                             | description                                                          |
|------------------------------------|----------------------------------------------------------------------|
| [core](core/readme.md)             | Examples for the beam core Java SDK.                                 |
| [core-scala](core-scala/readme.md) | Examples for the beam core Java SDK in Scala (but not [scio][scio]). |
| [io-files](io-files/readme.md)     | Examples for reading from and writing to files.                      |

[scio]: https://github.com/spotify/scio

Running with a locally-built SNAPSHOT
------------------------------------------------------------------------------

```bash
# Build beam artifacts and publish to /tmp/snapshots/
cd beam
./gradlew -Ppublishing -PdistMgmtSnapshotsUrl=/tmp/snapshots/ publishToMavenLocal

# Build this project against the snapshots.
cd beam-enchiridion
mvn -Dbeam.version=2.x.0-SNAPSHOT -Plocal-snapshot clean install
```
