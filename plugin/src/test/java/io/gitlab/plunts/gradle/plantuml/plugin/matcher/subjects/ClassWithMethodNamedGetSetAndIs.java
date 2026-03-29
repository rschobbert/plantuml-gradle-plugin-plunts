package io.gitlab.plunts.gradle.plantuml.plugin.matcher.subjects;

import java.util.HashMap;
import java.util.Map;

public class ClassWithMethodNamedGetSetAndIs {

  private Map<String, String> map = new HashMap<>();

  public String get(String someId) {
    return map.get(someId);
  }

  public boolean is(String someId) {
    return map.containsKey(someId);
  }

  public void set(String someId, String someValue) {
    map.put(someId, someValue);
  }

}
