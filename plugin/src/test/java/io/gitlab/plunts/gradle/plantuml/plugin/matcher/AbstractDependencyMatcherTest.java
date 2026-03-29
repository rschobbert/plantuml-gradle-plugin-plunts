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
package io.gitlab.plunts.gradle.plantuml.plugin.matcher;

import io.github.classgraph.ClassInfo;
import java.io.File;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AbstractDependencyMatcherTest {

  @Test
  void testInsideOfProject() {
    File dummyFile = new File("foobar");
    AbstractClasspathMatcher.PROJECT_CLASSPATH_HOLDER.set(Set.of(dummyFile));

    try {
      ClassInfo dummyClass = mock(ClassInfo.class);
      when(dummyClass.getClasspathElementFile()).thenReturn(dummyFile);

      AbstractDependencyMatcher instance = new SuperclassMatcher().insideOfProject();
      assertTrue(instance.test(mock(ClassInfo.class), dummyClass));
    } finally {
      AbstractClasspathMatcher.PROJECT_CLASSPATH_HOLDER.remove();
    }
  }

  @Test
  void testOutsideOfProject() {
    File dummyFile = new File("foobar");
    AbstractClasspathMatcher.PROJECT_CLASSPATH_HOLDER.set(Set.of(dummyFile));

    try {
      ClassInfo dummyClass = mock(ClassInfo.class);
      when(dummyClass.getClasspathElementFile()).thenReturn(new File("outsideFile"));

      AbstractDependencyMatcher instance = new SuperclassMatcher().outsideOfProject();
      assertTrue(instance.test(mock(ClassInfo.class), dummyClass));
    } finally {
      AbstractClasspathMatcher.PROJECT_CLASSPATH_HOLDER.remove();
    }
  }

}
