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

import io.github.classgraph.ArrayTypeSignature;
import io.github.classgraph.BaseTypeSignature;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassRefTypeSignature;
import io.github.classgraph.FieldInfo;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodParameterInfo;
import io.github.classgraph.PackageInfo;
import io.github.classgraph.TypeArgument;
import io.github.classgraph.TypeParameter;
import io.github.classgraph.TypeSignature;
import io.github.classgraph.TypeVariableSignature;
import io.gitlab.plunts.gradle.plantuml.plugin.relation.AbstractClassRelation;
import io.gitlab.plunts.gradle.plantuml.plugin.relation.ExtensionRelation;
import io.gitlab.plunts.gradle.plantuml.plugin.relation.GenericRelation;
import io.gitlab.plunts.gradle.plantuml.plugin.relation.PackageRelation;
import io.gitlab.plunts.gradle.plantuml.plugin.relation.RelationOverride;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

/**
 * Parses ClassInfo objects and builds a PlantUML diagram out of them.
 */
class ClassDiagramBuilder extends DiagramBuilder<ClassDiagram> {

  private boolean seperatorBeforeFields;
  private boolean seperatorBeforeMethods;

  ClassDiagramBuilder(ClassDiagram diagram) {
    super(diagram);
  }

  @Override
  public String build() {
    StringBuilder sb = new StringBuilder("@startuml ").append(diagram.getName()).append('\n');
    writeStyle(sb);
    sb.append("\n");
    writeClassDefinitions(sb);
    writePackageNotes(sb);
    writeClassRelations(sb);
    writePackageRelations(sb);
    sb.append("@enduml\n");
    return sb.toString();
  }

  private void writeClassDefinitions(StringBuilder target) {
    final Collection<ClassDiagramDefaults.ClassGrouping> classGroupings = diagram.getClassGroupings();
    for (ClassInfo classInfo : classes) {
      if (classGroupings.stream().noneMatch(group -> group.getIncludeExclude().isIncluded(classInfo))) {
        writeClassInfo(classInfo, "", target);
      }
    }

    for (ClassDiagramDefaults.ClassGrouping classGrouping : classGroupings) {
      target.append("together {\n");
      for (ClassInfo classInfo : classes) {
        if (classGrouping.getIncludeExclude().isIncluded(classInfo)) {
          writeClassInfo(classInfo, INDENTATION, target);
        }
      }
      target.append("}\n");
    }
  }

  private void writeClassInfo(ClassInfo classInfo, String indentation, StringBuilder target) {
    target.append(indentation).append(getTypePrefix(classInfo)).append(" \"");
    writeClassName(classInfo, target);
    for (var classStyle : diagram.getStyle().getClassStyles()) {
      if (classStyle.getMatcher().test(classInfo)) {
        target.append(" ").append(classStyle.getStyle());
        break;
      }
    }
    target.append(" {\n");
    StringBuilder memberNotes = writeMembers(classInfo, indentation + INDENTATION, target);
    target.append(indentation).append("}\n");
    for (var classNote : diagram.getClassNotes()) {
      if (classNote.getMatcher().test(classInfo)) {
        target.append(indentation).append(START_NOTE).append(classNote.getPosition()).append(" of ").append(classInfo.getName()).append('\n');
        classNote.getNote().lines().forEachOrdered(line -> 
          target.append(indentation).append(INDENTATION).append(line).append('\n')
        );
        target.append(indentation).append(END_NOTE);
      }
    }
    target.append(memberNotes);
  }

  private StringBuilder writeMembers(ClassInfo classInfo, String indentation, StringBuilder target) {
    StringBuilder memberNotes = new StringBuilder();
    for (FieldInfo fieldInfo : classInfo.getDeclaredFieldInfo()) {
      if (!fieldInfo.isSynthetic() && diagram.getFieldIncludeExclude().isIncluded(fieldInfo)) {
        writeFieldInfo(fieldInfo, indentation, target, memberNotes);
      }
    }
    for (MethodInfo methodInfo : classInfo.getDeclaredMethodInfo()) {
      if (!methodInfo.isSynthetic() && diagram.getMethodIncludeExclude().isIncluded(methodInfo)) {
        writeMethodInfo(methodInfo, indentation, target, memberNotes);
      }
    }
    return memberNotes;
  }

  private void writeClassName(ClassInfo classInfo, StringBuilder target) {
    for (ClassInfo outerClass : classInfo.getOuterClasses()) {
      target.append(outerClass.getSimpleName()).append("$");
    }
    target.append(classInfo.getSimpleName()).append("\" as ").append(classInfo.getName());
    if (!classInfo.getTypeSignatureOrTypeDescriptor().getTypeParameters().isEmpty()) {
      target.append("<");
      for (Iterator<TypeParameter> it = classInfo.getTypeSignatureOrTypeDescriptor().getTypeParameters().iterator(); it.hasNext();) {
        TypeParameter typeParameter = it.next();
        target.append(typeParameter.toStringWithSimpleNames());
        if (it.hasNext()) {
          target.append(", ");
        }
      }
      target.append(">");
    }
  }

