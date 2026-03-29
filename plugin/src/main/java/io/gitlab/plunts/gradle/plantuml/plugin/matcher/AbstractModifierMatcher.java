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
import org.gradle.api.InvalidUserDataException;

/**
 * Abstract base class for matchers that test elements with modifiers.
 */
public abstract class AbstractModifierMatcher extends AbstractNameMatcher {

  private static final int VISIBILITY_MODIFIERS = Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC;
  private int includedVisibilityModifiers = 0;
  private int includedMiscModifiers = 0;
  private int excludedModifiers = 0;

  @Override
  public AbstractModifierMatcher withName(String name) {
    super.withName(name);
    return this;
  }

  @Override
  public AbstractModifierMatcher withNameLike(String glob) {
    super.withNameLike(glob);
    return this;
  }

  public AbstractModifierMatcher thatArePrivate() {
    if (includedVisibilityModifiers != 0 || excludedModifiers != 0) {
      throw new InvalidUserDataException("thatArePrivate can only be called as first modifier");
    }
    includedVisibilityModifiers = Modifier.PRIVATE;
    return this;
  }

  public AbstractModifierMatcher thatArePackagePrivate() {
    if (includedVisibilityModifiers != 0 || excludedModifiers != 0) {
      throw new InvalidUserDataException("thatArePackagePrivate can only be called as first modifier");
    }
    excludedModifiers = VISIBILITY_MODIFIERS;
    return this;
  }

  public AbstractModifierMatcher thatAreProtected() {
    if (includedVisibilityModifiers != 0 || excludedModifiers != 0) {
      throw new InvalidUserDataException("thatAreProtected can only be called as first modifier");
    }
    includedVisibilityModifiers = Modifier.PROTECTED;
    return this;
  }

  public AbstractModifierMatcher thatArePublic() {
    if (includedVisibilityModifiers != 0 || excludedModifiers != 0) {
      throw new InvalidUserDataException("thatArePublic can only be called as first modifier");
    }
    includedVisibilityModifiers = Modifier.PUBLIC;
    return this;
  }

  public AbstractModifierMatcher thatAreNotPublic() {
    if (includedVisibilityModifiers != 0 || excludedModifiers != 0) {
      throw new InvalidUserDataException("thatArePublic can only be called as first modifier");
    }
    excludedModifiers = Modifier.PUBLIC;
    return this;
  }

  public AbstractModifierMatcher thatAreAbstract() {
    if (includedMiscModifiers != 0 || excludedModifiers != 0) {
      throw new InvalidUserDataException("thatAreAbstract can only be called as first modifier");
    }
    includedMiscModifiers = Modifier.ABSTRACT;
    return this;
  }

  public AbstractModifierMatcher thatAreNotAbstract() {
    if (includedMiscModifiers != 0 || excludedModifiers != 0) {
      throw new InvalidUserDataException("thatAreNotAbstract can only be called as first modifier");
    }
    excludedModifiers = Modifier.ABSTRACT;
    return this;
  }

  public AbstractModifierMatcher thatAreStatic() {
    if (includedMiscModifiers != 0 || excludedModifiers != 0) {
      throw new InvalidUserDataException("thatAreStatic can only be called as first modifier");
    }
    includedMiscModifiers = Modifier.STATIC;
    return this;
  }

  public AbstractModifierMatcher thatAreNotStatic() {
    if (includedMiscModifiers != 0 || excludedModifiers != 0) {
      throw new InvalidUserDataException("thatAreNotStatic can only be called as first modifier");
    }
    excludedModifiers = Modifier.STATIC;
    return this;
  }

  public AbstractModifierMatcher thatAreVolatile() {
    if (includedMiscModifiers != 0 || excludedModifiers != 0) {
      throw new InvalidUserDataException("thatAreVolatile can only be called as first modifier");
    }
    includedMiscModifiers = Modifier.VOLATILE;
    return this;
  }

