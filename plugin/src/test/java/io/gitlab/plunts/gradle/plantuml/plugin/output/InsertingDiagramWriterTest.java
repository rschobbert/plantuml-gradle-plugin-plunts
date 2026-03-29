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
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InsertingDiagramWriterTest {

  @Test
  void testWriteFileNotFound() {
    final File file = new File("build/tmp/test/thisDoesNotExist.md");
    InsertingDiagramWriter writer = new InsertingDiagramWriter(file);
    writer.setTemporaryDir(new File("build/tmp/test/"));
    FileNotFoundException ex = assertThrows(FileNotFoundException.class, () -> {
      writer.write("Foo", "Bar");
    });
    assertThat(ex.getMessage(), containsString("File not found"));
    assertThat(ex.getMessage(), containsString("thisDoesNotExist.md"));
    assertThat(ex.getMessage(), containsString("@startuml Foo"));
    assertThat(ex.getMessage(), containsString("@enduml"));
  }

  @Test
  void testWriteMarkerNotFound() throws Exception {
    final File file = new File("build/tmp/test/thisIsEmpty.md");
    file.createNewFile();

    InsertingDiagramWriter writer = new InsertingDiagramWriter(file);
    writer.setTemporaryDir(new File("build/tmp/test/"));
    IOException ex = assertThrows(IOException.class, () -> {
      writer.write("Foo", "Bar");
    });
    assertThat(ex.getMessage(), containsString("Marker not found"));
    assertThat(ex.getMessage(), containsString("thisIsEmpty.md"));
    assertThat(ex.getMessage(), containsString("@startuml Foo"));
    assertThat(ex.getMessage(), containsString("@enduml"));
  }

}
