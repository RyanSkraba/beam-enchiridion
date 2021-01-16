package com.skraba.beam.enchiridion.core.pi;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;

/** For every incoming point, return its distance squared from the origin. */
public class MeasureDistanceDoFn extends DoFn<KV<Double, Double>, Double> {

  @ProcessElement
  public void processElement(ProcessContext context) {
    KV<Double, Double> point = context.element();
    context.output(point.getKey() * point.getKey() + point.getValue() * point.getValue());
  }
}