  public AbstractModifierMatcher thatAreNotVolatile() {
    if (includedMiscModifiers != 0 || excludedModifiers != 0) {
      throw new InvalidUserDataException("thatAreNotVolatile can only be called as first modifier");
    }
    excludedModifiers = Modifier.VOLATILE;
    return this;
  }

  public AbstractModifierMatcher thatAreTransient() {
    if (includedMiscModifiers != 0 || excludedModifiers != 0) {
      throw new InvalidUserDataException("thatAreTransient can only be called as first modifier");
    }
    includedMiscModifiers = Modifier.TRANSIENT;
    return this;
  }

  public AbstractModifierMatcher thatAreNotTransient() {
    if (includedMiscModifiers != 0 || excludedModifiers != 0) {
      throw new InvalidUserDataException("thatAreNotTransient can only be called as first modifier");
    }
    excludedModifiers = Modifier.TRANSIENT;
    return this;
  }

  public AbstractModifierMatcher thatAreNative() {
    if (includedMiscModifiers != 0 || excludedModifiers != 0) {
      throw new InvalidUserDataException("thatAreNative can only be called as first modifier");
    }
    includedMiscModifiers = Modifier.NATIVE;
    return this;
  }

  public AbstractModifierMatcher thatAreNotNative() {
    if (includedMiscModifiers != 0 || excludedModifiers != 0) {
      throw new InvalidUserDataException("thatAreNotNative can only be called as first modifier");
    }
    excludedModifiers = Modifier.NATIVE;
    return this;
  }

  public AbstractModifierMatcher andPrivate() {
    if ((includedVisibilityModifiers & VISIBILITY_MODIFIERS) != 0
            || (excludedModifiers & VISIBILITY_MODIFIERS) != 0) {
      throw new InvalidUserDataException("private can't be conjugate with other visibility modifiers");
    }
    includedVisibilityModifiers |= Modifier.PRIVATE;
    return this;
  }

  public AbstractModifierMatcher andPackagePrivate() {
    if ((includedVisibilityModifiers & VISIBILITY_MODIFIERS) != 0
            || (excludedModifiers & VISIBILITY_MODIFIERS) != 0) {
      throw new InvalidUserDataException("package-private can't be conjugate with other visibility modifiers");
    }
    excludedModifiers |= VISIBILITY_MODIFIERS;
    return this;
  }

  public AbstractModifierMatcher andProtected() {
    if ((includedVisibilityModifiers & VISIBILITY_MODIFIERS) != 0
            || (excludedModifiers & VISIBILITY_MODIFIERS) != 0) {
      throw new InvalidUserDataException("protected can't be conjugate with other visibility modifiers");
    }
    includedVisibilityModifiers |= Modifier.PROTECTED;
    return this;
  }

  public AbstractModifierMatcher andPublic() {
    if ((includedVisibilityModifiers & VISIBILITY_MODIFIERS) != 0
            || (excludedModifiers & VISIBILITY_MODIFIERS) != 0) {
      throw new InvalidUserDataException("public can't be conjugate with other visibility modifiers");
    }
    includedVisibilityModifiers |= Modifier.PUBLIC;
    return this;
  }

  public AbstractModifierMatcher andNotPublic() {
    if ((includedVisibilityModifiers & VISIBILITY_MODIFIERS) != 0
            || (excludedModifiers & VISIBILITY_MODIFIERS) != 0) {
      throw new InvalidUserDataException("public can't be conjugate with other visibility modifiers");
    }
    excludedModifiers |= Modifier.PUBLIC;
    return this;
  }

  public AbstractModifierMatcher andAbstract() {
    if ((excludedModifiers & Modifier.ABSTRACT) != 0) {
      throw new InvalidUserDataException("abstract already excluded before");
    }
    includedMiscModifiers |= Modifier.ABSTRACT;
    return this;
  }

