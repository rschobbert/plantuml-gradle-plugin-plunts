package io.gitlab.plunts.gradle.plantuml.examples.app.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

  @Id
  private UUID id;
  private Instant insDate;
  private Instant modDate;

}
