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
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Base class for diagram writers, which are write the result of the builder "somewhere".
 *
 * @see DefaultDiagramWriter
 * @see InsertingDiagramWriter
 * @see RenderingDiagramWriter
 */
@Getter
@Setter
@RequiredArgsConstructor
public abstract class AbstractDiagramWriter implements Serializable {

  private final File file;
  private File temporaryDir;
  private String plantumlServer;
  private transient ClassLoader renderClassLoader;

  /**
   * Writes the diagram to the location specified by the writer implementation.
   *
   * @param name the name of the diagram
   * @param diagram the diagram
   * @throws IOException if an I/O error occurs
   */
  public abstract void write(String name, String diagram) throws IOException;

  protected String blendPreemble(String newContent) throws IOException {
    Path outputPath = getFile().toPath();
    if (!Files.exists(outputPath)) {
      return newContent;
    }

    final String currentContent = Files.readString(outputPath);
    final int preembleLength = Math.max(0, currentContent.indexOf(newContent.substring(0, 5)));
    if (currentContent.substring(preembleLength).equals(newContent)) {
      return null;
    } else {
      return currentContent.substring(0, preembleLength) + newContent;
    }
  }

}
