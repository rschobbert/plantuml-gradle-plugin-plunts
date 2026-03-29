package io.gitlab.plunts.gradle.plantuml.examples.app.entity;

import java.time.Instant;
import javax.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Embeddable
public class Trip {

  private Instant startTime;
  private Instant endTime;

}
