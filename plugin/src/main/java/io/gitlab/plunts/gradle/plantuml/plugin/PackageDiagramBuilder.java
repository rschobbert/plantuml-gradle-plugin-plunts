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

import io.github.classgraph.ClassInfo;
import io.github.classgraph.PackageInfo;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static java.util.Collections.emptyMap;

/**
 * Parses ClassInfo objects and builds a PlantUML diagram showing only the package dependencies.
 */
class PackageDiagramBuilder extends DiagramBuilder<PackageDiagram> {

  PackageDiagramBuilder(PackageDiagram diagram) {
    super(diagram);
  }

  @Override
  public String build() {
    StringBuilder sb = new StringBuilder("@startuml ").append(diagram.getName()).append('\n');
    writeStyle(sb);
    sb.append("\n");
    writePackageDefinitions(sb);
    writePackageNotes(sb);
    writePackageRelations(sb);
    sb.append("@enduml\n");
    return sb.toString();
  }

  private void writePackageDefinitions(StringBuilder target) {
    Map<String, Set<PackageInfo>> packageInfos = new TreeMap<>();
    for (ClassInfo classInfo : classes) {
      final PackageInfo packageInfo = classInfo.getPackageInfo();
      final PackageInfo parent = packageInfo.getParent();
      if (parent != null) {
        packageInfos.computeIfAbsent(parent.getName(), k -> new TreeSet<>()).add(packageInfo);
      }
    }

    for (Map.Entry<String, Set<PackageInfo>> entry : packageInfos.entrySet()) {
      final String parentName = entry.getKey();
      target.append("package ").append(parentName).append(" {\n");
      for (PackageInfo packageInfo : entry.getValue()) {
        final String packageName = packageInfo.getName();
        target.append(INDENTATION).append("[").append(packageName.substring(parentName.length() + 1))
                .append("] as ").append(packageName).append('\n');
      }
      target.append("}\n");
    }
  }

  private void writePackageRelations(StringBuilder target) {
    Set<String> writtenRelations = new HashSet<>();
    for (ClassInfo classInfo : classes) {
      StringBuilder sb = new StringBuilder();
      associativeRelations.getOrDefault(classInfo, emptyMap()).keySet().forEach(associatedClass -> {
        writePackageRelation(classInfo, associatedClass, sb);
        if (sb.isEmpty()) {
          return;
        }

        final String relation = sb.toString().trim();
        if (!writtenRelations.contains(relation)) {
          target.append(sb);
          writtenRelations.add(relation);
        }
        sb.setLength(0);
      });
    }
  }

  private void writePackageRelation(ClassInfo from, ClassInfo to, StringBuilder target) {
    if (!classes.contains(from) || !classes.contains(to) || from.getPackageName().equals(to.getPackageName())) {
      return;
    }

    target.append(from.getPackageName()).append(" ");
    target.append("-->");
    target.append(" ").append(to.getPackageName());
    target.append("\n");
  }

}
