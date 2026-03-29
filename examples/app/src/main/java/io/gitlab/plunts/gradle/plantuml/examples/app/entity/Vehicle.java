package io.gitlab.plunts.gradle.plantuml.examples.app.entity;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@DiscriminatorColumn
@EqualsAndHashCode(callSuper = true)
public class Vehicle extends AbstractEntity {

  private String plateNo;
  private boolean registered;
  @NotNull
  @Column(name = "vehicle_state")
  private VehicleState state;
  @OneToMany(mappedBy = "mountedTo")
  @Size(min = 2, max = 100)
  private List<Tire> tires;
  @NotNull
  @OneToOne
  private SteeringWheel steeringWheel;
  @OneToMany
  @NotEmpty
  private List<Passenger> passengers;
  @ElementCollection
  private List<Trip> trips;

}
