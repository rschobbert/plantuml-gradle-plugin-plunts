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

import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

class PlantUmlPluginTest {

  @Test
  @SuppressWarnings("unchecked")
  void testApply() {
    Project root = ProjectBuilder.builder().withName("root").build();
    final ClassDiagramsExtension rootExtension = root.getExtensions().create("classDiagrams", ClassDiagramsExtension.class);
    rootExtension.getPlantumlServer().set("Foobar");
    rootExtension.getRenderClasspath().set(mock(FileCollection.class));
    
    Project intermediate = ProjectBuilder.builder().withName("intermediate").withParent(root).build();
    Project project = ProjectBuilder.builder().withName("leaf").withParent(intermediate).build();

    new PlantUmlPlugin().apply(project);
    assertThat(project.getTasks().getByName("generateClassDiagrams"), is(instanceOf(GenerateClassDiagramsTask.class)));
    ClassDiagramsExtension extension = project.getExtensions().getByType(ClassDiagramsExtension.class);
    assertThat(extension.getPlantumlServer().get(), is("Foobar"));
    assertThat(extension.getRenderClasspath().get(), is(rootExtension.getRenderClasspath().get()));
  }

}