  private void writeFieldInfo(FieldInfo fieldInfo, String indentation, StringBuilder target, StringBuilder memberNotes) {
    if (fieldInfo.isEnum()) {
      target.append(indentation).append(fieldInfo.getName()).append("\n");
      seperatorBeforeFields = true;
      seperatorBeforeMethods = true;
    } else {
      if (seperatorBeforeFields) {
        target.append(indentation).append(DOTTED_SEPERATOR);
        seperatorBeforeFields = false;
      }

      target.append(indentation).append(getVisibilityPrefix(fieldInfo.getModifiers()));
      appendTypeWithoutAnnotations(fieldInfo.getTypeSignatureOrTypeDescriptor(), target);
      target.append(" ").append(fieldInfo.getName()).append("\n");
      seperatorBeforeMethods = true;
    }

    for (var fieldNote : diagram.getFieldNotes()) {
      if (fieldNote.getMatcher().test(fieldInfo)) {
        memberNotes.append(START_NOTE).append(fieldNote.getPosition()).append(" of ")
                .append(fieldInfo.getClassInfo().getName()).append("::").append(fieldInfo.getName())
                .append('\n').append(fieldNote.getNote()).append('\n').append(END_NOTE);
      }
    }
  }

  private void writeMethodInfo(MethodInfo methodInfo, String indentation, StringBuilder target, StringBuilder memberNotes) {
    if (seperatorBeforeMethods) {
      target.append(indentation).append(LINE_SEPERATOR);
      seperatorBeforeMethods = false;
    }

    target.append(indentation).append(getVisibilityPrefix(methodInfo.getModifiers()));
    appendTypeWithoutAnnotations(methodInfo.getTypeSignatureOrTypeDescriptor().getResultType(), target);
    target.append(" ").append(methodInfo.getName()).append("(");
    for (Iterator<MethodParameterInfo> it = asList(methodInfo.getParameterInfo()).iterator(); it.hasNext();) {
      MethodParameterInfo parameter = it.next();
      appendTypeWithoutAnnotations(parameter.getTypeSignatureOrTypeDescriptor(), target);
      if (it.hasNext()) {
        target.append(", ");
      }
    }
    target.append(")\n");

    for (var methodNote : diagram.getMethodNotes()) {
      if (methodNote.getMatcher().test(methodInfo)) {
        memberNotes.append(START_NOTE).append(methodNote.getPosition()).append(" of ")
                .append(methodInfo.getClassInfo().getName()).append("::").append(methodInfo.getName())
                .append('\n').append(methodNote.getNote()).append('\n').append(END_NOTE);
      }
    }
  }

  private void writeClassRelations(StringBuilder target) {
    for (ClassInfo classInfo : classes) {
      writeClassExtensions(classInfo, target);
      associativeRelations.getOrDefault(classInfo, emptyMap()).forEach((associatedClass, association)
              -> writeClassRelation(classInfo, associatedClass, association, target)
      );
      writeAdditionalRelations(classInfo, target);
    }
  }

  private void writeClassExtensions(ClassInfo classInfo, StringBuilder target) {
    List<ClassInfo> excludedInterfaces = emptyList();
    if (classInfo.getSuperclass() != null) {
      excludedInterfaces = classInfo.getSuperclass().getInterfaces();
      writeClassRelation(classInfo, classInfo.getSuperclass(), new ExtensionRelation(false), target);
    }
    for (ClassInfo interfaceInfo : classInfo.getInterfaces()) {
      if (!excludedInterfaces.contains(interfaceInfo)) {
        writeClassRelation(classInfo, interfaceInfo, new ExtensionRelation(true), target);
      }
    }
  }

  private void writeAdditionalRelations(ClassInfo classInfo, StringBuilder target) {
    for (RelationOverride additionalRelation : diagram.getAdditionalRelations()) {
      if (additionalRelation.isFrom(classInfo)) {
        // welp, let's look for the other side(s) of this relation...
        for (ClassInfo to : classes) {
          if (additionalRelation.isTo(to)) {
            writeClassRelationNoChecks(classInfo, to, GenericRelation.INSTANCE, additionalRelation, target);
          }
        }
      }
    }
  }

  private void writeClassRelation(ClassInfo from, ClassInfo to, AbstractClassRelation relation, StringBuilder target) {
    if (!classes.contains(from) || !classes.contains(to) || diagram.getRemovedRelations().stream().anyMatch(rel -> rel.matches(relation.getClass(), from, to))) {
      return;
    }

    RelationOverride override = RelationOverride.EMPTY;
    for (RelationOverride relationOverride : diagram.getRelationOverrides()) {
      if (relationOverride.matches(relation.getClass(), from, to)) {
        override = relationOverride;
        break;
      }
    }

    writeClassRelationNoChecks(from, to, relation, override, target);
  }

