﻿# Bezirk 

The Bezirk Middleware project forms the heart of the Bezirk platform. This repository contains the following middleware modules:

- **android** - Android-specific middleware code
 - **android-libraries** -
   - **android-comms-zyre-jni** -
    - **proxy** -
    - **zbarscanner** -
 - **android-starter** -
 - **ui** - Android GUI's for Bezirk configuration and user-interaction
   - **control-ui** - basic GUI for the Bezirk Android app
    - **sphere-pipe-ui** - library containing configuration layouts for Bezirk Android app
- **core** - platform-independent middleware code
 - **actions** -
 - **bezirk** -
 - **bezirk-middleware-api** - interfaces and classes definining the Bezirk API and its JavaDocs
 - **commons** -
 - **comms** -
 - **control-messages** -
 - **device-objects** -
 - **network-util** -
 - **pipes** -
 - **protocols** - implementations of Bezirk-standard protocols (e.g. those for personalization)
- **java** - Standard Java-specific middleware code
 - **libraries** -
   - **bezirk-factory** -
    - **comms-zyre-jni** -
 - **starter** -
 - **testapp** - a basic "Hello World" Zirk for quickly testing middleware changes

## Build Dependencies

- Latest version of the JDK
- (Optional) Latest version of Android Studio

To build and test the middleware run: `./gradlew check`

## System Enivornment
### OSX
Set the environnment variable DYLD_LIBRARY_PATH

1. Go to home directory
    ```
    cd ~
    ```
2. If file ‘.bash_profile’ does not exist create it 
    ```
    touch .bash_profile
    ```
3. Open file to edit
    ```
    vim .bash_profile
    ```
4. Add path to the osx native libraries will be extracted at /[tmpdir]/lib-zeromq-bin/. Example:  /tmp/lib-zeromq-bin/
        or edit run configuration of each application /tests and add -Djava.libarary.path="/tmp/lib-zeromq-bin/" (BELOW STEP IS NOT NEEDED. verify and remove)
    ```
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
