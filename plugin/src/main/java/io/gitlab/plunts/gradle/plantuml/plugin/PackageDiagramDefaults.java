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
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.BaselineMatcher;
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.BiIncludeExclude;
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.ClassMatcher;
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.IncludeExclude;
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.InterfaceMatcher;
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.PackageMatcher;
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.ReferencedClassMatcher;
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.SubclassMatcher;
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.SuperclassMatcher;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.gradle.api.Action;

/**
 * Gradle DSL to configure defaults for package diagrams of the project.
 */
public class PackageDiagramDefaults implements Serializable {

  protected static final String LEFT = "left";
  protected static final String RIGHT = "right";
  protected static final String TOP = "top";
  protected static final String BOTTOM = "bottom";
  protected boolean inheritDefaults = true;
  private PackageDiagramDefaults defaults;
  private final IncludeExclude<ClassInfo, BaselineMatcher> baselineIncludeExclude = new IncludeExclude<>();
  private final BiIncludeExclude<ClassInfo, SuperclassMatcher> superclassIncludeExclude = new BiIncludeExclude<>();
  private final BiIncludeExclude<ClassInfo, SubclassMatcher> subclassIncludeExclude = new BiIncludeExclude<>();
  private final BiIncludeExclude<ClassInfo, InterfaceMatcher> interfaceIncludeExclude = new BiIncludeExclude<>();
  private final BiIncludeExclude<ClassInfo, ReferencedClassMatcher> referencesIncludeExclude = new BiIncludeExclude<>();
  private final Collection<ElementNote<PackageMatcher>> packageNotes = new ArrayList<>();
  @Getter
  protected final Style style = new Style();

  PackageDiagramDefaults(PackageDiagramDefaults defaults) {
    this.defaults = defaults;
  }

  void setDefaults(PackageDiagramDefaults defaults) {
    this.defaults = defaults;
  }

  public void style(Action<Style> action) {
    action.execute(style);
  }

  public void style(Closure<Style> closure) {
    closure.setDelegate(style);
    closure.call(style);
  }

  public void dontInheritDefaults() {
    this.inheritDefaults = false;
  }

  public void include(BaselineMatcher matcher) {
    baselineIncludeExclude.addInclude(matcher);
  }

  public void include(SuperclassMatcher matcher) {
    superclassIncludeExclude.addInclude(matcher);
  }

  public void include(SubclassMatcher matcher) {
    subclassIncludeExclude.addInclude(matcher);
  }

  public void include(InterfaceMatcher matcher) {
    interfaceIncludeExclude.addInclude(matcher);
  }

  public void include(ReferencedClassMatcher matcher) {
    referencesIncludeExclude.addInclude(matcher);
  }

  public void exclude(BaselineMatcher matcher) {
    baselineIncludeExclude.addExclude(matcher);
  }

  public void exclude(SuperclassMatcher matcher) {
    superclassIncludeExclude.addExclude(matcher);
  }

  public void exclude(SubclassMatcher matcher) {
    subclassIncludeExclude.addExclude(matcher);
  }

  public void exclude(InterfaceMatcher matcher) {
    interfaceIncludeExclude.addExclude(matcher);
  }

  public void exclude(ReferencedClassMatcher matcher) {
    referencesIncludeExclude.addExclude(matcher);
  }

  public PackageNoteBuilder note(String note) {
    return new PackageNoteBuilder(note);
  }

  public PackageMatcher packages() {
    return new PackageMatcher();
  }

  public PackageMatcher packages(String name) {
    return new PackageMatcher().withNameLike(name);
  }

  public SuperclassMatcher superclasses() {
    return new SuperclassMatcher();
  }

  public SubclassMatcher subclasses() {
    return new SubclassMatcher();
  }

  public InterfaceMatcher interfaces() {
    return new InterfaceMatcher();
  }

  public ReferencedClassMatcher referencedClasses() {
    return new ReferencedClassMatcher();
  }

  public IncludeExclude<ClassInfo, BaselineMatcher> getBaselineIncludeExclude() {
    IncludeExclude<ClassInfo, BaselineMatcher> copy = new IncludeExclude<>(baselineIncludeExclude);
    if (this.inheritDefaults && this.defaults != null) {
      copy.addAll(this.defaults.getBaselineIncludeExclude());
    }
    return copy;
  }

  public BiIncludeExclude<ClassInfo, SuperclassMatcher> getSuperclassIncludeExclude() {
    BiIncludeExclude<ClassInfo, SuperclassMatcher> copy = new BiIncludeExclude<>(superclassIncludeExclude);
    if (this.inheritDefaults && this.defaults != null) {
      copy.addAll(this.defaults.getSuperclassIncludeExclude());
    }
    return copy;
  }

  public BiIncludeExclude<ClassInfo, SubclassMatcher> getSubclassIncludeExclude() {
    BiIncludeExclude<ClassInfo, SubclassMatcher> copy = new BiIncludeExclude<>(subclassIncludeExclude);
    if (this.inheritDefaults && this.defaults != null) {
      copy.addAll(this.defaults.getSubclassIncludeExclude());
    }
    return copy;
  }

  public BiIncludeExclude<ClassInfo, InterfaceMatcher> getInterfaceIncludeExclude() {
    BiIncludeExclude<ClassInfo, InterfaceMatcher> copy = new BiIncludeExclude<>(interfaceIncludeExclude);
    if (this.inheritDefaults && this.defaults != null) {
      copy.addAll(this.defaults.getInterfaceIncludeExclude());
    }
    return copy;
  }

