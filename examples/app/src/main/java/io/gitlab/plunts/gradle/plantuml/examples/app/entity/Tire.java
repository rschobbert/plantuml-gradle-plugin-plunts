package io.gitlab.plunts.gradle.plantuml.examples.app.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Tire extends AbstractEntity {

  public static final int DEFAULT_RADIUS = 17;
  private int diameter = DEFAULT_RADIUS;
  @ManyToOne
  private Vehicle mountedTo;

}
