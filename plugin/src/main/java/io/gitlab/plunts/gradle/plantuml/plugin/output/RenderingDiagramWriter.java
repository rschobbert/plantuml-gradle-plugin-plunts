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
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import org.gradle.api.GradleException;

/**
 * This implementation of {@link AbstractDiagramWriter} uses a PlantUML server to directly render the diagram and write
 * the result to a file. If it does not exist, it will be created (including directories). Any previous content will be
 * overwritten, except for comments at the beginning of SVG files.
 */
public class RenderingDiagramWriter extends AbstractDiagramWriter {

  private final String format;

  /**
   * Constructs the writer with the target file. The file must have an extension indicating the rendering type (e.g. png
   * or svg).
   *
   * @param file the output file
   */
  public RenderingDiagramWriter(File file) {
    super(file);

    final String filename = file.getName();
    final int dot = filename.lastIndexOf('.');
    if (dot < 0) {
      throw new GradleException("File needs an extension: " + filename);
    }
    this.format = filename.substring(dot + 1);
  }

  @Override
  public void write(String name, String diagram) throws IOException {
    getFile().getParentFile().mkdirs();
    Path outputPath = getFile().toPath();

    byte[] result;
    if (getRenderClassLoader() != null) {
      result = new LocalRenderer(getRenderClassLoader(), format).render(diagram);
    } else if (getPlantumlServer() != null) {
      result = new ServerRenderer(getPlantumlServer(), format).render(diagram);
    } else {
      throw new GradleException("Neither 'plantumlServer' nor 'renderClasspath' set");
    }

    boolean contentEqual;
    if (format.equalsIgnoreCase("svg")) {
      String svg = new String(result);
      svg = blendPreemble(svg);
      if (svg == null) {
        contentEqual = true;
      } else {
        contentEqual = false;
        result = svg.getBytes();
      }
    } else if (Files.exists(outputPath)) {
      contentEqual = Arrays.equals(result, Files.readAllBytes(outputPath));
    } else {
      contentEqual = false;
    }

    if (!contentEqual) {
      try (OutputStream writer = Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
        writer.write(result);
      }
    }
  }

}
