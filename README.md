# Bezirk 

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

## For core-zirks & show-case application developers
* To continue to use your existing projects, point to artifacts of version '2.0.1' instead of projects from Java-Common, Java-Build & Android-Build.
* Migrate projects to the new structure.
