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

import java.lang.reflect.Modifier;
import org.gradle.api.GradleException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The modifier matcher has too many variantions, so we need a seperate test for it.
 */
class AbstractModifierMatcherTest {

  @Test
  void testThatArePrivate() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatArePrivate();
    assertTrue(matcher.modifiersMatch(Modifier.STATIC | Modifier.FINAL | Modifier.PRIVATE));
    assertFalse(matcher.modifiersMatch(Modifier.NATIVE | Modifier.VOLATILE | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.TRANSIENT | Modifier.STRICT));
  }

  @Test
  void testThatArePackagePrivate() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatArePackagePrivate();
    assertTrue(matcher.modifiersMatch(Modifier.STATIC | Modifier.FINAL));
    assertFalse(matcher.modifiersMatch(Modifier.STATIC | Modifier.FINAL | Modifier.PROTECTED));
    assertFalse(matcher.modifiersMatch(Modifier.NATIVE | Modifier.VOLATILE | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.TRANSIENT | Modifier.STRICT | Modifier.PRIVATE));
  }

  @Test
  void testThatAreProtected() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatAreProtected();
    assertTrue(matcher.modifiersMatch(Modifier.STATIC | Modifier.FINAL | Modifier.PROTECTED));
    assertFalse(matcher.modifiersMatch(Modifier.NATIVE | Modifier.VOLATILE | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.TRANSIENT | Modifier.STRICT));
  }

  @Test
  void testThatArePublic() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatArePublic();
    assertTrue(matcher.modifiersMatch(Modifier.STATIC | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.NATIVE | Modifier.VOLATILE | Modifier.PROTECTED));
    assertFalse(matcher.modifiersMatch(Modifier.TRANSIENT | Modifier.STRICT));
  }

  @Test
  void testThatAreNotPublic() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatAreNotPublic();
    assertTrue(matcher.modifiersMatch(Modifier.INTERFACE | Modifier.SYNCHRONIZED | Modifier.PRIVATE));
    assertTrue(matcher.modifiersMatch(Modifier.STATIC | Modifier.FINAL | Modifier.PROTECTED));
    assertTrue(matcher.modifiersMatch(Modifier.TRANSIENT | Modifier.STRICT));
    assertFalse(matcher.modifiersMatch(Modifier.NATIVE | Modifier.VOLATILE | Modifier.PUBLIC));
  }

  @Test
  void testThatAreAbstract() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatAreAbstract();
    assertTrue(matcher.modifiersMatch(Modifier.ABSTRACT | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.NATIVE | Modifier.VOLATILE | Modifier.PROTECTED));
    assertFalse(matcher.modifiersMatch(Modifier.TRANSIENT | Modifier.STRICT));
  }

  @Test
  void testThatAreNotAbstract() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatAreNotAbstract();
    assertTrue(matcher.modifiersMatch(Modifier.NATIVE | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.ABSTRACT | Modifier.VOLATILE | Modifier.PROTECTED));
  }

  @Test
  void testThatAreStatic() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatAreStatic();
    assertTrue(matcher.modifiersMatch(Modifier.STATIC | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.NATIVE | Modifier.VOLATILE | Modifier.PROTECTED));
    assertFalse(matcher.modifiersMatch(Modifier.TRANSIENT | Modifier.STRICT));
  }

  @Test
  void testThatAreNotStatic() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatAreNotStatic();
    assertTrue(matcher.modifiersMatch(Modifier.NATIVE | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.STATIC | Modifier.VOLATILE | Modifier.PROTECTED));
  }

  @Test
  void testThatAreVolatile() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatAreVolatile();
    assertTrue(matcher.modifiersMatch(Modifier.VOLATILE | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.STATIC | Modifier.NATIVE | Modifier.PROTECTED));
  }

  @Test
  void testThatAreNotVolatile() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatAreNotVolatile();
    assertTrue(matcher.modifiersMatch(Modifier.NATIVE | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.STATIC | Modifier.VOLATILE | Modifier.PROTECTED));
  }

  @Test
  void testThatAreTransient() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatAreTransient();
    assertTrue(matcher.modifiersMatch(Modifier.TRANSIENT | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.STATIC | Modifier.VOLATILE | Modifier.PROTECTED));
  }

  @Test
  void testThatAreNotTransient() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatAreNotTransient();
    assertTrue(matcher.modifiersMatch(Modifier.NATIVE | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.STATIC | Modifier.TRANSIENT | Modifier.PROTECTED));
  }

  @Test
  void testThatAreNative() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatAreNative();
    assertTrue(matcher.modifiersMatch(Modifier.NATIVE | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.STATIC | Modifier.TRANSIENT | Modifier.PROTECTED));
  }

  @Test
  void testThatAreNotNative() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatAreNotNative();
    assertTrue(matcher.modifiersMatch(Modifier.TRANSIENT | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.STATIC | Modifier.NATIVE | Modifier.PROTECTED));
  }

  @Test
  void testExceptionsAfterThatVisibility() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatArePrivate();
    assertThrows(GradleException.class, matcher::thatArePrivate);
    assertThrows(GradleException.class, matcher::thatArePackagePrivate);
    assertThrows(GradleException.class, matcher::thatAreProtected);
    assertThrows(GradleException.class, matcher::thatArePublic);
    assertThrows(GradleException.class, matcher::thatAreNotPublic);
  }

  @Test
  void testExceptionsAfterThatMisc() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatAreStatic();
    assertThrows(GradleException.class, matcher::thatAreAbstract);
    assertThrows(GradleException.class, matcher::thatAreNotAbstract);
    assertThrows(GradleException.class, matcher::thatAreStatic);
    assertThrows(GradleException.class, matcher::thatAreNotStatic);
    assertThrows(GradleException.class, matcher::thatAreVolatile);
    assertThrows(GradleException.class, matcher::thatAreNotVolatile);
    assertThrows(GradleException.class, matcher::thatAreTransient);
    assertThrows(GradleException.class, matcher::thatAreNotTransient);
    assertThrows(GradleException.class, matcher::thatAreNative);
    assertThrows(GradleException.class, matcher::thatAreNotNative);
  }

  @Test
  void testExceptionsAfterThatExclusions() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatAreNotStatic();
    assertThrows(GradleException.class, matcher::thatArePrivate);
    assertThrows(GradleException.class, matcher::thatArePackagePrivate);
    assertThrows(GradleException.class, matcher::thatAreProtected);
    assertThrows(GradleException.class, matcher::thatArePublic);
    assertThrows(GradleException.class, matcher::thatAreNotPublic);
    assertThrows(GradleException.class, matcher::thatAreStatic);
    assertThrows(GradleException.class, matcher::thatAreNotStatic);
    assertThrows(GradleException.class, matcher::thatAreVolatile);
    assertThrows(GradleException.class, matcher::thatAreNotVolatile);
    assertThrows(GradleException.class, matcher::thatAreTransient);
    assertThrows(GradleException.class, matcher::thatAreNotTransient);
    assertThrows(GradleException.class, matcher::thatAreNative);
    assertThrows(GradleException.class, matcher::thatAreNotNative);
  }

  @Test
  void testAndPrivate() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatAreStatic().andPrivate();
    assertTrue(matcher.modifiersMatch(Modifier.STATIC | Modifier.FINAL | Modifier.PRIVATE));
    assertFalse(matcher.modifiersMatch(Modifier.FINAL | Modifier.PRIVATE));
    assertFalse(matcher.modifiersMatch(Modifier.FINAL | Modifier.STATIC));
  }

  @Test
  void testAndPackagePrivate() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatAreStatic().andPackagePrivate();
    assertTrue(matcher.modifiersMatch(Modifier.STATIC | Modifier.FINAL));
    assertFalse(matcher.modifiersMatch(Modifier.STATIC | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.NATIVE));
  }

  @Test
  void testAndProtected() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatAreStatic().andProtected();
    assertTrue(matcher.modifiersMatch(Modifier.STATIC | Modifier.FINAL | Modifier.PROTECTED));
    assertFalse(matcher.modifiersMatch(Modifier.STATIC | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.FINAL | Modifier.PROTECTED));
  }

  @Test
  void testAndPublic() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatAreStatic().andPublic();
    assertTrue(matcher.modifiersMatch(Modifier.STATIC | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.STATIC | Modifier.FINAL | Modifier.PROTECTED));
    assertFalse(matcher.modifiersMatch(Modifier.FINAL | Modifier.PUBLIC));
  }

  @Test
  void testAndNotPublic() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatAreStatic().andNotPublic();
    assertTrue(matcher.modifiersMatch(Modifier.STATIC | Modifier.PRIVATE));
    assertTrue(matcher.modifiersMatch(Modifier.STATIC | Modifier.PROTECTED));
    assertTrue(matcher.modifiersMatch(Modifier.STATIC | Modifier.STRICT));
    assertFalse(matcher.modifiersMatch(Modifier.STATIC | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.INTERFACE | Modifier.PRIVATE));
    assertFalse(matcher.modifiersMatch(Modifier.FINAL | Modifier.PROTECTED));
    assertFalse(matcher.modifiersMatch(Modifier.TRANSIENT | Modifier.STRICT));
  }

  @Test
  void testAndAbstract() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatArePublic().andAbstract();
    assertTrue(matcher.modifiersMatch(Modifier.ABSTRACT | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.STATIC | Modifier.FINAL | Modifier.PROTECTED));
    assertFalse(matcher.modifiersMatch(Modifier.FINAL | Modifier.PUBLIC));
  }

  @Test
  void testAndNotAbstract() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatArePublic().andNotAbstract();
    assertTrue(matcher.modifiersMatch(Modifier.NATIVE | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.ABSTRACT | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.FINAL | Modifier.PROTECTED));
  }

  @Test
  void testAndStatic() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatArePublic().andStatic();
    assertTrue(matcher.modifiersMatch(Modifier.STATIC | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.STATIC | Modifier.FINAL | Modifier.PROTECTED));
    assertFalse(matcher.modifiersMatch(Modifier.FINAL | Modifier.PUBLIC));
  }

  @Test
  void testAndNotStatic() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatArePublic().andNotStatic();
    assertTrue(matcher.modifiersMatch(Modifier.NATIVE | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.STATIC | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.FINAL | Modifier.PROTECTED));
  }

  @Test
  void testAndVolatile() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatArePublic().andVolatile();
    assertTrue(matcher.modifiersMatch(Modifier.VOLATILE | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.STATIC | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.VOLATILE | Modifier.PROTECTED));
  }

  @Test
  void testAndNotVolatile() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatArePublic().andNotVolatile();
    assertTrue(matcher.modifiersMatch(Modifier.STATIC | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.VOLATILE | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.STATIC | Modifier.PROTECTED));
  }

  @Test
  void testAndTransient() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatArePublic().andTransient();
    assertTrue(matcher.modifiersMatch(Modifier.TRANSIENT | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.STATIC | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.TRANSIENT | Modifier.PROTECTED));
  }

  @Test
  void testAndNotTransient() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatArePublic().andNotTransient();
    assertTrue(matcher.modifiersMatch(Modifier.STATIC | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.TRANSIENT | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.STATIC | Modifier.PROTECTED));
  }

  @Test
  void testAndNative() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatArePublic().andNative();
    assertTrue(matcher.modifiersMatch(Modifier.NATIVE | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.STATIC | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.NATIVE | Modifier.PROTECTED));
  }

  @Test
  void testAndNotNative() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatArePublic().andNotNative();
    assertTrue(matcher.modifiersMatch(Modifier.STATIC | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.NATIVE | Modifier.FINAL | Modifier.PUBLIC));
    assertFalse(matcher.modifiersMatch(Modifier.STATIC | Modifier.PROTECTED));
  }

  @Test
  void testExceptionsAfterContradictingAnd() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatArePrivate().andAbstract().andStatic().andVolatile().andTransient().andNative();
    assertThrows(GradleException.class, matcher::andPrivate);
    assertThrows(GradleException.class, matcher::andPackagePrivate);
    assertThrows(GradleException.class, matcher::andProtected);
    assertThrows(GradleException.class, matcher::andPublic);
    assertThrows(GradleException.class, matcher::andNotPublic);
    assertThrows(GradleException.class, matcher::andNotAbstract);
    assertThrows(GradleException.class, matcher::andNotStatic);
    assertThrows(GradleException.class, matcher::andNotVolatile);
    assertThrows(GradleException.class, matcher::andNotTransient);
    assertThrows(GradleException.class, matcher::andNotNative);

    matcher = new AbstractModifierMatcherImpl()
            .thatAreNotStatic().andNotAbstract().andNotVolatile().andNotTransient().andNotNative();
    assertThrows(GradleException.class, matcher::andStatic);
    assertThrows(GradleException.class, matcher::andAbstract);
    assertThrows(GradleException.class, matcher::andVolatile);
    assertThrows(GradleException.class, matcher::andTransient);
    assertThrows(GradleException.class, matcher::andNative);
    
    matcher = new AbstractModifierMatcherImpl()
            .thatArePackagePrivate();
    assertThrows(GradleException.class, matcher::andNotPublic);
  }

  @Test
  void testPublicOrPrivate() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatArePublic().orPrivate();
    assertTrue(matcher.modifiersMatch(Modifier.NATIVE | Modifier.FINAL | Modifier.PUBLIC));
    assertTrue(matcher.modifiersMatch(Modifier.STATIC | Modifier.STRICT | Modifier.PRIVATE));
    assertFalse(matcher.modifiersMatch(Modifier.VOLATILE | Modifier.PROTECTED));
    assertFalse(matcher.modifiersMatch(Modifier.FINAL));
  }

  @Test
  void testPackagePrivateOrPrivate() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatArePackagePrivate().orPrivate();
    assertTrue(matcher.modifiersMatch(Modifier.NATIVE | Modifier.FINAL));
    assertTrue(matcher.modifiersMatch(Modifier.STATIC | Modifier.STRICT | Modifier.PRIVATE));
    assertFalse(matcher.modifiersMatch(Modifier.VOLATILE | Modifier.PROTECTED));
    assertFalse(matcher.modifiersMatch(Modifier.FINAL | Modifier.PUBLIC));
  }

  @Test
  void testPublicOrPackagePrivate() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatArePublic().orPackagePrivate();
    assertTrue(matcher.modifiersMatch(Modifier.NATIVE | Modifier.FINAL | Modifier.PUBLIC));
    assertTrue(matcher.modifiersMatch(Modifier.STATIC | Modifier.STRICT));
    assertFalse(matcher.modifiersMatch(Modifier.VOLATILE | Modifier.PROTECTED));
    assertFalse(matcher.modifiersMatch(Modifier.FINAL | Modifier.PRIVATE));
  }

  @Test
  void testPublicOrProtected() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatArePublic().orProtected();
    assertTrue(matcher.modifiersMatch(Modifier.NATIVE | Modifier.FINAL | Modifier.PUBLIC));
    assertTrue(matcher.modifiersMatch(Modifier.STATIC | Modifier.STRICT | Modifier.PROTECTED));
    assertFalse(matcher.modifiersMatch(Modifier.VOLATILE | Modifier.PRIVATE));
    assertFalse(matcher.modifiersMatch(Modifier.FINAL));
  }

  @Test
  void testPackagePrivateOrProtected() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatArePackagePrivate().orProtected();
    assertTrue(matcher.modifiersMatch(Modifier.NATIVE | Modifier.FINAL));
    assertTrue(matcher.modifiersMatch(Modifier.STATIC | Modifier.STRICT | Modifier.PROTECTED));
    assertFalse(matcher.modifiersMatch(Modifier.VOLATILE | Modifier.PRIVATE));
    assertFalse(matcher.modifiersMatch(Modifier.FINAL | Modifier.PUBLIC));
  }

  @Test
  void testProtectedOrPublic() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatAreProtected().orPublic();
    assertTrue(matcher.modifiersMatch(Modifier.NATIVE | Modifier.FINAL | Modifier.PUBLIC));
    assertTrue(matcher.modifiersMatch(Modifier.STATIC | Modifier.STRICT | Modifier.PROTECTED));
    assertFalse(matcher.modifiersMatch(Modifier.VOLATILE | Modifier.PRIVATE));
    assertFalse(matcher.modifiersMatch(Modifier.FINAL));
  }

  @Test
  void testPackagePrivateOrPublic() {
    AbstractModifierMatcher matcher = new AbstractModifierMatcherImpl()
            .thatArePackagePrivate().orPublic();
    assertTrue(matcher.modifiersMatch(Modifier.NATIVE | Modifier.FINAL | Modifier.PUBLIC));
    assertTrue(matcher.modifiersMatch(Modifier.STATIC | Modifier.STRICT));
    assertFalse(matcher.modifiersMatch(Modifier.VOLATILE | Modifier.PRIVATE));
    assertFalse(matcher.modifiersMatch(Modifier.FINAL | Modifier.PROTECTED));
  }

  public static class AbstractModifierMatcherImpl extends AbstractModifierMatcher {

    @Override
    public boolean modifiersMatch(int modifiers) {
      return super.modifiersMatch(modifiers);
    }

  }

}
