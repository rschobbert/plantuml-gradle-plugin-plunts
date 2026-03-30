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
package io.gitlab.plunts.gradle.plantuml.plugin;

import io.gitlab.plunts.gradle.plantuml.plugin.output.AbstractDiagramWriter;
import io.gitlab.plunts.gradle.plantuml.plugin.output.DefaultDiagramWriter;
import io.gitlab.plunts.gradle.plantuml.plugin.output.InsertingDiagramWriter;
import io.gitlab.plunts.gradle.plantuml.plugin.output.RenderingDiagramWriter;
import io.gitlab.plunts.gradle.plantuml.plugin.relation.PackageRelation;
import io.gitlab.plunts.gradle.plantuml.plugin.relation.RelationOverride;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.gradle.api.file.RegularFile;

/**
 * Gradle DSL to configure a class diagram for the project.
 */
@Getter
public class ClassDiagram extends ClassDiagramDefaults implements Diagram {

  private String name;
  private final List<AbstractDiagramWriter> writers = new ArrayList<>();
  private final List<RelationOverride> additionalRelations = new ArrayList<>();
  private final List<PackageRelation> packageRelations = new ArrayList<>();
  private final List<RelationOverride> removedRelations = new ArrayList<>();

  ClassDiagram(ClassDiagramDefaults defaults) {
    super(defaults);
  }

  public void name(String name) {
    this.name = name;
  }

  public void writeTo(File outputFile) {
    this.writers.add(new DefaultDiagramWriter(outputFile));
  }

  public void writeTo(RegularFile outputFile) {
    this.writeTo(outputFile.getAsFile());
  }

  public void insertInto(File outputFile) {
    this.writers.add(new InsertingDiagramWriter(outputFile));
  }

  public void insertInto(RegularFile outputFile) {
    this.insertInto(outputFile.getAsFile());
  }

  public void renderTo(File outputFile) {
    this.writers.add(new RenderingDiagramWriter(outputFile));
  }

  public void renderTo(RegularFile outputFile) {
    this.renderTo(outputFile.getAsFile());
  }

  public void add(RelationOverride relation) {
    this.additionalRelations.add(relation);
  }

  public void add(PackageRelation relation) {
    this.packageRelations.add(relation);
  }

  public void remove(RelationOverride relation) {
    this.removedRelations.add(relation);
  }

  @Override
  public String toString() {
    return "ClassDiagram[" + name + "]";
  }
}
