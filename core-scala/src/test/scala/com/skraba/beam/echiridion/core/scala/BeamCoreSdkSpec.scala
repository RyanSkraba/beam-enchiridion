package com.skraba.beam.echiridion.core.scala

import org.apache.beam.sdk.options.{PipelineOptions, PipelineOptionsFactory}
import org.apache.beam.sdk.testing.{PAssert, TestPipeline}
import org.apache.beam.sdk.transforms.{
  Create,
  Filter,
  MapElements,
  ProcessFunction
}
import org.apache.beam.sdk.values.TypeDescriptors
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funspec.AnyFunSpecLike
import org.scalatest.matchers.should.Matchers

/** Unit tests for the Java SDK using Scala.
  */
class BeamCoreSdkSpec
    extends AnyFunSpecLike
    with Matchers
    with BeforeAndAfterEach {

  var pipeline: TestPipeline = null

  override def beforeEach() = {
    // Using test pipeline outside of a JUnit @Rule
    pipeline = TestPipeline.create
    pipeline.enableAbandonedNodeEnforcement(false)
  }

  describe("Beam core SDK functions") {

    it("can run a filter.") {
      // Applying a filter.
      val predicate: ProcessFunction[String, java.lang.Boolean] =
        m => m.length == 3
      val output = pipeline
        .apply(Create.of("one", "two", "three"))
        .apply(Filter.by(predicate))
      PAssert.that(output).containsInAnyOrder("one", "two")

      // Run the test.
      pipeline.run();
    }

    it("can create pipeline options.") {
      val args: Array[String] = Array("--jobName=MyJob")
      val options: PipelineOptions =
        PipelineOptionsFactory.fromArgs(args: _*).create()

      options.getJobName shouldBe "MyJob"
    }

    it("can run a map.") {
      // Applying a filter.
      val predicate: ProcessFunction[String, java.lang.Boolean] =
        m => m.length == 3
      val output = pipeline
        .apply(Create.of("one", "two", "three"))
        .apply(MapElements.into(TypeDescriptors.booleans).via(predicate))
      PAssert.that(output).containsInAnyOrder(true, true, false)

      // Run the test.
      pipeline.run();
    }
  }

}
