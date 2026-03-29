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

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import org.gradle.api.InvalidUserDataException;

/**
 * Matcher to test ClassInfos.
 */
public class ClassMatcher extends AbstractAnnotationMatcher implements BaselineMatcher {

  private boolean needsParentClass = false;
  private boolean rejectParentClass = false;

  public ClassMatcher withParentClass() {
    if (rejectParentClass) {
      throw new InvalidUserDataException("already called withoutParentClass");
    }
    needsParentClass = true;
    return this;
  }

  public ClassMatcher withoutParentClass() {
    if (needsParentClass) {
      throw new InvalidUserDataException("already called withParentClass");
    }
    rejectParentClass = true;
    return this;
  }

  @Override
  public boolean test(ClassInfo classInfo) {
    final ClassInfoList outerClasses = classInfo.getOuterClasses();
    if (needsParentClass && outerClasses.isEmpty()) {
      return false;
    }
    if (rejectParentClass && !outerClasses.isEmpty()) {
      return false;
    }
    return classpathMatches(classInfo)
            && nameMatches(classInfo.getName())
            && annotationsMatch(classInfo.getAnnotationInfo())
            && modifiersMatch(classInfo.getModifiers());
  }

  @Override
  public ClassMatcher withName(String name) {
    super.withName(name);
    return this;
  }

  @Override
  public ClassMatcher withNameLike(String glob) {
    super.withNameLike(glob);
    return this;
  }

  @Override
  public ClassMatcher outsideOfProject() {
    super.outsideOfProject();
    return this;
  }

  @Override
  public ClassMatcher insideOfProject() {
    super.insideOfProject();
    return this;
  }

  @Override
  public void configureAccept(ClassGraph classGraph) {
    final String glob = getGlob();
    if (glob != null) {
      classGraph.acceptClasses(glob);
    }
  }

  @Override
  public void configureReject(ClassGraph classGraph) {
    final String glob = getGlob();
    if (glob != null) {
      classGraph.rejectClasses(glob);
    }
  }

}
