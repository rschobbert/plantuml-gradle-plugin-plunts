package io.gitlab.plunts.gradle.plantuml.examples.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Data
public abstract class AbstractDto {

  private UUID id;
  private Instant insDate;
  private Instant modDate;

}