  public AbstractModifierMatcher andNotAbstract() {
    if ((includedMiscModifiers & Modifier.ABSTRACT) != 0) {
      throw new InvalidUserDataException("abstract already included before");
    }
    excludedModifiers |= Modifier.ABSTRACT;
    return this;
  }

  public AbstractModifierMatcher andStatic() {
    if ((excludedModifiers & Modifier.STATIC) != 0) {
      throw new InvalidUserDataException("static already excluded before");
    }
    includedMiscModifiers |= Modifier.STATIC;
    return this;
  }

  public AbstractModifierMatcher andNotStatic() {
    if ((includedMiscModifiers & Modifier.STATIC) != 0) {
      throw new InvalidUserDataException("static already included before");
    }
    excludedModifiers |= Modifier.STATIC;
    return this;
  }

  public AbstractModifierMatcher andVolatile() {
    if ((excludedModifiers & Modifier.VOLATILE) != 0) {
      throw new InvalidUserDataException("volatile already excluded before");
    }
    includedMiscModifiers |= Modifier.VOLATILE;
    return this;
  }

  public AbstractModifierMatcher andNotVolatile() {
    if ((includedMiscModifiers & Modifier.VOLATILE) != 0) {
      throw new InvalidUserDataException("volatile already included before");
    }
    excludedModifiers |= Modifier.VOLATILE;
    return this;
  }

  public AbstractModifierMatcher andTransient() {
    if ((excludedModifiers & Modifier.TRANSIENT) != 0) {
      throw new InvalidUserDataException("transient already excluded before");
    }
    includedMiscModifiers |= Modifier.TRANSIENT;
    return this;
  }

  public AbstractModifierMatcher andNotTransient() {
    if ((includedMiscModifiers & Modifier.TRANSIENT) != 0) {
      throw new InvalidUserDataException("transient already included before");
    }
    excludedModifiers |= Modifier.TRANSIENT;
    return this;
  }

  public AbstractModifierMatcher andNative() {
    if ((excludedModifiers & Modifier.NATIVE) != 0) {
      throw new InvalidUserDataException("native already excluded before");
    }
    includedMiscModifiers |= Modifier.NATIVE;
    return this;
  }

  public AbstractModifierMatcher andNotNative() {
    if ((includedMiscModifiers & Modifier.NATIVE) != 0) {
      throw new InvalidUserDataException("native already included before");
    }
    excludedModifiers |= Modifier.NATIVE;
    return this;
  }

  public AbstractModifierMatcher orPrivate() {
    if ((excludedModifiers & VISIBILITY_MODIFIERS) == 0) {
      includedVisibilityModifiers |= Modifier.PRIVATE;
    }
    excludedModifiers &= ~Modifier.PRIVATE;
    return this;
  }

  public AbstractModifierMatcher orPackagePrivate() {
    excludedModifiers |= VISIBILITY_MODIFIERS & ~includedVisibilityModifiers;
    includedVisibilityModifiers = 0;
    return this;
  }

  public AbstractModifierMatcher orProtected() {
    if ((excludedModifiers & VISIBILITY_MODIFIERS) == 0) {
      includedVisibilityModifiers |= Modifier.PROTECTED;
    }
    excludedModifiers &= ~Modifier.PROTECTED;
    return this;
  }

  public AbstractModifierMatcher orPublic() {
    if ((excludedModifiers & VISIBILITY_MODIFIERS) == 0) {
      includedVisibilityModifiers |= Modifier.PUBLIC;
    }
    excludedModifiers &= ~Modifier.PUBLIC;
    return this;
  }

  protected boolean modifiersMatch(int modifiers) {
    final boolean miscIncluded = (modifiers & includedMiscModifiers) == includedMiscModifiers;
    final boolean visibilityIncluded = includedVisibilityModifiers == 0 || (modifiers & includedVisibilityModifiers) != 0;
    final boolean notExcluded = (modifiers & excludedModifiers) == 0;
    return miscIncluded && visibilityIncluded && notExcluded;
  }

}
