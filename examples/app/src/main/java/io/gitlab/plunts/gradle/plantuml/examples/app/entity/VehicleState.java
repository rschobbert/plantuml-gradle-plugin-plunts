package io.gitlab.plunts.gradle.plantuml.examples.app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum to test special notation.
 */
@Getter
@AllArgsConstructor
public enum VehicleState {

  NEW("new"), IN_USE("in use"), BROKEN("broken"), SCRAPPED("scrapped");

  private final String label;

  @Override
  public String toString() {
    return label;
  }

}
