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
import java.io.Serializable;
import java.util.Set;
import org.gradle.api.InvalidUserDataException;

/**
 * Abstract base class for matchers that test classpath elements.
 */
public abstract class AbstractClasspathMatcher implements Serializable {

  protected static final ThreadLocal<Set<File>> PROJECT_CLASSPATH_HOLDER = new ThreadLocal<>();
  private boolean mustBeInside = false;
  private boolean mustBeOutside = false;

  public static void setProjectClasspath(Set<File> projectClasspath) {
    PROJECT_CLASSPATH_HOLDER.set(projectClasspath);
  }

  public AbstractClasspathMatcher insideOfProject() {
    if (mustBeOutside) {
      throw new InvalidUserDataException("already called outsideOfProject");
    }
    mustBeInside = true;
    return this;
  }

  public AbstractClasspathMatcher outsideOfProject() {
    if (mustBeInside) {
      throw new InvalidUserDataException("already called insideOfProject");
    }
    mustBeOutside = true;
    return this;
  }

  protected boolean classpathMatches(ClassInfo classInfo) {
    try {
      final File classpathElement = classInfo.getClasspathElementFile();
      if (mustBeInside) {
        return classpathElement != null && PROJECT_CLASSPATH_HOLDER.get().contains(classpathElement);
      } else if (mustBeOutside) {
        return classpathElement == null || !PROJECT_CLASSPATH_HOLDER.get().contains(classpathElement);
      } else {
        return true;
      }
    } catch (IllegalArgumentException ex) {
      // thrown for jdk classes
      return !mustBeInside;
    }
  }

}
