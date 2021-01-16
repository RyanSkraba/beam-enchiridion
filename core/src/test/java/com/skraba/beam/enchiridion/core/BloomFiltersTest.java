package com.skraba.beam.enchiridion.core;

import org.apache.beam.sdk.io.GenerateSequence;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.Filter;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.apache.beam.vendor.guava.v26_0_jre.com.google.common.hash.BloomFilter;
import org.junit.Rule;
import org.junit.Test;

/** Unit tests for {@link BloomFilters}. */
public class BloomFiltersTest {
  @Rule public TestPipeline pipeline = TestPipeline.create();

  @Test
  public void testBasic() {

    // Create a filter with "0" to "999"
    PCollectionView<BloomFilter<String>> filter =
        pipeline
            .apply("FilterContentsGenerate", GenerateSequence.from(0).to(1000))
            .apply(
                "FilterContentsToString",
                MapElements.into(TypeDescriptors.strings()).via((String::valueOf)))
            .apply("ToBloom", BloomFilters.ofStrings(1000, 0.01));

    // The input dataset has "500" to "1499"
    PCollection<String> in =
        pipeline
            .apply("InputGenerate", GenerateSequence.from(500).to(1500))
            .apply(
                "InputToString",
                MapElements.into(TypeDescriptors.strings()).via((String::valueOf)));

    // Use the Bloom filter on the input dataset.  There should only be 500 exact matches, and ~1%
    // of 500 (~5) false positives.
    PCollection<String> filtered = in.apply(BloomFilters.filter(filter));

    // In fact, there are exactly 6 false positives.
    PAssert.that(filtered.apply("AssertFilteredCount", Count.globally())).containsInAnyOrder(506L);
    PCollection<String> falsePositives = filtered.apply(Filter.by(s -> s.length() > 3));
    PAssert.that(falsePositives.apply("AssertFalsePositive", Count.globally()))
        .containsInAnyOrder(6L);
    PAssert.that(falsePositives).containsInAnyOrder("1056", "1137", "1220", "1263", "1269", "1392");

    pipeline.run();
  }
}
