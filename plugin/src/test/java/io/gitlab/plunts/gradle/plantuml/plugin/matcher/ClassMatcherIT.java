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

import org.gradle.api.GradleException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The class matcher has some exception cases we have to test seperatly.
 */
class ClassMatcherIT {

  @Test
  void testWithNameException() {
    ClassMatcher matcher = new ClassMatcher();
    assertThrows(GradleException.class, () -> matcher.withName("x.*"));
  }

  @Test
  void testWithParentClassException() {
    ClassMatcher matcher = new ClassMatcher().withoutParentClass();
    assertThrows(GradleException.class, matcher::withParentClass);
  }

  @Test
  void testWithoutParentClassException() {
    ClassMatcher matcher = new ClassMatcher().withParentClass();
    assertThrows(GradleException.class, matcher::withoutParentClass);
  }

  @Test
  void testInsideOfProjectException() {
    ClassMatcher matcher = (ClassMatcher) new ClassMatcher().outsideOfProject();
    assertThrows(GradleException.class, matcher::insideOfProject);
  }

  @Test
  void testOutsideOfProjectException() {
    ClassMatcher matcher = (ClassMatcher) new ClassMatcher().insideOfProject();
    assertThrows(GradleException.class, matcher::outsideOfProject);
  }

}