  private void writeClassRelationNoChecks(ClassInfo from, ClassInfo to, AbstractClassRelation relation, RelationOverride override, StringBuilder target) {
    target.append(from.getName()).append(" ");
    override.writeArrow(relation, target);
    target.append(" ").append(to.getName());
    override.writeLabel(relation, target);
    target.append("\n");
  }

  private void writePackageRelations(StringBuilder target) {
    List<PackageRelation> packageRelations = diagram.getPackageRelations();
    if (packageRelations.size() > 0) {
      for (PackageRelation packageRelation : packageRelations) {
        writePackageRelation(packageRelation, target);
      }
    }
  }

  private void writePackageRelation(PackageRelation packageRelation, StringBuilder target) {
    for (ClassInfo fromClass : classes) {
      if (writePackageRelation(packageRelation, fromClass.getPackageInfo(), target)) {
          return; // abort loop, as soon as package relation was written
      }
    }
  }

  private boolean writePackageRelation(PackageRelation packageRelation, PackageInfo fromPackage, StringBuilder target) {
    if (packageRelation.isFrom(fromPackage)) {
      // let's look for the other side(s) of this relation...
      for (ClassInfo to : classes) {
        PackageInfo toPackage = to.getPackageInfo();
        if (packageRelation.isTo(toPackage)) {
          writePackageRelationNoChecks(fromPackage, toPackage, packageRelation, target);
          return true;
        }
      }
    }
    return false;
  }
  
  private void writePackageRelationNoChecks(PackageInfo from, PackageInfo to, PackageRelation packageRelation, StringBuilder target) {
    target.append(from.getName()).append(" ");
    packageRelation.writeArrow(target);
    target.append(" ").append(to.getName());
    packageRelation.writeLabel(target);
    target.append("\n");
    packageRelation.setAlreadyWritten(true);
  }

  private String getTypePrefix(ClassInfo classInfo) {
    if (classInfo.isEnum()) {
      return "enum";
    } else if (classInfo.isAnnotation()) {
      return "annotation";
    } else if (classInfo.isInterface()) {
      return "interface";
    } else if (classInfo.isAbstract()) {
      return "abstract class";
    } else if (classInfo.hasAnnotation("javax.persistence.Entity") || classInfo.hasAnnotation("jakarta.persistence.Entity")) {
      return "entity";
    } else {
      return "class";
    }
  }

  private String getVisibilityPrefix(int modifiers) {
    String prefix;
    if (Modifier.isPrivate(modifiers)) {
      prefix = "-";
    } else if (Modifier.isProtected(modifiers)) {
      prefix = "~";
    } else if (Modifier.isPublic(modifiers)) {
      prefix = "+";
    } else {
      prefix = "#";
    }

    if (Modifier.isStatic(modifiers)) {
      prefix += " {static}";
    } else if (Modifier.isAbstract(modifiers)) {
      prefix += " {abstract}";
    }
    return prefix;
  }

  private void appendTypeWithoutAnnotations(TypeSignature typeSignature, StringBuilder sb) {
    if (typeSignature instanceof ClassRefTypeSignature classRefTypeSignature) {
      sb.append(classRefTypeSignature.getClassInfo().getSimpleName());

      List<TypeArgument> typeArguments = classRefTypeSignature.getTypeArguments();
      // Append base class type arguments
      if (!typeArguments.isEmpty()) {
        appendTypeArguments(sb, typeArguments);
      }
    } else if (typeSignature instanceof ArrayTypeSignature arrayTypeSignature) {
      appendTypeWithoutAnnotations(arrayTypeSignature.getElementTypeSignature(), sb);
      for (int i = 0; i < arrayTypeSignature.getNumDimensions(); i++) {
        sb.append("[]");
      }
    } else if (typeSignature instanceof BaseTypeSignature baseTypeSignature) {
      sb.append(baseTypeSignature.getTypeStr());
    } else if (typeSignature instanceof TypeVariableSignature typeVariableSignature) {
      sb.append(typeVariableSignature.getName());
    } else {
      throw new AssertionError("Unknown signature type " + typeSignature.getClass().getName());
    }
  }

  private void appendTypeArguments(StringBuilder sb, List<TypeArgument> typeArguments) {
    sb.append('<');
    for (Iterator<TypeArgument> it = typeArguments.iterator(); it.hasNext();) {
      sb.append(it.next().toStringWithSimpleNames());
      if (it.hasNext()) {
        sb.append(", ");
      }
    }
    sb.append('>');
  }

}
