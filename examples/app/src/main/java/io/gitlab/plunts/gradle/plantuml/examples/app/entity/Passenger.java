package io.gitlab.plunts.gradle.plantuml.examples.app.entity;

import javax.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Passenger extends AbstractEntity {

  private String name;

}
