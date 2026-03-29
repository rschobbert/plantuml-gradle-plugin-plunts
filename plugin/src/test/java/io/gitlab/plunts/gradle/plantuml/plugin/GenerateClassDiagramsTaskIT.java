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
package io.gitlab.plunts.gradle.plantuml.plugin;

import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;
import groovyjarjarantlr.StringUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.regex.Pattern;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.GradleInternal;
import org.gradle.api.internal.artifacts.configurations.ConfigurationInternal;
import org.gradle.api.internal.artifacts.configurations.RoleBasedConfigurationContainerInternal;
import org.gradle.api.internal.file.temp.TemporaryFileProvider;
import org.gradle.api.internal.plugins.ExtensionContainerInternal;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.internal.project.taskfactory.TaskIdentity;
import org.gradle.api.internal.project.taskfactory.TaskIdentityFactory;
import org.gradle.api.internal.tasks.DefaultTaskDependency;
import org.gradle.api.internal.tasks.TaskContainerInternal;
import org.gradle.api.internal.tasks.TaskDependencyFactory;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.SourceSetOutput;
import org.gradle.api.tasks.TaskInputs;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.internal.id.ConfigurationCacheableIdFactory;
import org.gradle.internal.service.ServiceRegistry;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.util.Path;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junitpioneer.jupiter.DisableIfTestFails;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@DisableIfTestFails
@TestMethodOrder(OrderAnnotation.class)
class GenerateClassDiagramsTaskIT {

  private static final Pattern PATH_SEPERATOR = Pattern.compile(",");
  private static final Set<File> OUTPUT_FILES = new HashSet<>();
  private static final Set<File> CHECKED_FILES = new HashSet<>();
  private static Buffer pngBuffer = new Buffer();
  private static Buffer svgBuffer = new Buffer();

  private static String getOutputDirectory(String dir) {
    return "build/tmp/test/" + StringUtils.stripFront(dir, '/');
  }

  @BeforeAll
  static void prepareFilesForInsertion() throws IOException {
    prepareFileForInsertion("/examples/app/README.md");
    prepareFileForInsertion("/examples/dto/README.adoc");
    Files.copy(new File("../examples/app/diagrams/vehicle_context.png").toPath(), pngBuffer.outputStream());
    Files.copy(new File("../examples/app/diagrams/vehicle_context.svg").toPath(), svgBuffer.outputStream());
  }