  public BiIncludeExclude<ClassInfo, ReferencedClassMatcher> getReferencesIncludeExclude() {
    BiIncludeExclude<ClassInfo, ReferencedClassMatcher> copy = new BiIncludeExclude<>(referencesIncludeExclude);
    if (this.inheritDefaults && this.defaults != null) {
      copy.addAll(this.defaults.getReferencesIncludeExclude());
    }
    return copy;
  }

  public List<ElementNote<PackageMatcher>> getPackageNotes() {
    List<ElementNote<PackageMatcher>> copy = new ArrayList<>();
    if (this.inheritDefaults && this.defaults != null) {
      copy.addAll(this.defaults.getPackageNotes());
    }
    copy.addAll(this.packageNotes);
    return copy;
  }

  public class Style implements Serializable {

    private final List<String> includes = new ArrayList<>();
    private final List<String> defines = new ArrayList<>();
    private Boolean useIntermediatePackages = null;
    private Boolean showPackages = null;
    private String theme;
    private final Map<String, String> skinparams = new LinkedHashMap<>();
    private final Map<String, Boolean> show = new LinkedHashMap<>();
    private final List<ElementStyle<PackageMatcher>> packageStyles = new ArrayList<>();
    private final List<ElementStyle<ClassMatcher>> classStyles = new ArrayList<>();

    public void include(String file) {
      includes.add(file);
    }

    public void useIntermediatePackages() {
      useIntermediatePackages = Boolean.TRUE;
    }

    public void define(String def) {
      defines.add(def);
    }

    public void define(String def1, String def2) {
      define(def1 + " " + def2);
    }

    public void hidePackages() {
      showPackages = Boolean.FALSE;
    }

    public void theme(String theme) {
      this.theme = theme;
    }

    public void skinparam(String name, Object value) {
      skinparams.put(name, Objects.requireNonNull(value, "value must not be null").toString());
    }

    public void hide(String what) {
      show.put(what, Boolean.FALSE);
    }

    public void show(String what) {
      show.put(what, Boolean.TRUE);
    }

    public ElementStyleBuilder addStyle(String style) {
      return new ElementStyleBuilder(style);
    }

    public List<String> getIncludes() {
      List<String> copy = new ArrayList<>();
      if (inheritDefaults && defaults != null) {
        copy.addAll(defaults.style.getIncludes());
      }
      copy.addAll(includes);
      return copy;
    }

    public List<String> getDefines() {
      List<String> copy = new ArrayList<>();
      if (inheritDefaults && defaults != null) {
        copy.addAll(defaults.style.getDefines());
      }
      copy.addAll(defines);
      return copy;
    }

    public boolean isUseIntermediatePackages() {
      if (this.useIntermediatePackages != null) {
        return this.useIntermediatePackages;
      } else if (inheritDefaults && defaults != null) {
        return defaults.style.isUseIntermediatePackages();
      } else {
        return false;
      }
    }

    boolean isShowPackages() {
      if (this.showPackages != null) {
        return this.showPackages;
      } else if (inheritDefaults && defaults != null) {
        return defaults.style.isShowPackages();
      } else {
        return true;
      }
    }

    public String getTheme() {
      if (this.theme != null) {
        return this.theme;
      } else if (inheritDefaults && defaults != null) {
        return defaults.style.getTheme();
      } else {
        return null;
      }
    }

    public Map<String, String> getSkinparams() {
      Map<String, String> copy = new LinkedHashMap<>();
      if (inheritDefaults && defaults != null) {
        copy.putAll(defaults.style.getSkinparams());
      }
      copy.putAll(skinparams);
      return copy;
    }

    public Map<String, Boolean> getShow() {
      Map<String, Boolean> copy = new LinkedHashMap<>();
      if (inheritDefaults && defaults != null) {
        copy.putAll(defaults.style.getShow());
      }
      copy.putAll(show);
      return copy;
    }

    public List<ElementStyle<PackageMatcher>> getPackageStyles() {
      List<ElementStyle<PackageMatcher>> copy = new ArrayList<>();
      copy.addAll(packageStyles);
      if (inheritDefaults && defaults != null) {
        copy.addAll(defaults.style.getPackageStyles());
      }
      return copy;
    }

    public List<ElementStyle<ClassMatcher>> getClassStyles() {
      List<ElementStyle<ClassMatcher>> copy = new ArrayList<>();
      copy.addAll(classStyles);
      if (inheritDefaults && defaults != null) {
        copy.addAll(defaults.style.getClassStyles());
      }
      return copy;
    }

    @AllArgsConstructor
    public class ElementStyleBuilder {

      private final String style;

      public void to(PackageMatcher matcher) {
        packageStyles.add(new ElementStyle<>(matcher, style));
      }

      public void to(ClassMatcher matcher) {
        classStyles.add(new ElementStyle<>(matcher, style));
      }
    }
  }

  @Data
  @AllArgsConstructor
  public static class ElementStyle<M extends BaselineMatcher & Serializable> implements Serializable {

    private final M matcher;
    private final String style;
  }

  @AllArgsConstructor
  public class PackageNoteBuilder {

    protected final String note;

    public void leftOf(PackageMatcher matcher) {
      packageNotes.add(new ElementNote<>(matcher, LEFT, note));
    }

    public void rightOf(PackageMatcher matcher) {
      packageNotes.add(new ElementNote<>(matcher, RIGHT, note));
    }

    public void topOf(PackageMatcher matcher) {
      packageNotes.add(new ElementNote<>(matcher, TOP, note));
    }

    public void bottomOf(PackageMatcher matcher) {
      packageNotes.add(new ElementNote<>(matcher, BOTTOM, note));
    }
  }

  @Data
  @AllArgsConstructor
  public static class ElementNote<M extends Serializable> implements Serializable {

    private final M matcher;
    private final String position;
    private final String note;
  }

}
