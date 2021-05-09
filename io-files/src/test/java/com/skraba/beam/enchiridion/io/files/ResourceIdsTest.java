package com.skraba.beam.enchiridion.io.files;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.beam.sdk.io.FileSystems;
import org.apache.beam.sdk.io.fs.MatchResult;
import org.apache.beam.sdk.io.fs.ResolveOptions;
import org.apache.beam.sdk.io.fs.ResourceId;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ResourceIdsTest {

  @Rule public TemporaryFolder tmp = new TemporaryFolder();

  @Test
  public void testLocalResolve() {
    // An existing folder, in the form "/tmp/junit123"
    String tmpFolder = tmp.getRoot().toString();

    // A resource ID doesn't actually have to match reality.  It's not a file.
    ResourceId newDir = FileSystems.matchNewResource(tmpFolder, true);
    assertThat(newDir, not(nullValue()));
    ResourceId newFile = FileSystems.matchNewResource(tmpFolder, false);
    assertThat(newFile, not(nullValue()));

    // TODO: The URL format file:///tmp/junit123 doesn't actually work, contrary to the
    //  LocalFileSystem javadoc.
    assertThat(
        FileSystems.matchNewResource("file:/" + tmpFolder, false).toString(),
        endsWith("/file:" + tmpFolder));

    assertThat(newDir.toString(), is(tmpFolder + "/"));
    assertThat(newFile.toString(), is(tmpFolder));

    assertThat(
        newDir.resolve("*", ResolveOptions.StandardResolveOptions.RESOLVE_DIRECTORY).toString(),
        is(tmpFolder + "/*/"));
    assertThat(
        newDir.resolve("*", ResolveOptions.StandardResolveOptions.RESOLVE_FILE).toString(),
        is(tmpFolder + "/*"));
  }

  @Test
  public void testMatch() throws IOException {
    Setup.setupCSVFiles(tmp.getRoot().toPath());

    ResourceId root = FileSystems.matchNewResource(tmp.getRoot().toString(), true);

    // Match all of the files in the directory.
    List<MatchResult> matches =
        FileSystems.matchResources(
            Arrays.asList(
                // Top level files.
                root.resolve("*", ResolveOptions.StandardResolveOptions.RESOLVE_FILE),
                // Top level directories.
                root.resolve("*", ResolveOptions.StandardResolveOptions.RESOLVE_DIRECTORY),
                // All files recursively.
                root.resolve("**", ResolveOptions.StandardResolveOptions.RESOLVE_FILE),
                // All directories recursively.
                root.resolve("**", ResolveOptions.StandardResolveOptions.RESOLVE_DIRECTORY)));
    assertThat(matches, hasSize(4));

    // Top level files.
    assertThat(matches.get(0).status(), is(MatchResult.Status.OK));
    assertThat(matches.get(0).metadata(), hasSize(2));

    // Top level directories.
    assertThat(matches.get(1).status(), is(MatchResult.Status.OK));
    // TODO: I would expect this to return three directories: a, b, and z
    assertThat(matches.get(1).metadata(), hasSize(2));

    // All files recursively.
    assertThat(matches.get(2).status(), is(MatchResult.Status.OK));
    assertThat(matches.get(2).metadata(), hasSize(8));

    // All directories recursively.
    assertThat(matches.get(3).status(), is(MatchResult.Status.OK));
    // TODO: I would expect this to return six directories: a, b, b/c. b/x, b/x/y and z
    assertThat(matches.get(3).metadata(), hasSize(8));
  }
}
