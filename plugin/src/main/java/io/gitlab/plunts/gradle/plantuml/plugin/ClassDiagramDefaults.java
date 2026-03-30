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

import groovy.lang.Closure;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.FieldInfo;
import io.github.classgraph.MethodInfo;
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.ClassMatcher;
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.FieldMatcher;
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.IncludeExclude;
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.MethodMatcher;
import io.gitlab.plunts.gradle.plantuml.plugin.relation.AssociativeRelation;
import io.gitlab.plunts.gradle.plantuml.plugin.relation.ExtensionRelation;
import io.gitlab.plunts.gradle.plantuml.plugin.relation.PackageRelation;
import io.gitlab.plunts.gradle.plantuml.plugin.relation.RelationOverride;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.gradle.api.Action;
import org.gradle.api.InvalidUserDataException;

/**
 * Gradle DSL to configure defaults for class diagrams of the project.
 */
public class ClassDiagramDefaults extends PackageDiagramDefaults {

  private ClassDiagramDefaults defaults;
  private final IncludeExclude<FieldInfo, FieldMatcher> fieldIncludeExclude = new IncludeExclude<>();
  private final IncludeExclude<MethodInfo, MethodMatcher> methodIncludeExclude = new IncludeExclude<>();
  private final Collection<RelationOverride> relationOverrides = new ArrayList<>();
  private final Collection<ElementNote<ClassMatcher>> classNotes = new ArrayList<>();
  private final Collection<ElementNote<FieldMatcher>> fieldNotes = new ArrayList<>();
  private final Collection<ElementNote<MethodMatcher>> methodNotes = new ArrayList<>();
  private final Collection<ClassGrouping> classGroupings = new ArrayList<>();

  ClassDiagramDefaults(ClassDiagramDefaults defaults) {
    super(defaults);
    this.defaults = defaults;
  }

  void setDefaults(ClassDiagramDefaults defaults) {
    super.setDefaults(defaults);
    this.defaults = defaults;
  }

  public void include(FieldMatcher matcher) {
    fieldIncludeExclude.addInclude(matcher);
  }

  public void include(MethodMatcher matcher) {
    methodIncludeExclude.addInclude(matcher);
  }

  public void exclude(FieldMatcher matcher) {
    fieldIncludeExclude.addExclude(matcher);
  }

  public void exclude(MethodMatcher matcher) {
    methodIncludeExclude.addExclude(matcher);
  }

  @Override
  public ElementNoteBuilder note(String note) {
    return new ElementNoteBuilder(note);
  }

  public ClassMatcher classes() {
    return new ClassMatcher();
  }

  public ClassMatcher classes(String name) {
    return new ClassMatcher().withNameLike(name);
  }

  public FieldMatcher fields() {
    return new FieldMatcher(false);
  }
  
  public FieldMatcher enumValues() {
    return new FieldMatcher(true);
  }

  public MethodMatcher methods() {
    return new MethodMatcher();
  }

  public void override(RelationOverride relationOverride) {
    if (relationOverride.getFrom() == null) {
      throw new InvalidUserDataException("from not specified");
    }
    if (relationOverride.getTo() == null) {
      throw new InvalidUserDataException("from not specified");
    }
    relationOverrides.add(relationOverride);
  }

  public RelationOverride association() {
    return new RelationOverride(AssociativeRelation.class);
  }

  public RelationOverride extension() {
    return new RelationOverride(ExtensionRelation.class);
  }

  public PackageRelation packageRelation() {
    return new PackageRelation();
  }

  public void together(Action<ClassGrouping> action) {
    ClassGrouping classGrouping = new ClassGrouping();
    action.execute(classGrouping);
    classGroupings.add(classGrouping);
  }

  public void together(Closure<ClassGrouping> closure) {
    ClassGrouping classGrouping = new ClassGrouping();
    closure.setDelegate(classGrouping);
    closure.setResolveStrategy(Closure.DELEGATE_ONLY);
    closure.call(classGrouping);
    classGroupings.add(classGrouping);
  }

  public IncludeExclude<FieldInfo, FieldMatcher> getFieldIncludeExclude() {
    IncludeExclude<FieldInfo, FieldMatcher> copy = new IncludeExclude<>(fieldIncludeExclude);
    if (this.inheritDefaults && this.defaults != null) {
      copy.addAll(this.defaults.getFieldIncludeExclude());
    } else {
      copy.addInclude((FieldMatcher) fields().thatArePublic());
      copy.addInclude(enumValues());
    }
    return copy;
  }

