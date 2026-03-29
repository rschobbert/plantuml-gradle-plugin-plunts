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

import io.github.classgraph.ClassInfo;
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.InterfaceMatcher;
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.ReferencedClassMatcher;
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.SubclassMatcher;
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.SuperclassMatcher;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClassDiagramsExtensionTest {

  private static final String FAKE_CLASS_NAME = "foo.Bar";

  @Test
  void testDefaults() {
    ClassDiagramsExtension ext = new ClassDiagramsExtension(ProjectBuilder.builder().build().getObjects());
    ext.defaults(d -> {
      d.style(s -> {
        s.define("Test");
      });
    });
    assertThat(ext.getDefaults().getStyle().getDefines(), contains("Test"));
  }

  @Test
  void testDiagram() {
    ClassDiagramsExtension ext = new ClassDiagramsExtension(ProjectBuilder.builder().build().getObjects());
    ext.diagram(d -> {
      d.note("Test0").leftOf(d.classes());
      d.note("Test1").rightOf(d.classes());
      d.note("Test2").topOf(d.classes());
      d.note("Test3").bottomOf(d.classes());
      d.note("Test4").leftOf(d.packages());
      d.note("Test5").rightOf(d.packages());
      d.note("Test6").topOf(d.packages());
      d.note("Test7").bottomOf(d.packages());
      d.note("Test8").leftOf(d.fields());
      d.note("Test9").rightOf(d.fields());
      d.note("TestA").leftOf(d.methods());
      d.note("TestB").rightOf(d.methods());
    });

    final var diagram = ext.getDiagrams().get().get(0);
    assertThat(diagram.getClassNotes(), hasSize(4));
    assertThat(diagram.getPackageNotes(), hasSize(4));
    assertThat(diagram.getFieldNotes(), hasSize(2));
    assertThat(diagram.getMethodNotes(), hasSize(2));
  }

  @Test
  void testExcludeSuperclass() {
    var ext = new ClassDiagramDefaults(null);
    ext.exclude((SuperclassMatcher) ext.superclasses().of(FAKE_CLASS_NAME));

    ClassInfo classInfo = mock(ClassInfo.class);
    when(classInfo.getName()).thenReturn(FAKE_CLASS_NAME);
    assertTrue(ext.getSuperclassIncludeExclude().isExcluded(classInfo, mock(ClassInfo.class)));
  }

  @Test
  void testExcludeSubclass() {
    var ext = new ClassDiagramDefaults(null);
    ext.exclude((SubclassMatcher) ext.subclasses().of(FAKE_CLASS_NAME));

    ClassInfo classInfo = mock(ClassInfo.class);
    when(classInfo.getName()).thenReturn(FAKE_CLASS_NAME);
    assertTrue(ext.getSubclassIncludeExclude().isExcluded(classInfo, mock(ClassInfo.class)));
  }

  @Test
  void testExcludeInterface() {
    var ext = new ClassDiagramDefaults(null);
    ext.exclude((InterfaceMatcher) ext.interfaces().of(FAKE_CLASS_NAME));

    ClassInfo classInfo = mock(ClassInfo.class);
    when(classInfo.getName()).thenReturn(FAKE_CLASS_NAME);
    assertTrue(ext.getInterfaceIncludeExclude().isExcluded(classInfo, mock(ClassInfo.class)));
  }

  @Test
  void testExcludeReferencedClass() {
    var ext = new ClassDiagramDefaults(null);
    ext.exclude((ReferencedClassMatcher) ext.referencedClasses().of(FAKE_CLASS_NAME));

    ClassInfo classInfo = mock(ClassInfo.class);
    when(classInfo.getName()).thenReturn(FAKE_CLASS_NAME);
    assertTrue(ext.getReferencesIncludeExclude().isExcluded(classInfo, mock(ClassInfo.class)));
  }

}
