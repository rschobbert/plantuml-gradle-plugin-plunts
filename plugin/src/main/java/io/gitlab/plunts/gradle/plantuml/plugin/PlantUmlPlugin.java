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

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

/**
 * Gradle plugin that configures the {@link GenerateClassDiagramsTask}.
 */
public class PlantUmlPlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    ClassDiagramsExtension extension = project.getExtensions().create("classDiagrams", ClassDiagramsExtension.class);
    Project parent = project.getParent();
    while (parent != null) {
      final ClassDiagramsExtension parentExt = parent.getExtensions().findByType(ClassDiagramsExtension.class);
      if (parentExt != null) {
        project.getLogger().info("Inheriting class diagram configuration from " + project.getParent().getPath());
        extension.inheritFrom(parentExt);
        break;
      }
      parent = parent.getParent();
    }

    TaskProvider<GenerateClassDiagramsTask> task = project.getTasks().register("generateClassDiagrams", GenerateClassDiagramsTask.class, extension);
    project.afterEvaluate(t -> task.get().addProjectClasspath(project));
    project.subprojects(subProject -> subProject.afterEvaluate(task.get()::addProjectClasspath));
  }

}
