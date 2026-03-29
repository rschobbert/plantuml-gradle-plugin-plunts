# plantuml-gradle-plugin

Gradle plugin to build PlantUML diagrams from code (for living and up-to-date documentation).

## Activation

Add the plugin to the `build.gradle` of your project:

```groovy
plugins {
  id("io.gitlab.plunts.plantuml") version "2.4.0"
}
```

## Configuration

After the plugin was added to your project, you can configure as many diagrams as you like using the following extension:

```groovy
classDiagrams {
  diagram {
    name("Visualise Package")
    include(packages().withName("x.y.z"))
    writeTo(file("xyz.puml"))
  }
}
```

The above example writes a single diagram of a full package to a file.
This can be highly customized to create as many views on your code as necessary.
The following sections describe the full range of keywords that can be used.
For a "learning by example" experience, see the [example project](examples/app/build.gradle).

By default, all source sets that do not contain "test" are scanned.
It is possible to change this by defining a custom list of names or references:


```groovy
classDiagrams {
  sourceSets = ["main", "test"] // variant 1: use names
  sourceSets = [project.sourceSets.main, project.sourceSets.test] // variant 2: use references
  // ... diagramms
}
```

If you want to apply some configuration to **all** diagrams, you can use the `defaults` block.
Those will also be applied to sub-projects when they don't opt-out with `dontInheritDefaults`.

```groovy
classDiagrams {
  defaults {
    exclude(classes().annotatedWith("lombok.Generated"))
  }
}
```

### Include / Exclude

Each diagram **must** include at least one package or class, which is done using the `include` keyword followed by either `classes()` or `packages()`.
The name of the package/class is either specified exactly using `.withName("x.y.z")` or via glob using `.withNameLike("x.y.*")`.
Conversely the `exclude` keyword can be used to remove unwanted elements from the diagram.

Starting with the included classes, other elements can be included (or excluded) as well:

* `superclasses()` includes all superclasses of already included classes
* `subclasses()` includes all subclasses of already included classes
* `interfaces()` includes all implemented interfaces of already included classes
* `referencedClasses()` includes all classes that are used in fields of already included classes
* `fields()` is used to control which fields are shown (default: public)
* `enumValues()` is used to control which enum values are shown (default: all)
* `methods()` is used to control which fields are shown (default: public)

Each element can be further tailored using the following methods:

* `insideOfProject()` and `outsideOfProject()`
  * Match elements that are part of your project.
    For fields and methods this refers to the declaring class.
    If the project has subprojects, they are considered to be *inside* of the parent project.
  * Applicable to: packages, classes, fields and methods
* `withName(String)` and `withNameLike(String)`
  * Match elements by their name.
    For classes, the full name is tested.
  * Applicable to: packages, classes, fields and methods
* `nonRecursive()` and `maxDepth(int)`
  * *Sub-Packages* are matched by default, meaning `packages().withName("x.y")` also matches the package "x.y.z".
    Use this configuration to adjust this behaivour.
* `withParentClass()` and `withoutParentClass()`
  * Configure whether or not to include inner *classes* (i.e. parents with parent classes).
* `annotatedWith(String)`
  * Match elements that are annotated with the given annotation (full qualified).
  * Applicable to: classes, fields and methods
* `thatAreXY()`, `andAreXY()` and `orAreXY()`
  * Match elements having the given modifiers.
    Must be started with `thatAre` and can then be chained with `andAre`.
    Visibility modifiers can also be combined using `orAre`.
    To inverse the meaning, use the `not` keyword, e.g. `thatAreNotStatic()` or `thatAreNotPublic()`.
  * Supported modifiers: `public`, `protected`, `packagePrivate`, `private`, `static`, `abstract`, `final`, `volatile`, `transient`
  and `native`.
  * Applicable to: classes, fields and methods
  * Examples:
    * `methods().thatAreStatic().andPrivate()`
    * `fields().thatArePublic().orProtected()`
* `thatAreAccessors()` and `thatAreNotAccessors()`
  * Match *methods* that are accessors (getter/setter) for a field.
* `thatHaveAccessors()` and `thatDontHaveAccessors()`
  * Match *fields* that have at least one accessor method (getter/setter).
* `of(String)`, `of(classes())`, `of(packages())`
  * For fields/methods: match members inside the specified class/package.
  * For superclasses/subclasses/interfaces/referencedClasses: match classes that are referenced by the given class/package.
* `inPackage(String)`
  * Finding other classes isn't easy, so you may have to use this additional call to hint where they may be.
  * Applicable to: superclasses, subclasses, interfaces, referencedClasses

### Override Relations

The plugin tries it's best to use the correct arrows to show relations between classes.
This explicitly includes support for annotations used by the Java Persistence and Validation API.
If the result is not what you want to see, you can use the `override` keyword to change it.
Two kinds of relations can be overridden:

* `association()` is a relation determined by the fields of the class.
* `extension()` is the relation to superclasses and interfaces.

In both cases, the following methods are used to configure the override:

* `from(String)` and `to(String)` are used to define the classes of the relationship that shall be changed.
  Both accept glob patterns.
* `with(String)` overrides the whole relation. You may want to use it for heavy customizing needs.
* `withSourceMultiplicity(String)` and `withTargetMultiplicity(String)` override the multiplicity (e.g. "0..*").
* `withSourceArrow(String)` and `withTargetArrow(String)` changes the arrow only (e.g. "<" or ">").
* `withLineCharacter(String)` changes the line character (e.g. "-" or ".").
* `withStyle(String)` changes the style of the arrow (e.g. "down").
* `withLabel(String)` changes the label of the relation (never set automatically).

