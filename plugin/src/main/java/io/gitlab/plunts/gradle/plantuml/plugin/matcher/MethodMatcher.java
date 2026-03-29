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
import io.github.classgraph.MethodInfo;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * Matcher to test MethodInfos.
 */
public class MethodMatcher extends AbstractClassMemberMatcher<MethodInfo> {

  private static final Set<String> BUILTIN_METHODS = new HashSet<>(asList("toString", "equals", "hashCode", "clone", "finalize"));
  private static final Set<String> BUILTIN_ENUM_METHODS = new HashSet<>(asList("values", "valueOf"));
  private boolean needsAccessor = false;
  private boolean rejectAccessor = false;
  private boolean ignoreBuiltin = false;

  public MethodMatcher thatAreAccessors() {
    needsAccessor = true;
    return this;
  }

  public MethodMatcher thatAreNotAccessors() {
    rejectAccessor = true;
    return this;
  }

  public MethodMatcher andNotBuiltin() {
    this.ignoreBuiltin = true;
    return this;
  }

  @Override
  public boolean test(MethodInfo methodInfo) {
    boolean hasFieldWithName = hasFieldWithName(methodInfo);
    if (needsAccessor && !hasFieldWithName) {
      return false;
    }
    if (rejectAccessor && hasFieldWithName) {
      return false;
    }
    if (ignoreBuiltin && isBuiltin(methodInfo)) {
      return false;
    }
    return super.test(methodInfo);
  }

  private boolean hasFieldWithName(MethodInfo methodInfo) {
    ClassInfo classInfo = methodInfo.getClassInfo();
    String propertyName = guessPropertyName(methodInfo);
    if (propertyName == null) {
      return false;
    } else {
      return classInfo.hasField(propertyName);
    }
  }

  private String guessPropertyName(MethodInfo methodInfo) {
    String methodName = methodInfo.getName();
    String propertyName;
    if ((methodName.startsWith("get") || methodName.startsWith("set")) && methodName.length() > 3) {
      propertyName = methodName.substring(3);
    } else if (methodName.startsWith("is") && methodName.length() > 2) {
      propertyName = methodName.substring(2);
    } else {
      return null;
    }
    return Character.toLowerCase(propertyName.charAt(0)) + propertyName.substring(1);
  }

  private boolean isBuiltin(MethodInfo methodInfo) {
    final String methodName = methodInfo.getName();
    if (methodInfo.getClassInfo().isEnum() && BUILTIN_ENUM_METHODS.contains(methodName)) {
      return true;
    }
    return BUILTIN_METHODS.contains(methodName);
  }

}
