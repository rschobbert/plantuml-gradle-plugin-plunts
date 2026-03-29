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
import io.github.classgraph.ClassMemberInfo;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;

/**
 * Abstract base class for matchers that test class members.
 *
 * @param <T> the member type
 */
public abstract class AbstractClassMemberMatcher<T extends ClassMemberInfo> extends AbstractAnnotationMatcher implements Predicate<T> {

  @SuppressWarnings("java:S1948") // actual values are Serializable
  private Predicate<ClassInfo> classPredicate;

  public AbstractClassMemberMatcher<T> of(ClassMatcher classMatcher) {
    this.classPredicate = classMatcher;
    return this;
  }

  public AbstractClassMemberMatcher<T> of(PackageMatcher packageMatcher) {
    this.classPredicate = packageMatcher;
    return this;
  }

  public AbstractClassMemberMatcher<T> of(String glob) {
    return of(new ClassMatcher().withNameLike(glob));
  }

  @Override
  public boolean test(T memberInfo) {
    if (classPredicate != null && !classPredicate.test(memberInfo.getClassInfo())) {
      return false;
    }
    return classpathMatches(memberInfo.getClassInfo())
            && nameMatches(memberInfo.getName())
            && annotationsMatch(memberInfo.getAnnotationInfo())
            && modifiersMatch(memberInfo.getModifiers());
  }

}
