# Bezirk 

################################ DELETE THIS LATER ######################
remove and organize the platform build releated references
#########################################################################

## System Enivornment
### OSX
Setup enivornment variable DYLD_LIBRARY_PATH
1. Go to home directory
    ```
    cd ~
    ```
2. if file ‘.bash_profile’ does not exist create it 
    ```
    touch .bash_profile
    ```
3. open file to edit
    ```
    vim .bash_profile
    ```
4. add path to the osx native libraries will be extracted at /[tmpdir]/lib-zeromq-bin/. Example:  /tmp/lib-zeromq-bin/
        or edit run configuration of each application /tests and add -Djava.libarary.path="/tmp/lib-zeromq-bin/"
    ``` BELOW STEP IS NOT NEEDED. verify and remove
    export DYLD_LIBRARY_PATH="/Users/<username>/Documents/platform/Middleware/java/libraries/comms-zyre-jni/dist/native/osx”
    ```
    [Note : not needed for command line build / run. since the path is fixed by gradle script. needed only to run java build test code from android studio]
5. save the file and restart the terminal. Run command:
    ```
    export
    ```
    ensure the variable DYLD_LIBRARY_PATH is set to the right path
6. If Android Studio is already running, restart in order to load the variable
### Linux
Same as OSX except step4: use variable LD_LIBRARY_PATH instead of DYLD_LIBRARY_PATH
### Windows 7. 64 bit
1. Add the absolute path C:\Users\<username>\AppData\Local\Temp\lib-zeromq-bin to the system Path variable
    or
    edit run configuration of each java applications /tests [Middleware->java->testApp] and add -Djava.libarary.path="C:\Users\<username>\AppData\Local\Temp\lib-zeromq-bin"
    as a VM options
    ```
    C:\Users\<username>\AppData\Local\Temp\lib-zeromq-bin
    ```
    [Note : not needed for command line build / run. since the path is fixed by gradle script. needed only to run java build test code from android studio]
2. If using cmd, run command:
    ```
    set
    ```
    ensure the variable PATH contains the added path

    NOTE: restart cmd if already open
3. If using git-bash, run command:
    ```
    export
    ```
    ensure the variable PATH contains the added path
    
    NOTE: restart git-bash if already open
4. If Android Studio is already running, restart in order to load the variable
## Running builds

Since artifacts are currently not published to maven/nexus, use the following commandline approach to build projects.
1) Inside Platform/Middleware/init.gradle
    ```
    gradle.ext.execute = 'core'
    ```
2) Execute from within Platform/Middleware => Builds core platform projects and publishes artifacts to local maven repo
    ```
    gradle --init-script init.gradle clean build publishToMavenLocal
    ```
3) Change value of execute inside Platform/Middleware/init.gradle
    ```
    gradle.ext.execute = 'all'
    ```
4) Execute from within the Platform/Middleware => Builds java & android platform projects and publishes artifacts to local maven repo
    ```
    gradle --init-script init.gradle clean build publishToMavenLocal
    ```
    *Note: publishToMavenLocal in step 4 is only required if artifacts from java/android are to be used in other projects like examples/zirks.*

## Proposed directory structure *(experimental)*
Bezirk *(root/parent directory of all projects)*
* middleware *(only platform related projects)*
    * core *(previously named as Java-Common)*
        * build.gradle -> core specific build configurations
    * java *(previously named as Java-Build)*
        * build.gradle -> java specific build configurations
    * android *(previously names as Android-Build)*
        * build.gradle -> android specific build configurations
    * settings.gradle -> contains list of all modules to be include from core, java and android
    * build.gradle -> common configurations across modules/group of modules 
* core zirks
    * wipin
        * core
        * java
        * android
    * uProxy
        * core
        * java
        * android
    * settings.gradle -> contains list of all modules to be include from wipin, uproxy. *(IMPORTANT: This can be changed going forward such that wipin and uproxy have their own settings.gradle and build configurations)*
    * build.gradle -> common configurations across modules/group of modules 
* examples
    * xlight
        * core
        * java
        * android
    * party
        * core
        * java
        * android
    * ...
    * ...
    * settings.gradle -> contains list of all modules to be include from xlight, party, other examples. *(IMPORTANT: This can be changed going forward such that each major show-case application/example has its own settings.gradle and build configurations)*
    * build.gradle -> common configurations across modules/group of modules 
* bezirk helper files/configurations. For instance, licensing, gradle helper files required across middleware, core-zirks and examples.

## Approach
* Migrate files from existing projects to respective directories to ensure git tracking for future references.
* Adding gradle tasks for building, publishing artifacts, pmd, static analysis, other coverage criterias.
* Re-evaluating android buildtool versions, gradle versions, supported android-platforms.
* Once folder restructuring is completed, package renaming to be performed.
* Source code changes to rename existing modules/variables.
* Removing unnecssary modules.
* Improving dependency management, using stable artifacts from repositories for both internal and external dependencies.
For instance, Define compile time and runtime jars

    * *core-api.jar* : Compile time dependency 
    * *core-com.bezirk.impl.jar* : Runtime dependency
    
    OR
    
    Define just one jar *core.jar* and support with documentation only for the API classes. [Approach used by Sun]
## Tracking files for their commit history

```
git log --follow filePath
```
*NOTE: File history might not be visible in stash/repository directly*

## For core-zirks & show-case application developers
* To continue to use your existing projects, point to artifacts of version '2.0.1' instead of projects from Java-Common, Java-Build & Android-Build.
* Migrate projects to the new structure.

# Contact
In case of issues/improvements/suggestions regarding the proposed structure, please contact:

**Rishabh Gulati** *[rishabh.gulati@us.bosch.com]*

**Vimal** *[Vimalanandan.S@in.bosch.com]*
