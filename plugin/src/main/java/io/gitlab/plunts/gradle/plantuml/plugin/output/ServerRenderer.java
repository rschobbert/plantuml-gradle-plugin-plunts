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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.zip.Deflater;
import lombok.AllArgsConstructor;

/**
 * Renderer that uses a PlantUML server to render the diagram.
 */
@AllArgsConstructor
class ServerRenderer {

  private final String serverUrl;
  private final String format;

  public byte[] render(String diagram) throws IOException {
    final Deflater d = new Deflater(Deflater.BEST_COMPRESSION, true);
    d.setInput(diagram.getBytes(StandardCharsets.UTF_8));
    d.finish();

    byte[] output = new byte[diagram.length() * 2];
    int size = d.deflate(output);

    URI uri = URI.create(serverUrl + "/" + format + "/" + encode64(output, size));
    final URLConnection connection = uri.toURL().openConnection();
    assert connection instanceof HttpURLConnection;
    final int responseCode = ((HttpURLConnection) connection).getResponseCode();
    if (responseCode != 200) {
      throw new IOException("Server " + serverUrl + " returned status " + responseCode);
    }
    return connection.getInputStream().readAllBytes();
  }

  private String encode64(byte[] output, int size) {
    assert output.length + 2 >= size;

    StringBuilder sb = new StringBuilder(size * 2);
    for (int i = 0; i < size; i += 3) {
      append3bytes(sb, output[i] & 0xFF, output[i + 1] & 0xFF, output[i + 2] & 0xFF);
    }
    return sb.toString();
  }

  private void append3bytes(StringBuilder sb, int b1, int b2, int b3) {
    int c1 = b1 >> 2;
    int c2 = ((b1 & 0x3) << 4) | (b2 >> 4);
    int c3 = ((b2 & 0xF) << 2) | (b3 >> 6);
    int c4 = b3 & 0x3F;
    sb.append(encode6bit(c1 & 0x3F));
    sb.append(encode6bit(c2 & 0x3F));
    sb.append(encode6bit(c3 & 0x3F));
    sb.append(encode6bit(c4 & 0x3F));
  }

  private char encode6bit(int b) {
    if (b < 10) {
      return (char) ('0' + b);
    }
    b -= 10;
    if (b < 26) {
      return (char) ('A' + b);
    }
    b -= 26;
    if (b < 26) {
      return (char) ('a' + b);
    }
    b -= 26;
    if (b == 0) {
      return '-';
    }
    if (b == 1) {
      return '_';
    }
    throw new IllegalArgumentException("Unmappable byte: " + Integer.toHexString(b));
  }

}
