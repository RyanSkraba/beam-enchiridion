package com.skraba.beam.enchiridion.core.pi;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

import org.apache.beam.sdk.io.GenerateSequence;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.values.PCollection;
import org.junit.Rule;
import org.junit.Test;

/** Unit tests for {@link MonteCarloPiTransform}. */
public class MonteCarloPiTransformTest {

  @Rule public TestPipeline pipeline = TestPipeline.create();

  @Test
  public void testBasic() {
    PCollection<Double> pi =
        pipeline.apply(GenerateSequence.from(0).to(1000)).apply(new MonteCarloPiTransform());

    // An assertion on a transform.
    PAssert.that(pi.apply(Count.globally())).containsInAnyOrder(1L);

    // An assertion on every element in a collection.
    PAssert.that(pi)
        .satisfies(
            elements -> {
              for (Double approximation : elements) {
                assertThat(approximation, closeTo(Math.PI, 0.2));
              }
              return null;
            });

    // An assertion on the single element.
    PAssert.thatSingleton(pi)
        .satisfies(
            element -> {
              assertThat(element, closeTo(Math.PI, 0.2));
              return null;
            });

    pipeline.run();
  }
}
