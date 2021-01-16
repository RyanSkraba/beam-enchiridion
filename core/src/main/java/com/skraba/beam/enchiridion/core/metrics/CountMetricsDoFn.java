package com.skraba.beam.enchiridion.core.metrics;

import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.metrics.Counter;
import org.apache.beam.sdk.metrics.MetricNameFilter;
import org.apache.beam.sdk.metrics.MetricQueryResults;
import org.apache.beam.sdk.metrics.MetricResult;
import org.apache.beam.sdk.metrics.Metrics;
import org.apache.beam.sdk.metrics.MetricsFilter;
import org.apache.beam.sdk.transforms.DoFn;

/**
 * Passes through all elements, but counts them with Beam metrics.
 *
 * <p>The count can be extracted from a {@link PipelineResult} with just the name.
 *
 * @param <T> Ignored type, passed through.
 */
public class CountMetricsDoFn<T> extends DoFn<T, T> {

  public static final String DEFAULT_NAME = "count";

  private final String name;

  private Counter counter;

  private CountMetricsDoFn(String name) {
    this.name = name;
    this.counter = Metrics.counter(CountMetricsDoFn.class, name);
  }

  /** @return a metrics-counting pass-through function with the default name. */
  public static <T> CountMetricsDoFn<T> of() {
    return new CountMetricsDoFn<T>(DEFAULT_NAME);
  }

  /** @return a metrics-counting pass-through function with the given name. */
  public static <T> CountMetricsDoFn<T> of(String name) {
    return new CountMetricsDoFn<>(name);
  }

  /** @return an identifying name for this metric */
  public String getName() {
    return name;
  }

  /** Pass through the data without modification. */
  @ProcessElement
  public void processElement(ProcessContext c) {
    counter.inc();
    c.output(c.element());
  }

  /**
   * Count all of the rows that all instances of this DoFn have encountered.
   *
   * @param result The results of the pipeline, containing the metrics.
   * @return The sum total of all metrics counted by this class.
   */
  public static long getCount(PipelineResult result, String name) {
    MetricQueryResults metrics =
        result
            .metrics()
            .queryMetrics(
                MetricsFilter.builder()
                    .addNameFilter(MetricNameFilter.named(CountMetricsDoFn.class, name))
                    .build());
    long count = 0;
    for (MetricResult<Long> c : metrics.getCounters()) {
      count += c.getAttempted();
    }
    return count;
  }
}
