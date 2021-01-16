package com.skraba.beam.enchiridion.core.metrics;

import java.util.Objects;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.metrics.Gauge;
import org.apache.beam.sdk.metrics.GaugeResult;
import org.apache.beam.sdk.metrics.MetricNameFilter;
import org.apache.beam.sdk.metrics.MetricQueryResults;
import org.apache.beam.sdk.metrics.MetricResult;
import org.apache.beam.sdk.metrics.Metrics;
import org.apache.beam.sdk.metrics.MetricsFilter;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.SerializableFunction;

/**
 * Passes through all elements, but retains the last value in a gauge as a long.
 *
 * <p>The element is converted into a long via the {@link SerializableFunction} parameter. This is
 * the value that is stored in the metrics system and returned via the pipeline results.
 *
 * <p>The value can be extracted from a {@link PipelineResult} with just the name.
 *
 * @param <T> Ignored type, passed through.
 */
public class GaugeMetricsDoFn<T> extends DoFn<T, T> {

  public static final String DEFAULT_NAME = "count";

  private final String name;

  private final SerializableFunction<T, Long> converter;

  private final Gauge gauge;

  private GaugeMetricsDoFn(String name, SerializableFunction<T, Long> converter) {
    this.name = name;
    this.converter = converter;
    this.gauge = Metrics.gauge(GaugeMetricsDoFn.class, name);
  }

  /** @return a metrics pass-through function with the default name. */
  public static <T> GaugeMetricsDoFn<T> of() {
    return new GaugeMetricsDoFn<>(DEFAULT_NAME, GaugeMetricsDoFn::convertToLong);
  }

  /** @return a pass-through function with the given name. */
  public static <T> GaugeMetricsDoFn<T> of(String name, SerializableFunction<T, Long> converter) {
    return new GaugeMetricsDoFn<>(name, converter);
  }

  /** @return a pass-through function with the given name, and using the conversion function. */
  public static <T> GaugeMetricsDoFn<T> of(String name) {
    return new GaugeMetricsDoFn<>(name, GaugeMetricsDoFn::convertToLong);
  }

  public static Long convertToLong(Object element) {
    if (element instanceof Long) return (Long) element;
    return 0L;
  }

  /** @return an identifying name for this metric */
  public String getName() {
    return name;
  }

  /** Pass through the data without modification. */
  @ProcessElement
  public void processElement(ProcessContext c) {
    Long gaugeValue = converter.apply(c.element());
    if (gaugeValue != null) gauge.set(gaugeValue);
    c.output(c.element());
  }

  /**
   * @param result The results of the pipeline, containing the metrics.
   * @return The result associated with the gauge of the given name.
   */
  public static long getValue(PipelineResult result, String name) {
    MetricQueryResults metrics =
        result
            .metrics()
            .queryMetrics(
                MetricsFilter.builder()
                    .addNameFilter(MetricNameFilter.named(GaugeMetricsDoFn.class, name))
                    .build());
    for (MetricResult<GaugeResult> c : metrics.getGauges()) {
      return Objects.requireNonNull(c.getAttempted()).getValue();
    }
    return 0;
  }
}
