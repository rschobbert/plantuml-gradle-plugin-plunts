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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.AllArgsConstructor;
import org.gradle.api.GradleException;

/**
 * Renderer that uses loads a local PlantUML jar to render the diagram.
 */
@AllArgsConstructor
class LocalRenderer {

  private final ClassLoader cl;
  private final String format;

  public byte[] render(String diagram) throws IOException {
    try {
      return doRender(diagram);
    } catch (InvocationTargetException ex) {
      if (ex.getCause() instanceof IOException iOException) {
        throw iOException;
      } else {
        throw new GradleException("Failed to renderer locally", ex);
      }
    } catch (ReflectiveOperationException ex) {
      throw new GradleException("Failed to load local renderer", ex);
    }
  }

  private byte[] doRender(String diagram) throws IOException, ReflectiveOperationException {
    final Class<?> classFileFormat = cl.loadClass("net.sourceforge.plantuml.FileFormat");
    final Class<?> classFileFormatOption = cl.loadClass("net.sourceforge.plantuml.FileFormatOption");
    final Class<?> classSourceStringReader = cl.loadClass("net.sourceforge.plantuml.SourceStringReader");

    Object fileFormat = toFileFormat(format, classFileFormat);
    Object fileFormatOption = classFileFormatOption.getConstructor(classFileFormat, boolean.class).newInstance(fileFormat, true);
    Object sourceStringReader = classSourceStringReader.getConstructor(String.class).newInstance(diagram);

    try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      final Method m = classSourceStringReader.getMethod("outputImage", OutputStream.class, classFileFormatOption);
      m.invoke(sourceStringReader, out, fileFormatOption);
      return out.toByteArray();
    }
  }

  private Object toFileFormat(String format, Class<?> classFileFormat) throws ReflectiveOperationException {
    final Method m = classFileFormat.getMethod("valueOf", String.class);
    switch (format) {
      case "png":
        return m.invoke(null, "PNG");
      case "svg":
        return m.invoke(null, "SVG");
      case "pdf":
        return m.invoke(null, "PDF");
      case "tex":
        return m.invoke(null, "LATEX");
      case "eps":
        return m.invoke(null, "EPS");
      default:
        throw new UnsupportedOperationException("Unsupported format: " + format);
    }
  }

}