  public IncludeExclude<MethodInfo, MethodMatcher> getMethodIncludeExclude() {
    IncludeExclude<MethodInfo, MethodMatcher> copy = new IncludeExclude<>(methodIncludeExclude);
    if (this.inheritDefaults && this.defaults != null) {
      copy.addAll(this.defaults.getMethodIncludeExclude());
    } else {
      copy.addInclude(((MethodMatcher) methods().thatArePublic()).andNotBuiltin());
    }
    return copy;
  }

  public List<ElementNote<ClassMatcher>> getClassNotes() {
    List<ElementNote<ClassMatcher>> copy = new ArrayList<>();
    if (this.inheritDefaults && this.defaults != null) {
      copy.addAll(this.defaults.getClassNotes());
    }
    copy.addAll(this.classNotes);
    return copy;
  }

  public List<ElementNote<FieldMatcher>> getFieldNotes() {
    List<ElementNote<FieldMatcher>> copy = new ArrayList<>();
    if (this.inheritDefaults && this.defaults != null) {
      copy.addAll(this.defaults.getFieldNotes());
    }
    copy.addAll(this.fieldNotes);
    return copy;
  }

  public List<ElementNote<MethodMatcher>> getMethodNotes() {
    List<ElementNote<MethodMatcher>> copy = new ArrayList<>();
    if (this.inheritDefaults && this.defaults != null) {
      copy.addAll(this.defaults.getMethodNotes());
    }
    copy.addAll(this.methodNotes);
    return copy;
  }

  public Collection<RelationOverride> getRelationOverrides() {
    Collection<RelationOverride> copy = new ArrayList<>(relationOverrides);
    if (this.inheritDefaults && this.defaults != null) {
      copy.addAll(this.defaults.getRelationOverrides());
    }
    return copy;
  }

  public Collection<ClassGrouping> getClassGroupings() {
    Collection<ClassGrouping> copy = new ArrayList<>(classGroupings);
    if (this.inheritDefaults && this.defaults != null) {
      copy.addAll(this.defaults.getClassGroupings());
    }
    return copy;
  }

  public class ElementNoteBuilder extends PackageNoteBuilder {

    public ElementNoteBuilder(String note) {
      super(note);
    }

    public void leftOf(ClassMatcher matcher) {
      classNotes.add(new ElementNote<>(matcher, LEFT, note));
    }

    public void rightOf(ClassMatcher matcher) {
      classNotes.add(new ElementNote<>(matcher, RIGHT, note));
    }

    public void topOf(ClassMatcher matcher) {
      classNotes.add(new ElementNote<>(matcher, TOP, note));
    }

    public void bottomOf(ClassMatcher matcher) {
      classNotes.add(new ElementNote<>(matcher, BOTTOM, note));
    }

    public void leftOf(FieldMatcher matcher) {
      fieldNotes.add(new ElementNote<>(matcher, LEFT, note));
    }

    public void rightOf(FieldMatcher matcher) {
      fieldNotes.add(new ElementNote<>(matcher, RIGHT, note));
    }

    public void leftOf(MethodMatcher matcher) {
      methodNotes.add(new ElementNote<>(matcher, LEFT, note));
    }

    public void rightOf(MethodMatcher matcher) {
      methodNotes.add(new ElementNote<>(matcher, RIGHT, note));
    }

  }

  public class ClassGrouping implements Serializable {

    @Getter
    private final IncludeExclude<ClassInfo, ClassMatcher> includeExclude = new IncludeExclude<>();

    ClassGrouping() {
    }

    public ClassMatcher classes() {
      final ClassMatcher matcher = ClassDiagramDefaults.this.classes();
      includeExclude.addInclude(matcher);
      return matcher;
    }

    public ClassMatcher classes(String name) {
      final ClassMatcher matcher = ClassDiagramDefaults.this.classes(name);
      includeExclude.addInclude(matcher);
      return matcher;
    }

    public void except(ClassMatcher matcher) {
      includeExclude.addExclude(matcher);
    }

    public void include(ClassMatcher matcher) {
      ClassDiagramDefaults.this.include(matcher);
    }

    public void exclude(ClassMatcher matcher) {
      ClassDiagramDefaults.this.exclude(matcher);
      except(matcher);
    }

  }

}