  private static void prepareFileForInsertion(String projectFile) throws IOException {
    File from = new File(".." + projectFile).getCanonicalFile();
    File to = new File(getOutputDirectory(projectFile)).getCanonicalFile();
    try (BufferedReader reader = Files.newBufferedReader(from.toPath())) {
      to.getParentFile().mkdirs();
      try (BufferedWriter writer = Files.newBufferedWriter(to.toPath(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
        boolean charged = false;
        boolean suppress = false;
        String line;
        while ((line = reader.readLine()) != null) {
          if (suppress && "@enduml".equals(line)) {
            suppress = false;
            charged = false;
          }
          if (!suppress) {
            writer.write(line);
            writer.write("\n");
          }
          if (line.startsWith("'")) {
            charged = true;
          } else if (charged && "@startuml".equals(line)) {
            suppress = true;
          } else {
            charged = false;
          }
        }
      }
    }
  }

  @Order(1)
  @ParameterizedTest(name = "generateClassDiagrams {0}")
  @CsvSource(delimiter = ';', value = {
    "/examples/;app/build/classes/java/main/,dto/build/classes/java/main/;FakeDependency.jar;default",
    "/examples/app/;build/classes/java/main/;../examples/dto/build/classes/java/main/;default",
    "/examples/dto/;build/classes/java/main/;FakeDependency.jar;android"
  })
  void generateClassDiagrams(String projectDir, String projectClasspath, String dependencyClasspath, String mode) throws Exception {
    MockWebServer server = new MockWebServer();
    server.setDispatcher(new Dispatcher() {
      @Override
      public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
        if (request.getPath().contains("png")) {
          return new MockResponse().setBody(pngBuffer);
        } else if (request.getPath().contains("svg")) {
          return new MockResponse().setBody(svgBuffer);
        } else {
          return new MockResponse().setResponseCode(404);
        }
      }
    });
    server.start();

    try {
      ProjectInternal project = mockProject(projectDir, projectClasspath, dependencyClasspath, mode);
      ClassDiagramsExtension extension = parseBuildScript(projectDir, project);
      extension.getPlantumlServer().set("http://" + server.getHostName() + ":" + server.getPort());
      TaskIdentity<GenerateClassDiagramsTask> identity = new TaskIdentityFactory(new ConfigurationCacheableIdFactory())
              .create("generateClassDiagrams", GenerateClassDiagramsTask.class, project);
      GenerateClassDiagramsTask task = DefaultTask.injectIntoNewInstance(project, identity, () -> new GenerateClassDiagramsTask(extension, project.getObjects()));
      task.addProjectClasspath(project);
      task.generateClassDiagrams();
      assertThat(task.getOutputFiles().get(), is(not(empty())));
      OUTPUT_FILES.addAll(task.getOutputFiles().get());
    } finally {
      server.shutdown();
    }
  }

  @Order(2)
  @ParameterizedTest(name = "compareOutput {0}")
  @CsvSource({
    "/examples/diagrams/full_package.puml",
    "/examples/app/README.md",
    "/examples/app/diagrams/app_only.puml",
    "/examples/app/diagrams/classs_with_c.puml",
    "/examples/app/diagrams/controllers_with_references.puml",
    "/examples/app/diagrams/entities.puml",
    "/examples/app/diagrams/filtered_members.puml",
    "/examples/app/diagrams/full_package.puml",
    "/examples/app/diagrams/packages.png",
    "/examples/app/diagrams/packages.puml",
    "/examples/app/diagrams/private_members.puml",
    "/examples/app/diagrams/relation_override.puml",
    "/examples/app/diagrams/styled_output.puml",
    "/examples/app/diagrams/together.puml",
    "/examples/app/diagrams/vehicle_context.png",
    "/examples/app/diagrams/vehicle_context.puml",
    "/examples/app/diagrams/vehicle_context.svg",
    "/examples/dto/README.adoc",
    "/examples/dto/diagrams/full_package.puml",
    "/examples/dto/diagrams/tire.eps",
    "/examples/dto/diagrams/tire.pdf",
    "/examples/dto/diagrams/tire.png",
    "/examples/dto/diagrams/tire.svg",
    "/examples/dto/diagrams/tire.tex"
  })
  void compareOutput(String baseline) throws Exception {
    final File baselineFile = new File(".." + baseline).getCanonicalFile();
    final File testOutputFile = new File(getOutputDirectory(baseline)).getCanonicalFile();
    CHECKED_FILES.add(testOutputFile);

    if (baseline.endsWith("puml") || baseline.endsWith("md") || baseline.endsWith("adoc")) {
      assertThat("Difference in file " + baseline, testOutputFile, hasSameTextContentAs(baselineFile));
    } else {
      // do not compare rendered files:
      // - the server is mocked and always produces an identical "result"
      // - local rendering depends on graphviz, which may vary too much
      assertTrue(testOutputFile.exists());
    }
  }

  @Order(3)
  @Test
  void wasEveryOutputChecked() {
    assertThat(CHECKED_FILES, containsInAnyOrder(OUTPUT_FILES.toArray(new File[OUTPUT_FILES.size()])));
  }

  private ClassDiagramsExtension parseBuildScript(String projectDir, Project project) throws IOException, CompilationFailedException {
    CompilerConfiguration config = new CompilerConfiguration();
    config.setScriptBaseClass(GradleScriptMock.class.getName());
    GroovyShell shell = new GroovyShell(config);
    shell.setProperty("project", project);
    GradleScriptMock script = (GradleScriptMock) shell.parse(new File(".." + projectDir + "build.gradle"));
    script.setBaseDir(getOutputDirectory(projectDir));
    script.run();
    return script.getExtension();
  }

  @SuppressWarnings("unchecked")
  private ProjectInternal mockProject(String projectDir, String projectClasspath, String dependencyClasspath, String mode) {
    ProjectInternal project = (ProjectInternal) spy(ProjectBuilder.builder()
            .withGradleUserHomeDir(new File(getOutputDirectory("gradleHome")))
            .withProjectDir(new File(getOutputDirectory(projectDir)))
            .build());
    when(project.projectPath(any(String.class))).thenAnswer(invoc -> Path.path(":" + invoc.getArgumentAt(0, String.class)));
    when(project.identityPath(any(String.class))).thenAnswer(invoc -> Path.path(":" + invoc.getArgumentAt(0, String.class)));
    when(project.getObjects()).thenCallRealMethod();
    when(project.getConfigurations()).thenCallRealMethod();

    GradleInternal gradle = mock(GradleInternal.class);
    when(project.getGradle()).thenReturn(gradle);
    when(gradle.getIdentityPath()).thenReturn(Path.path(":"));

    DefaultTaskDependency defaultTaskDependency = mock(DefaultTaskDependency.class);
    TaskDependencyFactory taskDependencyFactory = mock(TaskDependencyFactory.class);
    when(taskDependencyFactory.configurableDependency()).thenReturn(defaultTaskDependency);
    when(project.getTaskDependencyFactory()).thenReturn(taskDependencyFactory);

    ServiceRegistry serviceRegistry = mock(ServiceRegistry.class);
    when(project.getServices()).thenReturn(serviceRegistry);

    ProjectLayout layout = mock(ProjectLayout.class);
    when(project.getLayout()).thenReturn(layout);

    Directory projectDirectory = mock(Directory.class);
    when(layout.getProjectDirectory()).thenReturn(projectDirectory);
    when(projectDirectory.file((String) any(String.class))).thenAnswer(new Answer<RegularFile>() {
      @Override
      public RegularFile answer(InvocationOnMock invocation) throws Throwable {
        String path = getOutputDirectory(projectDir + invocation.getArgumentAt(0, String.class));
        return () -> new File(path).getAbsoluteFile();
      }
    });

    TemporaryFileProvider temporaryFileProvider = mock(TemporaryFileProvider.class);
    when(temporaryFileProvider.newTemporaryDirectory(any(String.class)))
            .thenAnswer(invoc -> {
              final File tmpDir = new File(getOutputDirectory(invoc.getArgumentAt(0, String.class)));
              tmpDir.mkdirs();
              return tmpDir;
            });
    when(serviceRegistry.get(TemporaryFileProvider.class)).thenReturn(temporaryFileProvider);

    ExtensionContainerInternal extensionContainer = mock(ExtensionContainerInternal.class);
    when(project.getExtensions()).thenReturn(extensionContainer);

    JavaPluginExtension javaPluginExtension = mock(JavaPluginExtension.class);
    when(extensionContainer.findByType(JavaPluginExtension.class)).thenReturn(javaPluginExtension);

    FileCollection compileClasspath = mock(FileCollection.class);
    when(compileClasspath.getFiles()).thenReturn(PATH_SEPERATOR.splitAsStream(dependencyClasspath)
            .map(s -> new File(dependencyClasspath))
            .map(GenerateClassDiagramsTaskIT::toCanonicalFile)
            .collect(toSet()));

    SourceDirectorySet allSources = mock(SourceDirectorySet.class);
    when(allSources.getFiles()).thenReturn(emptySet());

    SourceSetContainer sourceSets;
    List<Task> tasks;
    if ("default".equals(mode)) {
      tasks = emptyList();
      sourceSets = mock(SourceSetContainer.class);

      SourceSet sourceSet = mock(SourceSet.class);
      when(sourceSets.getByName("main")).thenReturn(sourceSet);
      when(sourceSets.iterator()).thenReturn(Set.of(sourceSet).iterator());
      when(sourceSets.stream()).thenReturn(Set.of(sourceSet).stream());
      when(sourceSet.getName()).thenReturn("main");
      when(sourceSet.getCompileJavaTaskName()).thenReturn("compileJava");
      when(sourceSet.getCompileClasspath()).thenReturn(compileClasspath);

      SourceSetOutput sourceSetOutput = mock(SourceSetOutput.class);
      when(sourceSet.getOutput()).thenReturn(sourceSetOutput);

      FileCollection classesDirs = mock(FileCollection.class);
      when(sourceSetOutput.getClassesDirs()).thenReturn(classesDirs);
      when(classesDirs.getFiles()).thenReturn(PATH_SEPERATOR.splitAsStream(projectClasspath)
              .map(s -> new File(".." + projectDir + s))
              .map(GenerateClassDiagramsTaskIT::toCanonicalFile)
              .collect(toSet()));

      when(sourceSet.getAllSource()).thenReturn(allSources);
    } else {
      sourceSets = null;
      assertThat(projectClasspath, not(containsString(",")));

      JavaCompile javaCompileTask = mock(JavaCompile.class);
      when(javaCompileTask.getName()).thenReturn("compileDebugJava");
      DirectoryProperty destinationDirectory = mock(DirectoryProperty.class);
      Provider<File> classesDirProvider = mock(Provider.class);
      when(classesDirProvider.get()).thenReturn(toCanonicalFile(new File(".." + projectDir + projectClasspath)));
      when(destinationDirectory.getAsFile()).thenReturn(classesDirProvider);
      when(javaCompileTask.getDestinationDirectory()).thenReturn(destinationDirectory);
      when(javaCompileTask.getClasspath()).thenReturn(compileClasspath);
      when(javaCompileTask.getSource()).thenReturn(allSources);

      Task kotlinCompileTask = mock(Task.class);
      when(kotlinCompileTask.getName()).thenReturn("compileDebugKotlin");
      TaskInputs kotlinInputs = mock(TaskInputs.class);
      when(kotlinCompileTask.getInputs()).thenReturn(kotlinInputs);
      FileCollection emptyFileCollection = mock(FileCollection.class);
      when(kotlinInputs.getSourceFiles()).thenReturn(emptyFileCollection);
      tasks = List.of(javaCompileTask, kotlinCompileTask);
    }
    when(javaPluginExtension.getSourceSets()).thenReturn(sourceSets);
    when(extensionContainer.findByType(SourceSetContainer.class)).thenReturn(sourceSets);

    TaskContainerInternal taskContainer = mock(TaskContainerInternal.class);
    when(taskContainer.iterator()).thenAnswer(inv -> tasks.iterator());
    when(project.getTasks()).thenReturn(taskContainer);

    Configuration plantumlConfig = mock(ConfigurationInternal.class);
    when(plantumlConfig.getFiles()).thenReturn(Collections.singleton(new File(".")));
    when(plantumlConfig.getName()).thenReturn("plantuml");
    project.getConfigurations().add(plantumlConfig);

    return project;
  }

  private static File toCanonicalFile(File relative) {
    try {
      return relative.getCanonicalFile();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private static Matcher<File> hasSameTextContentAs(File expected) {
    return new BaseMatcher<File>() {

      private int errorLine = -1;
      private Exception errorException;
      private String errorExpected;
      private String errorActual;

      @Override
      public boolean matches(Object o) {
        List<String> expectedLines;
        List<String> actualLines;
        try {
          expectedLines = Files.readAllLines(expected.toPath());
          actualLines = Files.readAllLines(((File) o).toPath());
        } catch (IOException ex) {
          errorException = ex;
          return false;
        }

        final ListIterator<String> it1 = expectedLines.listIterator();
        final ListIterator<String> it2 = actualLines.listIterator();
        while (it1.hasNext()) {
          final String expected = it1.next();
          if (!it2.hasNext()) {
            this.errorLine = it1.nextIndex();
            this.errorExpected = expected;
            return false;
          }
          final String actual = it2.next();
          if (!expected.equals(actual)) {
            this.errorLine = it1.nextIndex();
            this.errorExpected = expected;
            this.errorActual = actual;
            return false;
          }
        }
        if (it2.hasNext()) {
          this.errorLine = it2.nextIndex();
          this.errorActual = it2.next();
          return false;
        }
        return true;
      }

      @Override
      public void describeTo(Description d) {
        if (errorLine < 0) {
          d.appendText("Content equal to " + expected);
        } else if (errorExpected != null) {
          d.appendText("Line " + errorLine + " to be: " + errorExpected);
        } else {
          d.appendText("File to end at line " + errorLine);
        }
      }

      @Override
      public void describeMismatch(Object item, Description description) {
        if (errorException != null) {
          description.appendText(errorException.toString());
        } else if (errorActual != null) {
          description.appendText("was: " + errorActual);
        } else {
          description.appendText("premature EOF");
        }
      }

    };
  }

  private static interface ConfigurationContainerGroovyObject extends RoleBasedConfigurationContainerInternal, GroovyObject {
  }

}
