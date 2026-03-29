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
import io.gitlab.plunts.gradle.plantuml.plugin.PackageDiagramDefaults.ElementNote;
import io.gitlab.plunts.gradle.plantuml.plugin.PackageDiagramDefaults.Style;
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.BaselineMatcher;
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.BiIncludeExclude;
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.IncludeExclude;
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.InterfaceMatcher;
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.PackageMatcher;
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.ReferencedClassMatcher;
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.SubclassMatcher;
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.SuperclassMatcher;
import io.gitlab.plunts.gradle.plantuml.plugin.output.AbstractDiagramWriter;
import java.util.List;

/**
 * Common interface for {@link ClassDiagram} and {@link PackageDiagram}.
 */
public interface Diagram {

  String getName();

  Style getStyle();

  List<ElementNote<PackageMatcher>> getPackageNotes();

  IncludeExclude<ClassInfo, BaselineMatcher> getBaselineIncludeExclude();

  BiIncludeExclude<ClassInfo, ReferencedClassMatcher> getReferencesIncludeExclude();

  BiIncludeExclude<ClassInfo, SuperclassMatcher> getSuperclassIncludeExclude();

  BiIncludeExclude<ClassInfo, SubclassMatcher> getSubclassIncludeExclude();

  BiIncludeExclude<ClassInfo, InterfaceMatcher> getInterfaceIncludeExclude();

  List<AbstractDiagramWriter> getWriters();

}
