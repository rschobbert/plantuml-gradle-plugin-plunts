package io.gitlab.plunts.gradle.plantuml.examples.app.entity;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode
public class ParkingSlot {

  @Id
  private UUID id;
  @ManyToMany
  private List<Car> allowedCars;
  private Map<String, Object> foobar;

}
