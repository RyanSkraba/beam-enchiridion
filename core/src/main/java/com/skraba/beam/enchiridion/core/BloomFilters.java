package com.skraba.beam.enchiridion.core;

import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.transforms.Combine;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.vendor.guava.v26_0_jre.com.google.common.base.Charsets;
import org.apache.beam.vendor.guava.v26_0_jre.com.google.common.hash.BloomFilter;
import org.apache.beam.vendor.guava.v26_0_jre.com.google.common.hash.Funnel;
import org.apache.beam.vendor.guava.v26_0_jre.com.google.common.hash.Funnels;

/**
 * Implements transformations for creating and filtering on Bloom filters.
 *
 * <p>This uses the vendored guava {@link BloomFilter} packaged with Beam.
 */
public class BloomFilters {

  /**
   * @param funnel A consumer for the elements.
   * @param expectedSize The expected number of elements in the collection.
   * @param fpp The approximate false positive probability desired.
   * @return A transformation that creates a singleton view of a BloomFilter with all the elements
   *     inside.
   */
  public static <T> PTransform<PCollection<T>, PCollectionView<BloomFilter<T>>> of(
      Funnel<? super T> funnel, long expectedSize, double fpp) {
    return new PTransform<>() {

      @Override
      public PCollectionView<BloomFilter<T>> expand(PCollection<T> input) {
        PCollection<BloomFilter<T>> in =
            input
                .apply(Combine.globally(new BloomFilterCreateFn<T>(funnel, expectedSize, fpp)))
                .setCoder(SerializableCoder.of((Class<BloomFilter<T>>) (Class) BloomFilter.class));
        return in.apply(View.asSingleton());
      }
    };
  }

  /**
   * Create a bloom filter over a String collection.
   *
   * @param expectedSize The expected number of elements in the collection.
   * @param fpp The approximate false positive probability desired.
   * @return A transformation that creates a singleton view of a BloomFilter with all the elements
   *     inside.
   */
  public static PTransform<PCollection<String>, PCollectionView<BloomFilter<String>>> ofStrings(
      long expectedSize, double fpp) {
    return of(Funnels.stringFunnel(Charsets.UTF_8), expectedSize, fpp);
  }

  /**
   * Applies a filter to a collection to remove elements that are definitely not in the filter.
   *
   * <p>That is, if an element is excluded by this transformation, it is guaranteed to not have been
   * in the collection that created the Bloom filter. If it is included, it was probably (but not
   * guaranteed) to have been added to the Bloom filter.
   *
   * @param bloomFilter The Bloom filter initialized for a filter collection.
   * @return A subset of the input where elements were probably also added to the filter collection.
   */
  public static <T> PTransform<PCollection<T>, PCollection<T>> filter(
      final PCollectionView<BloomFilter<T>> bloomFilter) {
    return new PTransform<PCollection<T>, PCollection<T>>() {
      @Override
      public PCollection<T> expand(PCollection<T> input) {
        return input.apply(
            ParDo.of(
                    new DoFn<T, T>() {
                      @ProcessElement
                      public void processElement(ProcessContext c) {
                        if (c.sideInput(bloomFilter).mightContain(c.element()))
                          c.output(c.element());
                      }
                    })
                .withSideInputs(bloomFilter));
      }
    };
  }

  /** Create and aggregate a {@link BloomFilter}. */
  private static class BloomFilterCreateFn<T>
      extends Combine.CombineFn<T, BloomFilter<T>, BloomFilter<T>> {
    private final long expectedSize;
    private final double fpp;
    private final Funnel<? super T> funnel;

    public BloomFilterCreateFn(Funnel<? super T> funnel, long expectedSize, double fpp) {
      this.funnel = funnel;
      this.expectedSize = expectedSize;
      this.fpp = fpp;
    }

    @Override
    public BloomFilter<T> createAccumulator() {
      return BloomFilter.create(funnel, expectedSize, fpp);
    }

    @Override
    public BloomFilter<T> addInput(BloomFilter<T> mutableAccumulator, T input) {
      mutableAccumulator.put(input);
      return mutableAccumulator;
    }

    @Override
    public BloomFilter<T> mergeAccumulators(Iterable<BloomFilter<T>> accumulators) {
      BloomFilter<T> merged = null;
      for (BloomFilter<T> b : accumulators) {
        if (merged == null) merged = b;
        else merged.putAll(b);
      }
      return merged;
    }

    @Override
    public BloomFilter<T> extractOutput(BloomFilter<T> accumulator) {
      return accumulator;
    }
  }
}
