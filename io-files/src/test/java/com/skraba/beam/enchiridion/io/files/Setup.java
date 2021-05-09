package com.skraba.beam.enchiridion.io.files;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Helpers for setting up a test files. */
public class Setup {

  /**
   * Sets up a small set of CSV files in the given directory.
   *
   * <pre>
   * ./a
   * ./a/four.csv
   * ./a/three.csv
   * ./b
   * ./b/c
   * ./b/c/eight.csv
   * ./b/c/seven.csv
   * ./b/five.csv
   * ./b/six.csv
   * ./b/x
   * ./b/x/y
   * ./one.csv
   * ./two.csv
   * ./z
   * </pre>
   *
   * @param root An existing directory to create
   */
  public static void setupCSVFiles(Path root) throws IOException {
    Path aDir = Files.createDirectory(root.resolve("a"));
    Path bDir = Files.createDirectory(root.resolve("b"));
    Path cDir = Files.createDirectory(bDir.resolve("c"));
    Files.write(root.resolve("one.csv"), "1;one\n1;un\n".getBytes(StandardCharsets.UTF_8));
    Files.write(root.resolve("two.csv"), "2;two\n".getBytes(StandardCharsets.UTF_8));
    Files.write(aDir.resolve("three.csv"), "3;three\n3;trois\n".getBytes(StandardCharsets.UTF_8));
    Files.write(aDir.resolve("four.csv"), "4;four\n".getBytes(StandardCharsets.UTF_8));
    Files.write(bDir.resolve("five.csv"), "5;five\n5;cinq\n".getBytes(StandardCharsets.UTF_8));
    Files.write(bDir.resolve("six.csv"), "6;six\n".getBytes(StandardCharsets.UTF_8));
    Files.write(cDir.resolve("seven.csv"), "7;seven\n7;sept\n".getBytes(StandardCharsets.UTF_8));
    Files.write(cDir.resolve("eight.csv"), "8;eight\n".getBytes(StandardCharsets.UTF_8));
    // Directories with no files in them.
    Path xDir = Files.createDirectory(bDir.resolve("x"));
    Path yDir = Files.createDirectory(xDir.resolve("y"));
    Path zDir = Files.createDirectory(root.resolve("z"));
  }
}
