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

/**
 * Relation where one class extends the other.
 */
public class ExtensionRelation implements AbstractClassRelation {

  private final String lineCharacter;

  public ExtensionRelation(boolean isInterface) {
    this.lineCharacter = isInterface ? "." : "-";
  }

  @Override
  public String getSourceMultiplicity() {
    return null;
  }

  @Override
  public String getSourceArrow() {
    return null;
  }

  @Override
  public String getLineCharacter() {
    return this.lineCharacter;
  }

  @Override
  public String getStyle() {
    return "u";
  }

  @Override
  public String getTargetArrow() {
    return "|>";
  }

  @Override
  public String getTargetMultiplicity() {
    return null;
  }

  @Override
  public String getLabel() {
    return null;
  }

}
