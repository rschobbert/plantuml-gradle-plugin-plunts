package io.gitlab.plunts.gradle.plantuml.examples.app.service;

import io.gitlab.plunts.gradle.plantuml.examples.app.entity.Vehicle;
import io.gitlab.plunts.gradle.plantuml.examples.app.service.internal.GarageHelper;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

import static java.util.Collections.emptyList;

public class VehicleService {

  private GarageHelper garageHelper = new GarageHelper();

  public List<Vehicle> getVehicles(int page, int offset) {
    return emptyList();
  }

  @Deprecated
  protected final @NotNull List<Vehicle> getVehicelsNoChecks(@NotNull Integer page, @NotNull Integer offset) {
    return emptyList();
  }

  private String buildQuery() {
    return new QueryBuilder().build();
  }

  private static final class QueryBuilder<T extends Vehicle> {

    @Getter
    @Setter
    private T entity;
    @Getter
    @Setter
    private String[] fields;

    public String build() {
      return "";
    }

  }

}
