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

import groovy.lang.Closure;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.gradle.api.Action;
import org.gradle.api.file.FileCollection;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Optional;

/**
 * Gradle DSL extension to configure class diagrams for the project.
 */
@Getter
@Setter
public class ClassDiagramsExtension {

  private final ClassDiagramDefaults defaults = new ClassDiagramDefaults(null);
  private final SetProperty<Object> sourceSets;
  private final ListProperty<ClassDiagram> diagrams;
  private final ListProperty<PackageDiagram> packageDiagrams;
  @Optional
  private final Property<String> plantumlServer;
  @Optional
  private final Property<FileCollection> renderClasspath;

  @Inject
  public ClassDiagramsExtension(ObjectFactory objectFactory) {
    this.sourceSets = objectFactory.setProperty(Object.class).empty();
    this.diagrams = objectFactory.listProperty(ClassDiagram.class);
    this.packageDiagrams = objectFactory.listProperty(PackageDiagram.class);
    this.plantumlServer = objectFactory.property(String.class).convention("https://www.plantuml.com/plantuml");
    this.renderClasspath = objectFactory.property(FileCollection.class);
  }

  void inheritFrom(ClassDiagramsExtension parentExt) {
    this.plantumlServer.set(parentExt.plantumlServer);
    this.renderClasspath.set(parentExt.renderClasspath);
    this.defaults.setDefaults(parentExt.defaults);
  }

  public void renderClasspath(FileCollection renderClasspath) {
    this.renderClasspath.set(renderClasspath);
  }

  public void defaults(Action<ClassDiagramDefaults> action) {
    action.execute(defaults);
  }

  public void diagram(Action<ClassDiagram> action) {
    classDiagram(action);
  }

  public void classDiagram(Action<ClassDiagram> action) {
    ClassDiagram diagram = new ClassDiagram(this.defaults);
    action.execute(diagram);
    diagrams.add(diagram);
  }

  public void packageDiagram(Action<PackageDiagram> action) {
    PackageDiagram diagram = new PackageDiagram(this.defaults);
    action.execute(diagram);
    packageDiagrams.add(diagram);
  }

  public void defaults(Closure<ClassDiagram> closure) {
    closure.setDelegate(defaults);
    closure.call(defaults);
  }

  public void diagram(Closure<ClassDiagram> closure) {
    classDiagram(closure);
  }

  public void classDiagram(Closure<ClassDiagram> closure) {
    ClassDiagram diagram = new ClassDiagram(this.defaults);
    closure.setDelegate(diagram);
    closure.call(diagram);
    diagrams.add(diagram);
  }

  public void packageDiagram(Closure<PackageDiagram> closure) {
    PackageDiagram diagram = new PackageDiagram(this.defaults);
    closure.setDelegate(diagram);
    closure.call(diagram);
    packageDiagrams.add(diagram);
  }

}
