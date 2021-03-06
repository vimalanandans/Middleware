# Bezirk
[![Build Status](https://travis-ci.org/Bezirk-Bosch/Middleware.svg?branch=master)](https://travis-ci.org/Bezirk-Bosch/Middleware)

The Bezirk middleware is developer-friendly, user-centric, and privacy-respecting glue for the
Internet of Things.

## Build Dependencies

- Latest version of the JDK
- (Optional) Latest version of Android Studio

To build and test the middleware run: `./gradlew check`

To build the Bezirk API JavaDocs: `./gradlew :core:bezirk-middleware-api:javadoc`

To build and run the Java SE test app (convenient for quick sanity checks): `./gradlew :java:testApp:run`

## Vision

The more compelling aspects of the Internet of Things dream involve cooperation between devices. With
this cooperation, we can achieve dreams more complex than are currently possible.
Cooperation can enable a world where we offload our mundane tasks to our Things, with outcomes
customized to our needs and desires. Cooperation can also optimize business and industrial processes
using accurate and timely data. These dreams need an ecosystem where devices seamlessly interoperate
and are easy for the right (and only the right) entities to access.

In practice, devices are diverse in their manufacturers, functions, and use cases, and they often have buggy, poorly documented APIs that do not work together. How do we fulfill the IoT dream if the basic building blocks are clumsy and difficult to work with?

The Bezirk ecosystem aims to solve these problems. The middleware in this repository forms its heart.
The Bezirk middleware implements cloudless and brokerless publish-and-subscribe in Java. It also
includes basic security and identity management building blocks. Together, these components enable
secure, seamless interoperability. The ecosystem includes the following components to bootstrap interoperability:

- _Zirks_ use the middleware to communicate with hardware or other computing devices within the Bezirk
ecosystem
- _Events_ are the unit of communication between Zirks
 - [Adapter Zirks](https://github.com/Bezirk-Bosch/AdapterZirks) are wrappers that enable existing
 hardware (e.g., Philips Hue lights) to be controlled using Bezirk events.  
 - [Hardware events](https://github.com/Bezirk-Bosch/HardwareEvents) provide a uniform interface for accessing IoT devices, including
 beacons, environmental sensors, lights, and others.

## Example

With the middleware, events, and adapter Zirks, very little code is required to
write a Zirk that detects and actuates all Lightify lights on a network:

```java
    final Bezirk bezirk = BezirkMiddleware.registerZirk("Lightify Zirk");

    final EventSet lightEvents = new EventSet(LightsDetectedEvent.class);

    lightEvents.setEventReceiver(new EventSet.EventReceiver() {
        @Override
        public void receiveEvent(Event event, ZirkEndPoint sender) {
            if (event instanceof LightsDetectedEvent) {
                for (final Light light : ((LightsDetectedEvent) event).getLights()) {
                    bezirk.sendEvent(new TurnLightOnEvent(light));

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            bezirk.sendEvent(new TurnLightOffEvent(light));
                        }
                    }, 2000);
                }
            }
        }
    });

    bezirk.subscribe(lightEvents);

    Set<String> gateways = LightifyAdapter.discoverGateways();
    try {
        new LightifyAdapter(bezirk, gateways.toArray(new String[gateways.size()])[0]);
    } catch (IOException e) {
        logger.error("Failed to connect to lightify gateway", e);
    }
```

Using Philips Hue products instead? Simply switch the last two (non-exception related) lines of code
to use the Hue adapter. This example even grows in capability without code modifications. If another
Zirk on the same network were to implement support for yet another model of lights and broadcasts
`LightsDetectedEvent`, our example Zirk will seamlessly receive its messages and actuate the new
lights.

## The Bigger Picture

Many of the examples above have been smart home oriented. We are thinking bigger. What if the Zirks
you use sent observations about how you use them to a personalization Zirk you own? This
personalization Zirk could then build profiles based on how you interact with your world. What if instead
of waiting to order at a coffee shop, the store could ask your personalization Zirk about your favorite
drink as you walk in? What if your taxi knew what temperature the car should be and what to set the
radio to as it pulls up to pick you up?  What if the shipping processes for your medicine or
groceries were smart enough to ensure unbroken
[cold chains](https://en.wikipedia.org/wiki/Cold_chain)? What if your city could dynamically adjust
stop light timings based on current traffic? What if your doctor could tell what is going on every
time your blood pressure is too high? All of this can be built on top of the Bezirk middleware.

You might be thinking to yourself: "What about privacy concerns? I'm not sure I'm comfortable having every action I take observed
to build profiles." This is valid and another problem related to compelling IoT visions that we're trying to solve. The
Bezirk middleware aims to be privacy-respecting in the sense that it puts the user in
control of their own data, while taking care of the nitty-gritty security details automatically.
We don't force users' data to go through the cloud, and users can decide the scope within which their Zirks
can communicate. As a result, all of the users' information remains completely in their control. While parts of this vision are not implemented yet, we aim
to make it as easy on the user as possible. We firmly believe that people should not have to possess
expert-level security awareness to use the IoT safely, securely, and privately.

## Helping Out

Please peruse the [wiki](https://github.com/Bezirk-Bosch/Middleware/wiki) to learn more about
what we are working on, review design details for implemented and prospective features, and, most
importantly, learn how you can contribute to the effort.
