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

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationInfoList;
import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.ArrayTypeSignature;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassRefTypeSignature;
import io.github.classgraph.FieldInfo;
import io.github.classgraph.ReferenceTypeSignature;
import io.github.classgraph.TypeArgument;
import io.github.classgraph.TypeSignature;
import io.gitlab.plunts.gradle.plantuml.plugin.relation.AssociativeRelation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.Getter;

/**
 * Common methods for {@link ClassDiagramBuilder} and {@link PackageDiagramBuilder}.
 */
abstract class DiagramBuilder<T extends Diagram> {

  static final String DOTTED_SEPERATOR = "..\n";
  static final String LINE_SEPERATOR = "__\n";
  static final String INDENTATION = "  ";
  static final String START_NOTE = "note ";
  static final String END_NOTE = "end note\n";
  @Getter
  protected final T diagram;
  protected final List<ClassInfo> classes = new ArrayList<>();
  protected final Map<String, String> packageStyles = new TreeMap<>();
  protected final Map<String, String> packageNotes = new TreeMap<>();
  protected final Map<ClassInfo, Map<ClassInfo, AssociativeRelation>> associativeRelations = new HashMap<>();

  protected DiagramBuilder(T diagram) {
    this.diagram = diagram;
  }

  /**
   * Adds a class to the diagram.
   *
   * @param classInfo the class info
   */
  public void addClass(ClassInfo classInfo) {
    if (classInfo.isSynthetic() || diagram.getBaselineIncludeExclude().isExcluded(classInfo)) {
      return;
    }

    int idx = Collections.binarySearch(classes, classInfo);
    if (idx >= 0) {
      return;
    }
    classes.add(Math.abs(idx + 1), classInfo);
    addPackage(classInfo.getPackageName());

    addHierarchy(classInfo);
    for (FieldInfo fieldInfo : classInfo.getDeclaredFieldInfo()) {
      if (!fieldInfo.isSynthetic() && !fieldInfo.isEnum()) {
        processFieldReference(classInfo, fieldInfo.getTypeSignatureOrTypeDescriptor(), fieldInfo.getAnnotationInfo());
      }
    }
  }

  protected void addPackage(String packageName) {
    if (packageName.isBlank()) {
      return;
    }

    for (var packageStyle : diagram.getStyle().getPackageStyles()) {
      if (packageStyle.getMatcher().nameMatches(packageName)) {
        packageStyles.putIfAbsent(packageName, packageStyle.getStyle());
        break;
      }
    }

    for (var packageNote : diagram.getPackageNotes()) {
      if (packageNote.getMatcher().nameMatches(packageName)) {
        final String key = packageNote.getPosition() + " of " + packageName;
        packageNotes.putIfAbsent(key, packageNote.getNote());
        break;
      }
    }

    final int lastDot = packageName.lastIndexOf('.');
    if (lastDot > 0) {
      addPackage(packageName.substring(0, lastDot));
    }
  }

  private void addHierarchy(ClassInfo classInfo) {
    if (classInfo.getSuperclass() != null && diagram.getSuperclassIncludeExclude().isIncluded(classInfo, classInfo.getSuperclass())) {
      addClass(classInfo.getSuperclass());
    }
    if (classInfo.getInterfaces() != null) {
      for (ClassInfo interfaceInfo : classInfo.getInterfaces()) {
        if (diagram.getInterfaceIncludeExclude().isIncluded(classInfo, interfaceInfo)) {
          addClass(interfaceInfo);
        }
      }
    }
    if (classInfo.getSubclasses() != null) {
      for (ClassInfo subclassInfo : classInfo.getSubclasses()) {
        if (diagram.getSubclassIncludeExclude().isIncluded(classInfo, subclassInfo)) {
          addClass(subclassInfo);
        }
      }
    }
  }

  private void processFieldReference(ClassInfo classInfo, TypeSignature typeSignature, AnnotationInfoList annotations) {
    if (typeSignature instanceof ClassRefTypeSignature classRefTypeSignature) {
      if (classRefTypeSignature.getTypeArguments().isEmpty()
              && diagram.getReferencesIncludeExclude().isIncluded(classInfo, classRefTypeSignature.getClassInfo())
              && !classRefTypeSignature.getClassInfo().getPackageName().startsWith("java")) {
        addClass(classRefTypeSignature.getClassInfo());
      }

      // first check if opposite is already known
      AssociativeRelation relation = associativeRelations
              .computeIfAbsent(classRefTypeSignature.getClassInfo(), k -> new HashMap<>())
              .get(classInfo);
      AssociativeRelation.Viewpoint left;
      AssociativeRelation.Viewpoint right;
      if (relation == null) {
        // if not, check existing or create it finally
        relation = associativeRelations
                .computeIfAbsent(classInfo, k -> new HashMap<>())
                .computeIfAbsent(classRefTypeSignature.getClassInfo(), k -> new AssociativeRelation());
        left = AssociativeRelation.Viewpoint.SOURCE;
        right = AssociativeRelation.Viewpoint.TARGET;
      } else {
        relation.activateBackReference();
        left = AssociativeRelation.Viewpoint.TARGET;
        right = AssociativeRelation.Viewpoint.SOURCE;
      }

      processJpaAnnotations(annotations, relation, left, right);
      processValidationAnnotations(annotations, relation, right);
      processTypeArguments(classRefTypeSignature, classInfo, annotations);
    } else if (typeSignature instanceof ArrayTypeSignature arrayTypeSignature) {
      processFieldReference(classInfo, arrayTypeSignature.getElementTypeSignature(), annotations);
    }
    // ignore BaseTypeSignature and TypeVariableSignature
  }

