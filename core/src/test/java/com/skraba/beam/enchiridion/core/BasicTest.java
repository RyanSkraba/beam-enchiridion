package com.skraba.beam.enchiridion.core;

import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.Filter;
import org.apache.beam.sdk.values.PCollection;
import org.junit.Rule;
import org.junit.Test;

/** Simple unit tests using Beam. */
public class BasicTest {
  @Rule public TestPipeline pipeline = TestPipeline.create();

  @Test
  public void testCountOInString() {
    PCollection<String> in =
        pipeline.apply(
            "In",
            Create.of(
                "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"));

    PAssert.that(in.apply("AssertCount", Count.globally())).containsInAnyOrder(10L);
    PCollection<String> withO = in.apply(Filter.by(s -> s.contains("o")));
    PAssert.that(withO.apply("AssertWithO", Count.globally())).containsInAnyOrder(3L);

    pipeline.run();
  }
}
