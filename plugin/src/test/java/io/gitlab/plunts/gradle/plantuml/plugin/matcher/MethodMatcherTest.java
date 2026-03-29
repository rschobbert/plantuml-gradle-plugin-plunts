package io.gitlab.plunts.gradle.plantuml.plugin.matcher;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.gitlab.plunts.gradle.plantuml.plugin.matcher.subjects.ClassWithMethodNamedGetSetAndIs;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class MethodMatcherTest {

    @Test
    void testWithMethodNamesGetSetAndIs() {
        ClassInfo classInfo = new ClassGraph()
                    .enableAllInfo()
                    .acceptClasses(ClassWithMethodNamedGetSetAndIs.class.getName())
                    .scan()
                    .getClassInfo(ClassWithMethodNamedGetSetAndIs.class.getName());
        MethodMatcher matcher = new MethodMatcher().thatAreNotAccessors();

        assertThat(matcher.test(classInfo.getMethodInfo("get").get(0)), is(true));
        assertThat(matcher.test(classInfo.getMethodInfo("set").get(0)), is(true));
        assertThat(matcher.test(classInfo.getMethodInfo("is").get(0)), is(true));
    }
}
