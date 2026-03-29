/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gitlab.plunts.gradle.plantuml.plugin.output;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@Execution(ExecutionMode.CONCURRENT)
class DefaultDiagramWriterTest {

  @Test
  void testExistingFileUnchanged() throws IOException, InterruptedException {
    File file = new File("build/tmp/test/existingFileUnchanged.puml");
    Files.writeString(file.toPath(), "' Comment\nTestUML");
    FileTime lastModifiedTime = Files.getLastModifiedTime(file.toPath());

    Thread.sleep(1111); // getLastModifiedTime may not have enough precision on some systems
    DefaultDiagramWriter writer = new DefaultDiagramWriter(file);
    writer.write("Foobar", "TestUML");
    assertThat(Files.getLastModifiedTime(file.toPath()), is(lastModifiedTime));
  }

  @Test
  void testExistingFileChanged() throws IOException, InterruptedException {
    File file = new File("build/tmp/test/existingFileChanged.puml");
    Files.writeString(file.toPath(), "' Comment 1\n'Comment 2\nTestUML");
    FileTime lastModifiedTime = Files.getLastModifiedTime(file.toPath());

    Thread.sleep(1111); // getLastModifiedTime may not have enough precision on some systems
    DefaultDiagramWriter writer = new DefaultDiagramWriter(file);
    writer.write("Foobar", "TestUML2");
    assertThat(Files.getLastModifiedTime(file.toPath()), is(not(lastModifiedTime)));
    assertThat(Files.readString(file.toPath()), is("' Comment 1\n'Comment 2\nTestUML2"));
  }

  @Test
  void testExistingFileLonger() throws IOException, InterruptedException {
    File file = new File("build/tmp/test/existingFileLonger.puml");
    Files.writeString(file.toPath(), "' Comment 1\n'Comment 2\nTestUML\nTest2");
    FileTime lastModifiedTime = Files.getLastModifiedTime(file.toPath());

    Thread.sleep(1111); // getLastModifiedTime may not have enough precision on some systems
    DefaultDiagramWriter writer = new DefaultDiagramWriter(file);
    writer.write("Foobar", "TestUML");
    assertThat(Files.getLastModifiedTime(file.toPath()), is(not(lastModifiedTime)));
    assertThat(Files.readString(file.toPath()), is("' Comment 1\n'Comment 2\nTestUML"));
  }

  @Test
  void testExistingFileShorter() throws IOException, InterruptedException {
    File file = new File("build/tmp/test/existingFileShorter.puml");
    Files.writeString(file.toPath(), "' Comment 1\n'Comment 2\nTestUML");
    FileTime lastModifiedTime = Files.getLastModifiedTime(file.toPath());

    Thread.sleep(1111); // getLastModifiedTime may not have enough precision on some systems
    DefaultDiagramWriter writer = new DefaultDiagramWriter(file);
    writer.write("Foobar", "TestUML\nTest2");
    assertThat(Files.getLastModifiedTime(file.toPath()), is(not(lastModifiedTime)));
    assertThat(Files.readString(file.toPath()), is("' Comment 1\n'Comment 2\nTestUML\nTest2"));
  }

}