  private void processJpaAnnotations(AnnotationInfoList annotations, AssociativeRelation relation, AssociativeRelation.Viewpoint left, AssociativeRelation.Viewpoint right) {
    if (annotations.containsName("javax.persistence.OneToMany") || annotations.containsName("jakarta.persistence.OneToMany")) {
      relation.setArrow(left, AssociativeRelation.AGGREGATION_ARROW);
      relation.setMultiplicityMax(left, 1);
      relation.setMultiplicityMin(right, 0);
    } else if (annotations.containsName("javax.persistence.ManyToOne") || annotations.containsName("jakarta.persistence.ManyToOne")) {
      relation.setTargetArrow(AssociativeRelation.AGGREGATION_ARROW);
      relation.setMultiplicityMin(left, 0);
      relation.setMultiplicityMax(right, 1);
    } else if (annotations.containsName("javax.persistence.OneToOne") || annotations.containsName("jakarta.persistence.OneToOne")) {
      relation.setMultiplicityMin(left, 0);
      relation.setMultiplicityMax(left, 1);
      relation.setMultiplicityMin(right, 0);
      relation.setMultiplicityMax(right, 1);
    } else if (annotations.containsName("javax.persistence.ManyToMany") || annotations.containsName("jakarta.persistence.ManyToMany")) {
      relation.setMultiplicityMin(left, 0);
      relation.setMultiplicityMin(right, 0);
    } else if (annotations.containsName("javax.persistence.ElementCollection") || annotations.containsName("jakarta.persistence.ElementCollection")) {
      relation.setSourceArrow(AssociativeRelation.COMPOSITION_ARROW);
      relation.setMultiplicityMin(left, 1);
      relation.setMultiplicityMax(left, 1);
      relation.setMultiplicityMin(right, 0);
    }
  }

  private void processValidationAnnotations(AnnotationInfoList annotations, AssociativeRelation relation, AssociativeRelation.Viewpoint right) {
    final AnnotationInfo sizeConstraint = annotations.get("jakarta.validation.constraints.Size");
    if (sizeConstraint != null) {
      final AnnotationParameterValueList parameterValues = sizeConstraint.getParameterValues();
      final Integer min = (Integer) parameterValues.getValue("min");
      if (min != null) {
        relation.setMultiplicityMin(right, min);
      }
      final Integer max = (Integer) parameterValues.getValue("max");
      if (max != null) {
        relation.setMultiplicityMax(right, max);
      }
    }
    if (annotations.containsName("jakarta.validation.constraints.NotNull")
            || annotations.containsName("jakarta.validation.constraints.NotEmpty")) {
      relation.setMultiplicityMin(right, 1);
    }
  }

  private void processTypeArguments(final ClassRefTypeSignature classRefTypeSignature, ClassInfo classInfo, AnnotationInfoList annotations) {
    for (TypeArgument typeArgument : classRefTypeSignature.getTypeArguments()) {
      final ReferenceTypeSignature referenceTypeSignature = typeArgument.getTypeSignature();
      if (referenceTypeSignature != null) {
        processFieldReference(classInfo, referenceTypeSignature, annotations);
      }
    }
  }

  abstract String build();

  protected void writeStyle(StringBuilder sb) {
    for (String include : diagram.getStyle().getIncludes()) {
      sb.append("!include ").append(include).append('\n');
    }
    for (String define : diagram.getStyle().getDefines()) {
      sb.append("!define ").append(define).append('\n');
    }
    sb.append("!pragma useIntermediatePackages ").append(diagram.getStyle().isUseIntermediatePackages()).append('\n');
    if (!diagram.getStyle().isShowPackages()) {
      sb.append("set separator none\n");
    }
    appendIfNotNull(sb, "!theme ", diagram.getStyle().getTheme());
    for (Map.Entry<String, String> entry : diagram.getStyle().getSkinparams().entrySet()) {
      sb.append("skinparam ").append(entry.getKey()).append(" ").append(entry.getValue()).append('\n');
    }
    for (Map.Entry<String, Boolean> entry : diagram.getStyle().getShow().entrySet()) {
      if (Boolean.TRUE.equals(entry.getValue())) {
        sb.append("show ");
      } else {
        sb.append("hide ");
      }
      sb.append(entry.getKey()).append('\n');
    }
    for (Map.Entry<String, String> entry : packageStyles.entrySet()) {
      sb.append("package ");
      int idx = entry.getKey().lastIndexOf('.');
      if (idx >= 0) {
        sb.append(entry.getKey().substring(idx + 1)).append(" as ");
      }
      sb.append(entry.getKey()).append(" ").append(entry.getValue()).append(" {}\n");
    }
  }

  private void appendIfNotNull(StringBuilder sb, String prefix, Object value) {
    if (value != null) {
      sb.append(prefix).append(value).append('\n');
    }
  }

  protected void writePackageNotes(StringBuilder target) {
    for (Map.Entry<String, String> entry : packageNotes.entrySet()) {
      target.append(START_NOTE).append(entry.getKey()).append('\n');
      target.append(entry.getValue()).append('\n').append(END_NOTE);
    }
  }

}
