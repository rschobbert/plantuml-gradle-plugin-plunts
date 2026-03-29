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
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.gradle.api.GradleException;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RenderingDiagramWriterTest {

  @Test
  void testServerError() throws Exception {
    MockWebServer server = new MockWebServer();
    server.setDispatcher(new Dispatcher() {
      @Override
      public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
        return new MockResponse().setResponseCode(404);
      }
    });
    server.start();

    try {
      String serverUrl = "http://" + server.getHostName() + ":" + server.getPort();
      final File file = new File("build/tmp/test/thisShouldNotBeCreated.png");
      RenderingDiagramWriter writer = new RenderingDiagramWriter(file);
      writer.setPlantumlServer(serverUrl);
      IOException ex = assertThrows(IOException.class, () -> {
        writer.write("Foo", "Bar");
      });
      assertThat(ex.getMessage(), containsString("404"));
      assertThat(ex.getMessage(), containsString(serverUrl));
    } finally {
      server.shutdown();
    }
  }

  @Test
  void testUnchangedSvg() throws Exception {
    MockWebServer server = new MockWebServer();
    server.setDispatcher(new Dispatcher() {
      @Override
      public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
        return new MockResponse().setResponseCode(200).setBody("<svg xml");
      }
    });
    server.start();

    try {
      String serverUrl = "http://" + server.getHostName() + ":" + server.getPort();
      final File file = new File("build/tmp/test/unchanged.svg");

      Files.writeString(file.toPath(), "<!-- Test --><svg xml");
      FileTime lastModifiedTime = Files.getLastModifiedTime(file.toPath());

      RenderingDiagramWriter writer = new RenderingDiagramWriter(file);
      writer.setPlantumlServer(serverUrl);
      writer.write("Foobar", "");
      assertThat(Files.getLastModifiedTime(file.toPath()), is(lastModifiedTime));
    } finally {
      server.shutdown();
    }
  }

  @Test
  void testChangedSvgWithComment() throws Exception {
    MockWebServer server = new MockWebServer();
    server.setDispatcher(new Dispatcher() {
      @Override
      public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
        return new MockResponse().setResponseCode(200).setBody("<svg xml2");
      }
    });
    server.start();

    try {
      String serverUrl = "http://" + server.getHostName() + ":" + server.getPort();
      final File file = new File("build/tmp/test/unchanged.svg");

      Files.writeString(file.toPath(), "<!-- Test --><svg xml1");
      FileTime lastModifiedTime = Files.getLastModifiedTime(file.toPath());

      RenderingDiagramWriter writer = new RenderingDiagramWriter(file);
      writer.setPlantumlServer(serverUrl);
      writer.write("Foobar", "");
      assertThat(Files.getLastModifiedTime(file.toPath()), is(not(lastModifiedTime)));
      assertThat(Files.readString(file.toPath()), is("<!-- Test --><svg xml2"));
    } finally {
      server.shutdown();
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  void testRendererNotFound() throws ClassNotFoundException {
    final ClassLoader cl = mock(ClassLoader.class);
    when(cl.loadClass(any(String.class))).thenThrow(ClassNotFoundException.class);
    final File file = new File("build/tmp/test/thisShouldNotBeCreated.png");
    RenderingDiagramWriter writer = new RenderingDiagramWriter(file);
    writer.setRenderClassLoader(cl);
    assertThrows(GradleException.class, () -> {
      writer.write("Foo", "Bar");
    });
  }

  @Test
  @SuppressWarnings("unchecked")
  void testRendererRuntimeException() {
    final File file = new File("build/tmp/test/thisShouldNotBeCreated.foobar");
    RenderingDiagramWriter writer = new RenderingDiagramWriter(file);
    writer.setRenderClassLoader(getClass().getClassLoader());
    assertThrows(UnsupportedOperationException.class, () -> {
      writer.write("Foo", "Bar");
    });
  }

  @Test
  @SuppressWarnings("unchecked")
  void testInvalidFileNameRenderer() {
    final File file = new File("build/tmp/test/thisShouldNotBeCreated");
    assertThrows(GradleException.class, () -> {
      new RenderingDiagramWriter(file);
    });
  }

  @Test
  @SuppressWarnings("unchecked")
  void testNoRenderer() {
    final File file = new File("build/tmp/test/thisShouldNotBeCreated.foobar");
    RenderingDiagramWriter writer = new RenderingDiagramWriter(file);
    assertThrows(GradleException.class, () -> {
      writer.write("Foo", "Bar");
    });
  }

}
