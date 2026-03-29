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
import io.github.classgraph.PackageInfo;
import org.gradle.api.InvalidUserDataException;

/**
 * Matcher to test PackageInfos.
 */
public class PackageMatcher extends AbstractNameMatcher implements BaselineMatcher {

  private int maxDepth = Integer.MAX_VALUE;

  public PackageMatcher nonRecursive() {
    maxDepth = 0;
    return this;
  }

  public PackageMatcher recursive() {
    maxDepth = Integer.MAX_VALUE;
    return this;
  }

  public PackageMatcher maxDepth(int maxDepth) {
    this.maxDepth = maxDepth;
    return this;
  }

  @Override
  public boolean test(ClassInfo classInfo) {
    if (!classpathMatches(classInfo)) {
      return false;
    }
    PackageInfo packageInfo = classInfo.getPackageInfo();
    int depthLeft = maxDepth;
    while (packageInfo != null) {
      if (nameMatches(packageInfo.getName())) {
        return true;
      } else if (depthLeft <= 0) {
        break;
      } else {
        packageInfo = packageInfo.getParent();
        --depthLeft;
      }
    }
    return false;
  }

  @Override
  public boolean nameMatches(String name) {
    return super.nameMatches(name);
  }

  @Override
  public PackageMatcher withName(String name) {
    super.withName(name);
    return this;
  }

  @Override
  public PackageMatcher withNameLike(String glob) {
    super.withNameLike(glob);
    return this;
  }

  @Override
  public PackageMatcher outsideOfProject() {
    super.outsideOfProject();
    return this;
  }

  @Override
  public PackageMatcher insideOfProject() {
    super.insideOfProject();
    return this;
  }

  @Override
  public void configureAccept(ClassGraph classGraph) {
    final String glob = getGlob();
    if (glob != null) {
      if (maxDepth == 0) {
        classGraph.acceptPackagesNonRecursive(glob);
      } else {
        classGraph.acceptPackages(glob);
      }
    }
  }

  @Override
  public void configureReject(ClassGraph classGraph) {
    final String glob = getGlob();
    if (glob != null) {
      if (maxDepth == 0) {
        // maybe we could just ignore this and handle it ourself?
        throw new InvalidUserDataException("can not exclude packages non-recursivly");
      } else {
        classGraph.rejectPackages(glob);
      }
    }
  }

}
