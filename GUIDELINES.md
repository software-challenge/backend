# Overview

This document captures development standards and architecture decisions of this project as a point of reference.

## Testing

Unsere Unittests nutzen das [Kotest-Framework](https://kotest.io) mit [JUnit](https://junit.org) im Hintergrund.
Dabei setzen wir auf [WordSpec](https://kotest.io/styles/#word-spec),
da man damit semantisch übersichtlich
sowohl einfache Tests als auch Behavior Driven Development umsetzen kann.
Für Tests von Algorithmen ohne Logik/Verhalten
kann auch [FunSpec](https://kotest.io/styles/#fun-spec) nützlich sein

Viele Tests nutzen noch die StringSpec,
welche jedoch schnell unübersichtlich wird,
da sie keine Verschachtelung erlaubt.
Im Server gibt es außerdem noch einige JUnit5-Tests.
Diese sollten bei größeren Änderungen direkt zum neuen Stil migriert werden.

## XStream

All network communication (client-server) is done via XML, in our JVM implementation the [XStream library](https://x-stream.github.io)
handles the serialization and deserialization from and to objects.

To implement the protocol properly it requires annotations.
Apart from the persistent [sdk protocol classes](sdk/src/server-api) this is particularly relevant when implementing the `Move` and `GameState` classes in the current plugin, including all types used in their non-volatile fields (otherwise marked with @XStreamOmitField) such as `Board` and `Field`.

Another common issue is the pollution of the XML with `class` attributes e.g. `<lastMoveMono class="linked-hash-map"/>`.  
These are created by XStream to denote the implementation used for a Collection if the field type is an abstract type such as List, Set, etc and it thus can't deduce the concrete implementation via reflection.
To avert these superfluous attributes without needing a separate Converter, annotate the serialized fields with a concrete type instead.
Ideally these fields should then be private with generically typed getters to not expose the implementation details internally.

## Cloning

Relevant discussion: https://github.com/software-challenge/backend/pull/148

To enable cloning, we implement deep copy constructors together with a clone method which defers to the copy constructors.
This is needed for all shared plugin classes that hold state and which are not immutable.
Small classes (such as Field) should be immutable, so they can be shared instead of cloning them.

It might be interesting to consider replacing cloning with implicit sharing/copy-on-write semantics to make search algorithms more efficient:
https://doc.qt.io/qt-5/implicit-sharing.html#implicit-sharing-in-detail

## ServiceLoader

We recently introduced the use of the [Java built-in DI facility](https://itnext.io/serviceloader-the-built-in-di-framework-youve-probably-never-heard-of-1fa68a911f9b) [ServiceLoader](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html)
to make some year-specific implementations from the plugin accessible in the sdk and server.

Currently there are two interfaces, [IGamePlugin](sdk/src/server-api/sc/api/plugins/IGamePlugin.java) and [XStreamProvider]( sdk/src/server-api/sc/networking/XStreamProvider.kt), which are implemented in the plugin and then loaded through a ServiceLoader.
The information which implementations to use resides in [resources/META-INF/services](plugin/src/resources/META-INF/services).

## Networking Protocol Classes

[ProtocolPacket](sdk/src/server-api/sc/protocol/ProtocolPacket.kt) is the common interface
for objects sent via the XML Protocol.

### [Requests](sdk/src/server-api/sc/protocol/requests)
- are all suffixed with `Request`
- ask for an action or information  
- any request that extends [AdminLobbyRequest](sdk/src/server-api/sc/protocol/requests/ILobbyRequest.kt)
  requires authentication beforehand

#### [Responses](sdk/src/server-api/sc/protocol/responses)

(*Response) Response to a request  
(*Event) Update to all observers

#### [Room Messages](sdk/src/server-api/sc/protocol/room)

Data sent to a specific room has to implement [RoomMessage](sdk/src/server-api/sc/protocol/room/RoomMessage.kt)
and is then wrapped in a [RoomPacket](sdk/src/server-api/sc/protocol/room/RoomPacket.kt).

The package contains a few standard messages,
but most will be implemented in the corresponding plugin.

