package com.skraba.beam.enchiridion.core.pi;

import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.KV;

/** For every incoming record,throw away its data and return a random point in the unit square. */
public class ThrowDart extends SimpleFunction<Long, KV<Double, Double>> {

  @Override
  public KV<Double, Double> apply(Long input) {
    return KV.of(Math.random(), Math.random());
  }
}
