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

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.AbstractClasspathMatcher;
import io.gitlab.plunts.gradle.plantuml.plugin.output.AbstractDiagramWriter;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.inject.Inject;
import lombok.Getter;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFiles;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.compile.JavaCompile;

import static java.util.stream.Collectors.toSet;

/**
 * Gradle task that generates class PlantUML diagrams.
 */
public class GenerateClassDiagramsTask extends DefaultTask {

  @Input
  @Getter
  private final SetProperty<Object> sourceSets;
  @Input
  @Getter
  private final ListProperty<ClassDiagram> diagrams;
  @Input
  @Getter
  private final ListProperty<PackageDiagram> packageDiagrams;
  @Input
  @Optional
  @Getter
  private final Property<String> plantumlServer;
  @InputFiles
  @Optional
  @Getter
  private final Property<FileCollection> renderClasspath;
  private transient ClassLoader renderClassLoader;
  private final Set<File> projectClasspath = new HashSet<>();
  private FileCollection dependencyClasspath = null;
  @InputFiles
  @Getter
  private final SetProperty<File> allSources;
  @OutputFiles
  @Getter
  public final Provider<Set<File>> outputFiles;

  @Inject
  public GenerateClassDiagramsTask(final ClassDiagramsExtension extension, ObjectFactory objectFactory) {
    this.sourceSets = extension.getSourceSets();
    this.diagrams = extension.getDiagrams();
    this.packageDiagrams = extension.getPackageDiagrams();
    this.plantumlServer = extension.getPlantumlServer();
    this.renderClasspath = extension.getRenderClasspath();
    this.allSources = objectFactory.setProperty(File.class);
    this.outputFiles = extension.getDiagrams().zip(extension.getPackageDiagrams(), (resolvedDiagrams, resolvedPackageDiagrams)
            -> Stream.concat(resolvedDiagrams.stream(), resolvedPackageDiagrams.stream())
                    .map(Diagram::getWriters)
                    .flatMap(List::stream)
                    .map(AbstractDiagramWriter::getFile)
                    .map(f -> {
                      try {
                        return f.getCanonicalFile();
                      } catch (IOException ex) {
                        return f.getAbsoluteFile();
                      }
                    }).collect(toSet()));
    setGroup("Documentation");
    setDescription("Generates PlantUML diagrams for the main source code.");
  }

  void addProjectClasspath(final Project project) {
    final Predicate<SourceSet> sourceSetPredicate = getSourceSetPredicate();
    final SourceSetContainer sourceSetContainer = project.getExtensions().findByType(SourceSetContainer.class);
    if (sourceSetContainer != null) {
      sourceSetContainer.stream()
              .filter(sourceSetPredicate::test)
              .forEachOrdered(sourceSet -> addSourceSet(project, sourceSet));
    }

    try {
      getClass().getClassLoader().loadClass("com.android.build.api.dsl.ApplicationExtension");
      addAndroidClasspath(project);
    } catch (ClassNotFoundException ignore) {
      // android plugin not loaded => everything is fine
    }
  }

  private Predicate<SourceSet> getSourceSetPredicate() {
    Set<Object> resolvedSourceSets = this.sourceSets.get();
    if (resolvedSourceSets.isEmpty()) {
      return s -> !s.getName().toLowerCase().contains("test");
    } else {
      return s -> resolvedSourceSets.contains(s) || resolvedSourceSets.contains(s.getName());
    }
  }

  private void addSourceSet(Project project, SourceSet sourceSet) {
    this.projectClasspath.addAll(sourceSet.getOutput().getClassesDirs().getFiles());
    if (dependencyClasspath == null) {
      this.dependencyClasspath = sourceSet.getCompileClasspath();
    } else {
      this.dependencyClasspath = this.dependencyClasspath.plus(sourceSet.getCompileClasspath());
    }
    this.allSources.addAll(sourceSet.getAllSource().getFiles());
    dependsOn(project.getTasksByName(sourceSet.getCompileJavaTaskName(), false));
  }

  private void addAndroidClasspath(Project project) {
    for (Task task : project.getTasks()) {
      final String taskName = task.getName().toLowerCase(Locale.ROOT);
      if (taskName.contains("compile") && taskName.contains("debug") && !taskName.contains("test")) {
        if (task instanceof JavaCompile javaCompile) {
          this.projectClasspath.add(javaCompile.getDestinationDirectory().getAsFile().get());
          if (dependencyClasspath == null) {
            this.dependencyClasspath = javaCompile.getClasspath();
          } else {
            this.dependencyClasspath = this.dependencyClasspath.plus(javaCompile.getClasspath());
          }
          this.allSources.addAll(javaCompile.getSource().getFiles());
          dependsOn(task);
        } else if (taskName.contains("kotlin")) {
          this.allSources.addAll(task.getInputs().getSourceFiles().getFiles());
        }
      }
    }
  }

