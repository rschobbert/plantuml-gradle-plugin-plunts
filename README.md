Clone of the plantuml-gradle-plugin currently hosted on gitlab by user plunts. This repo is a clone for me to try out different things...

[The original (and active) repository is hosted on gitlab](https://gitlab.com/Plunts/plantuml-gradle-plugin)


I registered on gitlab to be able to submit merge requests to the owner of the repo, but don't want to scatter
my repositories around. That's why I will keep my repository here.


#### The following things were added / improved

* added ability to set diagram direction to 'left to right direction'
* added possibility to write package relation, which can be used to influence the layout
* added two separate buildfiles, to 
    * create delomboked sources, for those who want to work with eclipse and don't want to install the lombok eclipse plugin
        * the `build-delombok.gradle` buildfile is called, by calling 
          the build with the appropriate task. At the time of writing that is 
          the `delombok` task.
        * the delomboked sources are put in a 'src-delomboked' folder
          as sibling next to each 'src' folder
        * currently, to use you the delomboked sources, you either have to
          copy the files over the 'src' folder, or you have to modify the sourceSets
          
        hopefully the plugin owner will agree to remove the lombok stuff,
           
    * generate class diagrams of the plugin sources, for a better architectural understanding of the plugin
        * by calling `generatePluginDiagrams` task, you can create diagrams of the plugin sources
          themselves. This can be helpful in understanding the plugin. The definition of the diagrams 
          is in the buildfile 'build-plugin-diagrams.gradle'. Together with the diagrams, an html
          and a markdown file is generated. The html file for local viewing in a browser and the 
          [markdown file](doc/generated/diagrams.md) for online viewing via e.g. github.


Currently the first feature (using directionLR() to set the diagram direction) 
has been sent as merge-request to the plugin owner on gitlab. I will then 
also try to get him merge my other efforts. I rather not want another plugin derivate
around. But we'll see how that evolves ...


#### Building this plugin and local maven installation of the plugin

A little warning up front: I did not change the maven coordinates of this plugin (of course not).
That means, depending on the version you set, doing a local publish might (locally)
overwrite the original plugin. That is nothing serious, but it may lead to a short
confusion, on which version of the plugin is running at the moment. Cleaning up the 
local maven repository in your home directory, can help.

In case you want to use this version of the plugin (because features have not yet been 
merged, or you want to create your own locally modified version of the plugin), you can
publish the plugin to your local maven repository with the command (on linux):

`./gradlew --build-cache publishToMavenLocal`

or the longer version of the command:

`./gradlew --configure-on-demand --build-cache :plantuml-gradle-plugin:publishToMavenLocal`


Doing a clean build (without publishing) is also possible, but beware that (some) tests fail if you 
omit the `--build-cache` argument (at least that's what happened to me, and it took me some time
to find the cause):

`./gradlew --build-cache clean build`



#### getting rid of lombok

If you rather want to use sources without lombok, you can use the `delombok` task, which writes sibling
directories 'src-delomboked' next to each 'src' folder. 

In case you use **eclipse** (or **SpringToolSuite** ) just refresh your gradle project, 
and it will take the 'src-delomboked' folders instead of 'src'.

The magic happens in `build-alternative.gradle` which is used in case the 'src-delomboked' folders
exist:

```
allprojects {
    apply plugin: 'eclipse'
    
    ...
    
    if (project.name in [ 'plantuml-gradle-plugin', 'app', 'dto' ] ) {
        apply plugin: 'java'
        
        sourceSets { // if delomboked sources have been generated, use these in the sourcesets
            main.java.srcDirs = [ file('src-delomboked').isDirectory() ? 'src-delomboked/main/java' : 'src/main/java' ]
            test.java.srcDirs = [ file('src-delomboked').isDirectory() ? 'src-delomboked/test/java' : 'src/test/java' ]
        }
    }
    
    ...
}
```
This `allprojects` block is executed for all projects and applies the eclipse plugin to all projects.
It then applies the java plugin for the three java projects 'plantuml-gradle-plugin', 'app' and 'dto',
and decides how to configure the sourceSets. In case it finds a 'src-delomboked' folder, it uses that,
otherwise the 'src' folder.

This means that in eclipse **and** on the commandline you use the 'src-delomboked' folders. So calling
`./gradlew build` will also use the delomboked sources, if they are there.

That way, you won't get any compile errors in eclipse if you don't have the lombok plugin installed.

