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

import io.gitlab.plunts.gradle.plantuml.plugin.RegExUtil;
import io.github.classgraph.ClassInfo;
import java.io.Serializable;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Configurable override for relations.
 */
@RequiredArgsConstructor
public class RelationOverride implements Serializable {

  public static final RelationOverride EMPTY = new RelationOverride(AbstractClassRelation.class);

  private final Class<? extends AbstractClassRelation> relationType;
  @Getter
  private Pattern from;
  @Getter
  private Pattern to;

  private String arrow;
  private String sourceMultiplicity;
  private String sourceArrow;
  private String lineCharacter;
  private String style;
  private String targetArrow;
  private String targetMultiplicity;
  private String label;

  public RelationOverride from(String glob) {
    this.from = RegExUtil.convertGlobToRegex(glob);
    return this;
  }

  public RelationOverride to(String glob) {
    this.to = RegExUtil.convertGlobToRegex(glob);
    return this;
  }

  public RelationOverride with(String arrow) {
    this.arrow = arrow;
    return this;
  }

  public RelationOverride withSourceMultiplicity(String sourceMultiplicity) {
    this.sourceMultiplicity = sourceMultiplicity;
    return this;
  }

  public RelationOverride withSourceArrow(String sourceArrow) {
    this.sourceArrow = sourceArrow;
    return this;
  }

  public RelationOverride withLineCharacter(String lineCharacter) {
    this.lineCharacter = lineCharacter;
    return this;
  }

  public RelationOverride withStyle(String style) {
    this.style = style;
    return this;
  }

  public RelationOverride withTargetArrow(String targetArrow) {
    this.targetArrow = targetArrow;
    return this;
  }

  public RelationOverride withTargetMultiplicity(String targetMultiplicity) {
    this.targetMultiplicity = targetMultiplicity;
    return this;
  }

  public RelationOverride withLabel(String label) {
    this.label = label;
    return this;
  }

  public boolean matches(Class<? extends AbstractClassRelation> relationType, ClassInfo from, ClassInfo to) {
    return this.relationType.isAssignableFrom(relationType) && isFrom(from) && isTo(to);
  }

  public boolean isFrom(ClassInfo from) {
    return this.from.matcher(from.getName()).matches();
  }

  public boolean isTo(ClassInfo to) {
    return this.to.matcher(to.getName()).matches();
  }

  public void writeArrow(AbstractClassRelation relation, StringBuilder target) {
    if (arrow != null) {
      target.append(arrow);
    } else {
      final String lineChar = firstNonNull(lineCharacter, relation.getLineCharacter()).orElse("-");

      firstNonNull(sourceMultiplicity, relation.getSourceMultiplicity())
              .ifPresent(s -> target.append('"').append(s).append('"').append(" "));
      firstNonNull(sourceArrow, relation.getSourceArrow()).ifPresent(target::append);
      target.append(lineChar);
      firstNonNull(style, relation.getStyle()).ifPresent(target::append);
      target.append(lineChar);
      firstNonNull(targetArrow, relation.getTargetArrow()).ifPresent(target::append);
      firstNonNull(targetMultiplicity, relation.getTargetMultiplicity())
              .ifPresent(s -> target.append(" ").append('"').append(s).append('"'));
    }
  }

  public void writeLabel(AbstractClassRelation relation, StringBuilder target) {
    firstNonNull(label, relation.getLabel())
            .ifPresent(s -> target.append(" : ").append(s));
  }

  private static Optional<String> firstNonNull(String... strings) {
    for (String str : strings) {
      if (str != null) {
        return Optional.of(str);
      }
    }
    return Optional.empty();
  }

}
