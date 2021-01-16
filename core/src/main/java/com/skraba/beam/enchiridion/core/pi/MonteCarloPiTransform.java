package com.skraba.beam.enchiridion.core.pi;

import org.apache.beam.sdk.transforms.Combine;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.Filter;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;

/**
 * Use the Monte Carlo method to calculate PI.
 *
 * <p>This is a neat, but inefficient way to calculate PI in a distributed system. It takes a
 * collection of input elements. Each input element is ignored and replaced by a random number, used
 * in the calculation. The output is the value of Pi from the analysed random inputs.
 *
 * <p>A circle with a radius of 1 unit has an area of exactly PI units squared. If the centre is the
 * origin, one quarter of the circle will overlap with the positive unit square.
 *
 * <p>In other words if you throw a dart randomly in the unit square, you have PI/4 chance that it
 * lies within the circle (distance from the origin <= 1 unit). By throwing more and more darts and
 * checking the distance of each from the origin, you can get a more and more precise calculation of
 * PI.
 *
 * <p>Unlike other methods, you have to generate an order of magnitude more dart throws for each
 * additional decimal point of precision.
 */
public class MonteCarloPiTransform extends PTransform<PCollection<Long>, PCollection<Double>> {

  @Override
  public PCollection<Double> expand(PCollection<Long> input) {
    // Count the number of incoming records in total.
    final PCollectionView<Long> count =
        input.apply(Combine.globally(Count.<Long>combineFn()).withoutDefaults().asSingletonView());

    // Turn each record into a dart throw.
    PCollection<KV<Double, Double>> points =
        input.apply("RewriteInputs", MapElements.via(new ThrowDart()));
    // Calculate the distance (squared) from the origin of each throw.
    PCollection<Double> distance =
        points.apply("MeasureDistance", ParDo.of(new MeasureDistanceDoFn()));
    // Throw away all records that are outside of the circle.
    PCollection<Double> inCircle = distance.apply("FilterInCircle", Filter.lessThanEq(1.0d));

    // Count the number of darts inside the circle.
    PCollection<Long> inCircleCount = inCircle.apply(Count.globally());

    // Do the division in a PTransform.
    return inCircleCount.apply(
        "MonteCarlePi",
        ParDo.of(
                new DoFn<Long, Double>() {
                  @ProcessElement
                  public void processElement(ProcessContext c) {
                    // The element is the number of darts in the circle.
                    // The side input is the total number of darts thrown.
                    c.output(4.0d * c.element() / c.sideInput(count));
                  }
                })
            .withSideInputs(count));
  }
}
