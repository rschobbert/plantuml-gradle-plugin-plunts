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
import java.io.Serializable;
import java.util.function.Predicate;
import java.util.function.BiPredicate;

/**
 * Abstract base class for matchers that test dependencies of classes.
 */
public abstract class AbstractDependencyMatcher implements BiPredicate<ClassInfo, ClassInfo>, Serializable, ClassGraphConfiguration {

  @SuppressWarnings("java:S1948") // actual values are Serializable
  private Predicate<ClassInfo> classPredicate;
  @SuppressWarnings("java:S1948") // actual values are Serializable
  private Predicate<ClassInfo> dependencyPredicate;

  public AbstractDependencyMatcher of(ClassMatcher classMatcher) {
    this.classPredicate = classMatcher;
    return this;
  }

  public AbstractDependencyMatcher of(PackageMatcher packageMatcher) {
    this.classPredicate = packageMatcher;
    return this;
  }

  public AbstractDependencyMatcher of(String glob) {
    return of(new ClassMatcher().withNameLike(glob));
  }

  public AbstractDependencyMatcher matching(Predicate<ClassInfo> classMatcher) {
    this.dependencyPredicate = classMatcher;
    return this;
  }

  public AbstractDependencyMatcher insideOfProject() {
    return matching(new ClassMatcher().insideOfProject());
  }

  public AbstractDependencyMatcher outsideOfProject() {
    return matching(new ClassMatcher().outsideOfProject());
  }

  public AbstractDependencyMatcher inPackage(String glob) {
    return matching(new PackageMatcher().withNameLike(glob));
  }

  @Override
  public boolean test(ClassInfo classInfo, ClassInfo dependencyInfo) {
    if (classInfo == null || dependencyInfo == null) {
      return false;
    }
    if (classPredicate != null) {
      return classPredicate.test(classInfo);
    }
    if (dependencyPredicate != null) {
      return dependencyPredicate.test(dependencyInfo);
    }
    return true;
  }

  @Override
  public void configureAccept(ClassGraph classGraph) {
    if (dependencyPredicate instanceof ClassGraphConfiguration dependencyConfiguration) {
      dependencyConfiguration.configureAccept(classGraph);
    }
  }

  @Override
  public void configureReject(ClassGraph classGraph) {
  }

}
