package io.gitlab.plunts.gradle.plantuml.examples.app.entity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TestAnnotation {

  String value();

}
