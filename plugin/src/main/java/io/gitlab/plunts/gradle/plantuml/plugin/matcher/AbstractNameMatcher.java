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

import io.gitlab.plunts.gradle.plantuml.plugin.RegExUtil;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import org.gradle.api.InvalidUserDataException;

/**
 * Abstract base class for matchers that test elements with names.
 */
public abstract class AbstractNameMatcher extends AbstractClasspathMatcher {

  @Getter(AccessLevel.PROTECTED)
  private String glob;
  private Pattern regex;

  public AbstractNameMatcher withName(String name) {
    if (name.contains("*")) {
      throw new InvalidUserDataException("use withNameLike for globs");
    }
    return withNameLike(name);
  }

  public AbstractNameMatcher withNameLike(String glob) {
    this.glob = glob;
    this.regex = glob == null ? null : RegExUtil.convertGlobToRegex(glob);
    return this;
  }

  protected boolean nameMatches(String name) {
    if (regex == null) {
      return true;
    }
    return regex.matcher(name).matches();
  }

}
