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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

/**
 * This implementation of {@link AbstractDiagramWriter} inserts the result into an existing file.
 * The file must exist and contain the following marker:
 *
 * <pre>
 * <code>
 * 'diagramName
 * &#64;startuml
 * &#64;enduml
 * </code>
 * </pre>
 */
public class InsertingDiagramWriter extends AbstractDiagramWriter {

  /**
   * Constructs the writer with the target file.
   *
   * @param file the output file
   */
  public InsertingDiagramWriter(File file) {
    super(file);
  }

  @Override
  public void write(String name, String diagram) throws IOException {
    Path inputPath = getFile().toPath();
    Path outputPath = getTemporaryDir().toPath().resolve(inputPath.getFileName());

    if (!Files.exists(inputPath)) {
      throw new FileNotFoundException("File not found: " + inputPath + ". Please create it and insert the following marker:\n@startuml " + name + "\n@enduml");
    }

    boolean written;
    try (BufferedReader reader = Files.newBufferedReader(inputPath)) {
      try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
        written = insertDiagram(reader, writer, name, diagram);
      }
    }

    if (written) {
      Files.move(outputPath, inputPath, StandardCopyOption.REPLACE_EXISTING);
    } else {
      throw new IOException("Marker not found! Please add the following lines to " + inputPath + "\n@startuml " + name + "\n@enduml");
    }
  }

  private boolean insertDiagram(final BufferedReader reader, final BufferedWriter writer, String name, String diagram) throws IOException {
    String legacyMarker = "'" + name;
    String startUml = "@startuml " + name;
    boolean written = false;
    boolean charged = false;
    boolean suppress = false;
    String line;
    while ((line = reader.readLine()) != null) {
      if (legacyMarker.equals(line)) {
        charged = true;
        continue;
      } else if (startUml.equals(line) || (charged && "@startuml".equals(line))) {
        charged = true;
        suppress = true;
      } else if (charged) {
        charged = false;
      }
      if (!suppress) {
        writer.write(line);
        writer.write("\n");
      }
      if (suppress && "@enduml".equals(line)) {
        suppress = false;
        charged = false;
        writer.write(diagram);
        written = true;
      }
    }
    return written;
  }

}
