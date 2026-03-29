package io.gitlab.plunts.gradle.plantuml.examples.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TireDto extends AbstractDto {

  private int diameter;
  private VehicleDto mountedTo;

}
