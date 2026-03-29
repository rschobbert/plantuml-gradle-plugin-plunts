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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * This implementation of {@link AbstractDiagramWriter} writes the result to a file. If it does not exist, it will be
 * created (including directories). Any previous content will be overwritten, except for comments at the top.
 */
public class DefaultDiagramWriter extends AbstractDiagramWriter {

  /**
   * Constructs the writer with the target file.
   *
   * @param file the output file
   */
  public DefaultDiagramWriter(File file) {
    super(file);
  }

  @Override
  public void write(String name, String diagram) throws IOException {
    getFile().getParentFile().mkdirs();
    Path outputPath = getFile().toPath();

    String result = blendPreemble(diagram);
    if (result != null) {
      try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
        writer.write(result);
      }
    }
  }

}
