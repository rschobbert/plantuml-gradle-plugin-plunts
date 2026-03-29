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
package io.gitlab.plunts.gradle.plantuml.plugin;

import groovy.lang.Closure;
import groovy.lang.Script;
import java.io.File;
import org.gradle.testfixtures.ProjectBuilder;

public abstract class GradleScriptMock extends Script {

  private final ClassDiagramsExtension extension = new ClassDiagramsExtension(ProjectBuilder.builder().build().getObjects());
  private String baseDir;

  public void classDiagrams(Closure<ClassDiagramsExtension> closure) {
    closure.setDelegate(extension);
    closure.call();
  }

  void setBaseDir(String baseDir) {
    this.baseDir = baseDir;
  }

  public File file(String path) {
    return new File(baseDir + path);
  }

  ClassDiagramsExtension getExtension() {
    return extension;
  }

  public void configurations(Closure<?> closure) {
    // ignore
  }

  public void plugins(Closure<?> closure) {
    // ignore
  }

  public void subprojects(Closure<?> closure) {
    // ignore
  }

  public void dependencies(Closure<?> closure) {
    // ignore
  }

}
