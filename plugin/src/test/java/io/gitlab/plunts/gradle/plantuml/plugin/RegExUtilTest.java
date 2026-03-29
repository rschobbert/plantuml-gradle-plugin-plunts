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

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RegExUtilTest {

  @Test
  void testEscapeChar() {
    assertThat(RegExUtil.convertGlobToRegex("\\,").pattern(), is(","));
    assertThat(RegExUtil.convertGlobToRegex("\\Q").pattern(), is("\\\\Q"));
    assertThat(RegExUtil.convertGlobToRegex("\\E").pattern(), is("\\\\E"));
    assertThat(RegExUtil.convertGlobToRegex("\\*").pattern(), is("\\*"));
    assertThat(RegExUtil.convertGlobToRegex("\\\\").pattern(), is("\\\\"));
  }

  @Test
  void testClass() {
    assertThat(RegExUtil.convertGlobToRegex("[*].[?]?").pattern(), is("[*]\\.[?]."));
  }

  @Test
  void testGroup() {
    assertThat(RegExUtil.convertGlobToRegex("{!*}.{?}?").pattern(), is("(!.*)\\.(.)."));
  }

  @Test
  void testOr() {
    assertThat(RegExUtil.convertGlobToRegex("{a,b}.c,d").pattern(), is("(a|b)\\.c,d"));
  }

}
