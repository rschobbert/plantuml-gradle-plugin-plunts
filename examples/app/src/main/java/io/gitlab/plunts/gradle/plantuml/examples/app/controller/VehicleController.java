package io.gitlab.plunts.gradle.plantuml.examples.app.controller;

import io.gitlab.plunts.gradle.plantuml.examples.app.entity.Vehicle;
import io.gitlab.plunts.gradle.plantuml.examples.app.service.VehicleService;
import io.gitlab.plunts.gradle.plantuml.examples.dto.VehicleDto;
import java.util.List;
import jdk.jfr.Description;

import static java.util.Collections.emptyList;

public class VehicleController {

  @Description("Just an example for annotation matching")
  private VehicleService service;

  public List<VehicleDto> listVehicles(int page, int offset) {
    List<Vehicle> vehicles = service.getVehicles(page, offset);
    // skipping the magic
    return emptyList();
  }

}