  @TaskAction
  public void generateClassDiagrams() throws IOException {
    for (ClassDiagram diagram : diagrams.get()) {
      generateDiagram(new ClassDiagramBuilder(diagram));
    }
    for (PackageDiagram diagram : packageDiagrams.get()) {
      generateDiagram(new PackageDiagramBuilder(diagram));
    }
  }

  private void generateDiagram(DiagramBuilder<? extends Diagram> builder) throws IOException {
    final Diagram diagram = builder.getDiagram();
    final ClassGraph classGraph = new ClassGraph()
            .enableClassInfo().ignoreClassVisibility()
            .enableFieldInfo().ignoreFieldVisibility()
            .enableMethodInfo().ignoreMethodVisibility()
            .enableAnnotationInfo()
            .enableExternalClasses()
            .enableInterClassDependencies()
            .overrideClasspath(this.projectClasspath)
            .overrideClasspath(this.dependencyClasspath.getFiles());

    if (diagram.getBaselineIncludeExclude().isEmpty()) {
      throw new InvalidUserDataException("diagram " + diagram.getName() + " must define at least one class or package include");
    }

    Set<String> projectPackages = findProjectPackages();
    if (!projectPackages.isEmpty()) {
      classGraph.acceptPackages(projectPackages.toArray(new String[projectPackages.size()]));
    }

    diagram.getBaselineIncludeExclude().getIncludes().stream().forEach(matcher -> matcher.configureAccept(classGraph));
    diagram.getBaselineIncludeExclude().getExcludes().stream().forEach(matcher -> matcher.configureReject(classGraph));
    diagram.getReferencesIncludeExclude().getIncludes().stream().forEach(matcher -> matcher.configureAccept(classGraph));
    diagram.getSuperclassIncludeExclude().getIncludes().stream().forEach(matcher -> matcher.configureAccept(classGraph));
    diagram.getSubclassIncludeExclude().getIncludes().stream().forEach(matcher -> matcher.configureAccept(classGraph));

    AbstractClasspathMatcher.setProjectClasspath(projectClasspath);
    try (ScanResult scanResult = classGraph.scan()) {
      int baselineClasses = 0;
      for (ClassInfo classInfo : scanResult.getAllClasses()) {
        if (diagram.getBaselineIncludeExclude().isIncluded(classInfo)) {
          baselineClasses++;
          builder.addClass(classInfo);
        }
      }
      if (baselineClasses == 0) {
        throw new InvalidUserDataException("diagram " + diagram.getName() + " does not include any classes");
      }

      final String plantuml = builder.build();
      for (AbstractDiagramWriter writer : diagram.getWriters()) {
        writer.setTemporaryDir(getTemporaryDir());
        writer.setPlantumlServer(this.plantumlServer.get());
        writer.setRenderClassLoader(createOrGetRenderClassLoader());
        writer.write(diagram.getName(), plantuml);
      }
    } finally {
      AbstractClasspathMatcher.setProjectClasspath(null);
    }
  }

  private Set<String> findProjectPackages() {
    Set<String> result = new HashSet<>();
    for (File file : projectClasspath) {
      if (file.isDirectory()) {
        findProjectPackages(null, file, result);
      }
    }
    return result;
  }

  private boolean findProjectPackages(String prefix, File folder, Set<String> target) {
    boolean hasClasses = false;
    for (File file : folder.listFiles()) {
      if (file.getName().endsWith(".class")) {
        hasClasses = true;
      } else if (file.isDirectory()) {
        String packageName = prefix == null ? file.getName() : prefix + "." + file.getName();
        if (findProjectPackages(packageName, file, target)) {
          target.add(packageName);
        }
      }
    }
    return hasClasses;
  }

  private ClassLoader createOrGetRenderClassLoader() {
    if (!this.renderClasspath.isPresent()) {
      return null;
    }
    if (this.renderClassLoader != null) {
      return this.renderClassLoader;
    }

    final List<URL> urls = renderClasspath.get().getFiles().stream().map(File::toURI).map(t -> {
      try {
        return t.toURL();
      } catch (MalformedURLException ex) {
        throw new GradleException("Invalid renderClasspath element " + t, ex);
      }
    }).toList();

    if (urls.isEmpty()) {
      throw new GradleException("renderClasspath is empty!");
    }
    this.renderClassLoader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]), getClass().getClassLoader());
    return this.renderClassLoader;
  }

}
