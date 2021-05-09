package com.skraba.beam.enchiridion.io.files;

import java.io.IOException;
import org.apache.beam.sdk.io.FileIO;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.values.PCollection;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class RecursiveFilesTests {

  @Rule public TemporaryFolder tmp = new TemporaryFolder();

  @Rule public TestPipeline pipeline = TestPipeline.create();

  /** Checks that a double-star glob recursively matches all possible files. */
  @Test
  public void testFindAllFiles() throws IOException {
    Setup.setupCSVFiles(tmp.getRoot().toPath());

    PCollection<String> inputDirectories =
        pipeline.apply(Create.of(tmp.getRoot().toPath().resolve("**").toString()));

    PCollection<FileIO.ReadableFile> inputFiles =
        inputDirectories.apply(FileIO.matchAll()).apply(FileIO.readMatches());

    PCollection<String> inputContents = inputFiles.apply("Get text contents", TextIO.readFiles());

    PAssert.that(inputContents)
        .containsInAnyOrder(
            "1;one", "2;two", "3;three", "4;four", "5;five", "6;six", "7;seven", "8;eight", "1;un",
            "3;trois", "5;cinq", "7;sept");

    pipeline.run();
  }
}
