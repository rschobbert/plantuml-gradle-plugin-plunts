/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gitlab.plunts.gradle.plantuml.plugin.matcher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import lombok.Getter;

/**
 * Aggregator for includes and excludes of the same type.
 *
 * @param <E> the element type
 * @param <M> the matcher type
 */
@Getter
public final class IncludeExclude<E, M extends Predicate<E>> implements Serializable {

  private final List<M> includes = new ArrayList<>();
  private final List<M> excludes = new ArrayList<>();

  public IncludeExclude() {
  }

  public IncludeExclude(IncludeExclude<E, M> other) {
    this.addAll(other);
  }

  public void addAll(IncludeExclude<E, M> other) {
    this.includes.addAll(other.includes);
    this.excludes.addAll(other.excludes);
  }

  public void addInclude(M predicate) {
    excludes.remove(predicate);
    includes.add(predicate);
  }

  public void addExclude(M predicate) {
    includes.remove(predicate);
    excludes.add(predicate);
  }

  public boolean isEmpty() {
    return includes.isEmpty() && excludes.isEmpty();
  }

  public boolean isIncluded(E object) {
    return includes.stream().anyMatch(predicate -> predicate.test(object)) && !isExcluded(object);
  }

  public boolean isExcluded(E object) {
    return excludes.stream().anyMatch(predicate -> predicate.test(object));
  }

}
