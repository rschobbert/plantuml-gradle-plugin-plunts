package io.gitlab.plunts.gradle.plantuml.examples.dto;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VehicleDto extends AbstractDto {

  private String plateNo;
  private boolean registered;
  private List<TireDto> tires;
  private SteeringWheelDto steeringWheel;

}
