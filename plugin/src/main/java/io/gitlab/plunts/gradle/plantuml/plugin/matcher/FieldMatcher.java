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
import io.github.classgraph.FieldInfo;
import lombok.RequiredArgsConstructor;

/**
 * Matcher to test FieldInfos.
 */
@RequiredArgsConstructor
public class FieldMatcher extends AbstractClassMemberMatcher<FieldInfo> {

  private final boolean enumValues;
  private boolean needsAccessor = false;
  private boolean rejectAccessor = false;

  public FieldMatcher thatHaveAccessors() {
    needsAccessor = true;
    return this;
  }

  public FieldMatcher thatDontHaveAccessors() {
    rejectAccessor = true;
    return this;
  }

  @Override
  public boolean test(FieldInfo fieldInfo) {
    if (fieldInfo.isEnum() != enumValues) {
      return false;
    }

    boolean hasAccessors = hasAccessors(fieldInfo);
    if (needsAccessor && !hasAccessors) {
      return false;
    }
    if (rejectAccessor && hasAccessors) {
      return false;
    }
    return super.test(fieldInfo);
  }

  private boolean hasAccessors(FieldInfo fieldInfo) {
    String capitalizedName = capitalizeFieldName(fieldInfo);
    ClassInfo classInfo = fieldInfo.getClassInfo();
    return classInfo.hasMethod("get" + capitalizedName)
            || classInfo.hasMethod("set" + capitalizedName)
            || classInfo.hasMethod("is" + capitalizedName);
  }

  private String capitalizeFieldName(FieldInfo fieldInfo) {
    String fieldName = fieldInfo.getName();
    return Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
  }

}
