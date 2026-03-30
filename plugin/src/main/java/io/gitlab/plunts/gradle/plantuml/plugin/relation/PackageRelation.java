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
package io.gitlab.plunts.gradle.plantuml.plugin.relation;

import java.io.Serializable;
import java.util.Optional;
import java.util.regex.Pattern;

import io.github.classgraph.PackageInfo;
import io.gitlab.plunts.gradle.plantuml.plugin.RegExUtil;

/**
 * Configurable package relation.
 */
public class PackageRelation implements Serializable {
  private static final long serialVersionUID = 1L;

  private boolean alreadyWritten = false; // to prevent that packageRelation is written multiple times
  
  private Pattern from;
  private Pattern to;

  private String arrow;
  private String sourceArrow;
  private String lineCharacter;
  private String style;
  private String targetArrow;
  private String label;

  public PackageRelation() {
  }

  public PackageRelation from(String glob) {
    this.from = RegExUtil.convertGlobToRegex(glob);
    return this;
  }

  public PackageRelation to(String glob) {
    this.to = RegExUtil.convertGlobToRegex(glob);
    return this;
  }

  public PackageRelation with(String arrow) {
    this.arrow = arrow;
    return this;
  }

  public PackageRelation withSourceArrow(String sourceArrow) {
    this.sourceArrow = sourceArrow;
    return this;
  }

  public PackageRelation withLineCharacter(String lineCharacter) {
    this.lineCharacter = lineCharacter;
    return this;
  }

  public PackageRelation withHiddenStyle() {
    return withStyle("[hidden]");
  }
  
  public PackageRelation withStyle(String style) {
    this.style = style;
    return this;
  }

  public PackageRelation withTargetArrow(String targetArrow) {
    this.targetArrow = targetArrow;
    return this;
  }

  public PackageRelation withLabel(String label) {
    this.label = label;
    return this;
  }

  public boolean matches(PackageInfo from, PackageInfo to) {
    return isFrom(from) && isTo(to);
  }

  public boolean isFrom(PackageInfo from) {
    return this.from.matcher(from.getName()).matches();
  }

  public boolean isTo(PackageInfo to) {
    return this.to.matcher(to.getName()).matches();
  }

  public void writeArrow(StringBuilder target) {
    if (arrow != null) {
      target.append(arrow);
    } else {
      final String lineChar = firstNonNull(lineCharacter).orElse("-");

      firstNonNull(sourceArrow).ifPresent(target::append);
      target.append(lineChar);
      firstNonNull(style).ifPresent(target::append);
      target.append(lineChar);
      firstNonNull(targetArrow).ifPresent(target::append);
    }
  }

  public void writeLabel(StringBuilder target) {
    firstNonNull(label).ifPresent(s -> target.append(" : ").append(s));
  }

  private static Optional<String> firstNonNull(String... strings) {
    for (String str : strings) {
      if (str != null) {
        return Optional.of(str);
      }
    }
    return Optional.empty();
  }
  
  public Pattern getFrom() {
    return from;
  }
  
  public Pattern getTo() {
    return to;
  }
  
  public boolean isAlreadyWritten() {
      return alreadyWritten;
  }
  
  public void setAlreadyWritten(boolean alreadyWritten) {
    this.alreadyWritten = alreadyWritten;
  }

  @Override
  public String toString() {
    return "PackageRelation [from=" + from + ", to=" + to + "]";
  }
  
}
