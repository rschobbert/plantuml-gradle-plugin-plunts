package io.gitlab.plunts.gradle.plantuml.examples.app.entity;

import java.util.List;
import javax.persistence.ManyToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Car extends Vehicle {

  private int maxPassengers;
  @ManyToMany
  private List<ParkingSlot> allowedParkingSlots;

}
