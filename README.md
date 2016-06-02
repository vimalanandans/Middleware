# Bezirk

The Bezirk Middleware project forms the heart of the Bezirk platform. This repository contains the following middleware modules:

- **android** - Android-specific middleware code
 - **android-libraries** - code to fetch the Bezirk API and implement platform-specific features (e.g. barcode scanning)
   - **android-comms-zyre-jni** - Android-specific adapter for the [ZeroMQ Zyre](https://github.com/zeromq/zyre) framework
    - **proxy** - Android-specific code for fetching the Bezirk API
    - **zbarscanner** - Bezirk wrapper app for [ZbarScanner](https://sourceforge.net/projects/zbar/?source=navbar)
 - **android-starter** - code to implement Android-specific subset of the Bezirk API
 - **ui** - Android GUI's for Bezirk configuration and user-interaction
   - **control-ui** - basic GUI for the Bezirk Android app
    - **sphere-pipe-ui** - library containing configuration layouts for Bezirk Android app
- **core** - platform-independent middleware code
 - **actions** - specification of operations (e.g. send or receive an event) that can be requested of the middleware
 - **bezirk** - internal-facing concrete implementations of Bezirk API interfaces and classes
 - **bezirk-middleware-api** - interfaces and classes defining the Bezirk API and its JavaDocs
 - **commons** - primary location for internal implementation of middleware features
 - **comms** - implementations of various communication protocols for Bezirk
 - **control-messages** - platform-independent classes to implement Bezirk-internal messages (e.g. middleware instance-to-middleware instance communication)
 - **device-objects** - API for collecting information about a device that is joining a sphere
 - **network-util** - code for getting local networking information (e.g. IP address, MAC addresss, etc.)
 - **pipes** - code implementing Bezirk's pipe feature
 - **protocols** - implementations of Bezirk-standard protocols (e.g. those for personalization)
- **java** - Java SE-specific middleware code
 - **libraries** - code to fetch the Bezirk API and implement platform-specific feature code
   - **bezirk-factory** - Java SE-specific code for fetching the Bezirk API
    - **comms-zyre-jni** - Java SE-specific adapter for the [ZeroMQ Zyre](https://github.com/zeromq/zyre) framework
 - **starter** - code to implement Java SE-specific subset of the Bezirk API (e.g. for PC's running full JRE)
 - **testapp** - a basic "Hello World" Zirk for quickly testing middleware changes

## Build Dependencies

- Latest version of the JDK
- (Optional) Latest version of Android Studio

To build and test the middleware run: `./gradlew check`

To build the Bezirk API JavaDocs: `./gradlew :core:bezirk-middleware-api:javadoc`

To build and run the Java SE test app (convenient for quick sanity checks): `./gradlew :java:testApp:run`

## Running Without Gradle

If you'd like to run a Java SE build of the Bezirk middleware or an application that uses it without the supplied Gradle build scripts (e.g. to unit test within an IDE), you must configure your operating system's environment variable that the JVM uses to set the `java.library.path` system property to include the directory where Bezirk's [ZeroMQ](zeromq.org) dependencies are located. The Bezirk middleware contains copies of these dependencies and automatically copies their correct versions onto the system during the first run.

- Linux: Set `LD_LIBRARY_PATH` using the following commands:
```bash
echo 'export LD_LIBRARY_PATH=$TMPDIR/lib-zeromq-bin/' >> ~/.bash_profile
echo 'export PATH=$PATH:$LD_LIBRARY_PATH' >> ~/.bash_profile
```
- Mac OS X: Set `DYLD_LIBRARY_PATH` using the following commands:
```bash
echo 'export DYLD_LIBRARY_PATH=$TMPDIR/lib-zeromq-bin/' >> ~/.bash_profile
echo 'export PATH=$PATH:$DYLD_LIBRARY_PATH' >> ~/.bash_profile
```
- Windows: Add the absolute path C:\Users\\**\<username\>**\AppData\Local\Temp\lib-zeromq-bin to the system's `PATH` variable.

You must restart any application (e.g. Android Studio) that requires this environment variable before the configuration will take effect.

Alternatively, if you can set VM switches when running the JVM for an instance of Bezirk (or a Zirk), use `-Djava.library.path=<temp_zeromq_path>`.
