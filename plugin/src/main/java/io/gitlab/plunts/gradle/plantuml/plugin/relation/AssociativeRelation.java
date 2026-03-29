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

import java.util.Objects;
import lombok.Setter;
import org.gradle.internal.Pair;

/**
 * Associative relation between two classes.
 */
public class AssociativeRelation implements AbstractClassRelation {

  public static final String DEFAULT_BACK_ARROW = "<";
  public static final String AGGREGATION_ARROW = "o";
  public static final String COMPOSITION_ARROW = "*";
  public static final String INFINIT_MULTIPLICITY = "\"*\"";
  private Pair<Integer, Integer> sourceMultiplicity = null;
  @Setter
  private String sourceArrow = null;
  @Setter
  private String targetArrow = ">";
  private Pair<Integer, Integer> targetMultiplicity = null;

  public void activateBackReference() {
    if (sourceArrow == null) {
      sourceArrow = DEFAULT_BACK_ARROW;
    }
  }

  public void setMultiplicityMin(Viewpoint viewpoint, int multiplicityMin) {
    switch (viewpoint) {
      case SOURCE:
        setSourceMultiplicityMin(multiplicityMin);
        break;
      case TARGET:
        setTargetMultiplicityMin(multiplicityMin);
        break;
      default:
        throw new AssertionError(viewpoint.name());
    }
  }

  public void setMultiplicityMax(Viewpoint viewpoint, int multiplicityMax) {
    switch (viewpoint) {
      case SOURCE:
        setSourceMultiplicityMax(multiplicityMax);
        break;
      case TARGET:
        setTargetMultiplicityMax(multiplicityMax);
        break;
      default:
        throw new AssertionError(viewpoint.name());
    }
  }

  public void setArrow(Viewpoint viewpoint, String arrow) {
    switch (viewpoint) {
      case SOURCE:
        setSourceArrow(arrow);
        break;
      case TARGET:
        setTargetArrow(arrow);
        break;
      default:
        throw new AssertionError(viewpoint.name());
    }
  }

  @Override
  public String getSourceMultiplicity() {
    return stringify(sourceMultiplicity);
  }

  public void setSourceMultiplicityMin(int sourceMultiplicityMin) {
    if (sourceMultiplicity == null) {
      sourceMultiplicity = Pair.of(sourceMultiplicityMin, Integer.MAX_VALUE);
    } else if (sourceMultiplicityMin > sourceMultiplicity.left) {
      sourceMultiplicity = Pair.of(sourceMultiplicityMin, sourceMultiplicity.right);
    }
  }

  public void setSourceMultiplicityMax(int sourceMultiplicityMax) {
    if (sourceMultiplicity == null) {
      sourceMultiplicity = Pair.of(0, sourceMultiplicityMax);
    } else if (sourceMultiplicityMax < sourceMultiplicity.right) {
      sourceMultiplicity = Pair.of(sourceMultiplicity.left, sourceMultiplicityMax);
    }
  }

  @Override
  public String getSourceArrow() {
    return sourceArrow;
  }

  @Override
  public String getLineCharacter() {
    return "-";
  }

  @Override
  public String getStyle() {
    return null;
  }

  @Override
  public String getTargetArrow() {
    return targetArrow;
  }

  @Override
  public String getTargetMultiplicity() {
    return stringify(targetMultiplicity);
  }

  public void setTargetMultiplicityMin(int targetMultiplicityMin) {
    if (targetMultiplicity == null) {
      targetMultiplicity = Pair.of(targetMultiplicityMin, Integer.MAX_VALUE);
    } else if (targetMultiplicityMin > targetMultiplicity.left) {
      targetMultiplicity = Pair.of(targetMultiplicityMin, targetMultiplicity.right);
    }
  }

  public void setTargetMultiplicityMax(int targetMultiplicityMax) {
    if (targetMultiplicity == null) {
      targetMultiplicity = Pair.of(0, targetMultiplicityMax);
    } else if (targetMultiplicityMax < targetMultiplicity.right) {
      targetMultiplicity = Pair.of(targetMultiplicity.left, targetMultiplicityMax);
    }
  }

  @Override
  public String getLabel() {
    return null;
  }

  private static String stringify(Pair<Integer, Integer> multiplicity) {
    if (multiplicity == null) {
      return null;
    }

    assert multiplicity.left() != null;
    assert multiplicity.right() != null;
    if (Objects.equals(multiplicity.left(), multiplicity.right())) {
      return multiplicity.left().toString();
    } else if (multiplicity.right() == Integer.MAX_VALUE) {
      return multiplicity.left() + "..*";
    } else {
      return multiplicity.left() + ".." + multiplicity.right();
    }
  }

  public enum Viewpoint {
    SOURCE, TARGET
  }

}