### Add or Remove Relations

There are cases where relations are not discovered correctly.
For example, the plugin can not yet detect indirect relations when the middle-class (e.g. a container POJO) is excluded.
On the other hand, you might want to remove a relation from the diagram, because they are inrelevant or confusing in the context of the documentation.
The keywords `add` and `remove` can be used in combination with the `association` syntax above to achieve this.

```groovy
add(association().from("OneClass").to("AnotherClass"))
```

### Notes

Notes can be added on classes, methods and fields using the following syntax.
Due to limitations in PlantUML, only notes on **classes** can be on top or bottom.
Left and right are always available.

```groovy
note("This note is above of the class foo.Bar").topOf(classes().withName("foo.Bar"))
note("This note is left of all fields with name foo").leftOf(fields().withName("foo"))
note("This note is right of all methods with name bar").rightOf(methods().withName("bar"))
```

### Grouping Classes with "Together"

Sometimes, the default layout of PlantUML is not perfect.
While it can help to adjust [relations](#override-relations) or even [add](#add-or-remove-relations) hidden ones, another approach offered is the `together` keyword.
There are two approaches, which can be mixed if required:

1. Include all classes as described above, then group as many of them as you like.
   By using the matcher pattern, it is not necessary to list all of them individually.
   Surplus classes can be removed by prepending `except`.

```groovy
classDiagram {
  include(packages().withName("x.y.z"))
  together {
    classes().thatArePackagePrivate()
    except(classes("*.FooBar"))
  }
}
```

2. Alternativly, the `include` statement can be used inside the `together` block as well.
   This includes the given classes in the diagram and assigns them to the group.

```groovy
classDiagram {
  together {
    include(classes("x.y.z.Foo"))
    include(classes("x.y.z.Bar"))
  }
  together {
    include(classes("x.y.z.Lorem"))
    include(classes("x.y.z.Ipsum"))
  }
}
```

### Styling

The `style` section can be used to customize the visuals of the generated diagram.
This can be done per individual diagram, but is recommended to be done in the `default` block to ensure a common look.
It mainly uses the same names as PlantUML does: `include`, `define`, `theme`, `skinparam` and `hide`/`show` are currently available.
Additionally, you can use the special methods `hidePackages` to disable rendering of package names and boxes, or `useIntermediatePackages` to show every intermediate package even if it does not contain classes (which is the default in newer PlantUML versions, but suppressed by this plugin).

If you want to add colors or stereotypes to your packages or classes, you can do so with `addStyle`:

```groovy
addStyle("#ff0000").to(packages().withName("foo.bar"))
addStyle("<< (S,#FF7700) Singleton >>").to(classes().withNameLike("io.gitlab.plunts.gradle.plantuml.examples.app.*"))
```

### Output

Last but not least you have to configure where to store the diagram.
The `writeTo` command accepts any `file` and will overwrite it's content.
Alternativly, you can use `insertInto` to inject the diagram into another file, e.g. Markdown or AsciiDoc.
Use the diagram name to mark the location with a PlantUML comment:

```
'DiagramName
@startuml
@enduml
```

If the marker can't be found, the task will generate an error message with this exact instructions.
The [last diagram](examples/app/build.gradle) of the [example project](examples/app/README.md) shows how it is done in practice.

You can also directly render the diagram to a png or svg image.
To do this, use the `renderTo` command and specify a file with your desired extension.
By default, the plugin will use the public PlantUML server to do the actual rendering.
You can change the server URL using the `plantumlServer` property:

```groovy
classDiagrams {
  plantumlServer = "http://localhost:8081"
  diagram {
  }
}
```

Alternativly, you can choose to render on your local maschine.
For this, you have to define three things: a new gradle configuration, a dependency to the PlantUML library and an instruction for the plugin to use it:

```groovy
configurations {
  plantuml
}

dependencies {
  plantuml("net.sourceforge.plantuml:plantuml:1.2023.10")
  // if you want to render PDF, you'll also need the following two dependencies
  plantuml("org.apache.xmlgraphics:fop:2.9")
  plantuml("org.apache.xmlgraphics:batik-all:1.17")
}

classDiagrams {
  plantumlServer = null // optional, avoids surprises on configuration errors
  renderClasspath(project.configurations.plantuml)
  diagram {
  }
}
```

### Package Diagrams

For a coarse overview of your architecture, you may want to only show relations between the packages, not classes.
This can be done by defining a `packageDiagram`, which supports most of the options shown above.
If you use it, you can also change `diagram` to `classDiagram` to be more exlicit.

```groovy
classDiagrams {
  packageDiagram {
    name("Visualise Package")
    include(packages().withName("x.y.z"))
    writeTo(file("xyz_packages.puml"))
  }
  classDiagram {
    name("Visualise Classes in Package")
    include(packages().withName("x.y.z"))
    writeTo(file("xyz_classes.puml"))
  }
}
```

## Usage

Run the task `generateClassDiagrams` using gradle:

```sh
./gradlew generateClassDiagrams
```

## Development

The gradle project is configured to use itself to ensure consistency.
As a caveat, this requires to publish the current snapshot version to the local maven repository before development.
This is done by executing the following command:

```sh
./gradlew --configure-on-demand --build-cache :plantuml-gradle-plugin:publishToMavenLocal
```

## Credits

This project started as a fork of a plugin by [RoRoche](https://github.com/RoRoche/plantuml-gradle-plugin), which is unmaintained for a while now.
It ended up being a complete rewrite, but takes a lot of inspiration from it.
As the original, this is still build on top of [ClassGraph](https://github.com/classgraph/classgraph).
