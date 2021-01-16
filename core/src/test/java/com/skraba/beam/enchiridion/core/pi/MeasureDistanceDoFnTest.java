package com.skraba.beam.enchiridion.core.pi;

import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.junit.Rule;
import org.junit.Test;

/** Unit tests for {@link MeasureDistanceDoFn}. */
public class MeasureDistanceDoFnTest {

  @Rule public TestPipeline pipeline = TestPipeline.create();

  @Test
  public void testBasic() {
    PCollection<Double> in =
        pipeline
            .apply(Create.of(KV.of(0d, 0d), KV.of(0.5d, 0.5d), KV.of(1d, 1d), KV.of(3d, 4d)))
            .apply("measure", ParDo.of(new MeasureDistanceDoFn()));

    PAssert.that(in).containsInAnyOrder(0d, 0.5d, 2d, 25d);
    pipeline.run();
  }
}
