# stream-pipeline

A Java utility library that extends the standard `Stream` API with hierarchical traversal, paged streaming, stream pipelines, and binary/text stream consumers.

## Features

- **Flat streaming** — traverse recursive tree structures as a flat `Stream` with configurable traversal order
- **Paged streaming** — stream paginated data sources transparently via `PageSupplier`
- **Stream pipelines** — structured input→output pipeline with lifecycle hooks, retry, skip/abort flow control, and observability via `PipelineListener`
- **Binary & text consumers** — `StreamConsumer` implementations for byte and character streams
- **Swing component streaming** — flatten AWT/Swing component trees as a `Stream<Component>`
- **Reflection streaming** — stream ancestor class hierarchies

## Requirements

- Java 21+
- Maven 3.x

## Installation

```xml
<dependency>
    <groupId>org.cosinuscode.stream</groupId>
    <artifactId>stream-pipeline</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage

### Iterator / Enumeration to Stream

```java
Stream<T> stream = stream(iterator);
Stream<T> stream = stream(enumeration);
```

### Paged streaming

Implement `PageSupplier<T>` to provide pages on demand:

```java
Stream<MyItem> stream = pagedStream(pageable -> myRepo.findAll(pageable));
Stream<MyItem> stream = pagedStream(pageable -> myRepo.findAll(pageable), 50);
```

### Hierarchical flat streaming

Your node type must implement `StreamSupplier<T>` (return children via `stream()`):

```java
Stream<Node> flat = flatStream(FlatStreamingStrategy.IN_DEPTH, rootNode);
Stream<Node> flat = flatStream(FlatStreamingStrategy.LEVEL_UP_BOTTOM, rootNode);
Stream<Node> flat = flatStream(FlatStreamingStrategy.LEVEL_BOTTOM_UP, rootNode);
```

Traversal strategies:

| Strategy | Order |
|---|---|
| `LEVEL_UP_BOTTOM` | Breadth-first, parent before children |
| `IN_DEPTH` | Depth-first, parent before children |
| `LEVEL_BOTTOM_UP` | Breadth-first, children before parent |

### Swing component tree

```java
Stream<Component> components = flatComponentsStream(myPanel);
```

### Stream pipeline

Implement `Pipeline<D, I, O, S>` and call `openPipeline()`. Override the default lifecycle hooks as needed:

- `preparePipelineOpen` — pre-flight checks; throw `SkipPipelineConsumeException` to skip the entire pipeline
- `preparePipelineConsume` — setup before consumption starts
- `checkPipelineConsume` — post-consumption validation
- `getPipelineListener()` — return a `PipelineListener<D>` to observe open/consume/close/fail events

Throw `SkipPipelineConsumeException` inside a consumer to skip an individual item; throw `AbortPipelineConsumeException` to abort the run. Neither is treated as an application error.

## Building

```bash
mvn package          # build, test, attach sources and javadoc JARs
mvn test             # run tests only
mvn clean package -DskipTests   # fast build
```

## License

Apache License 2.0 — see [LICENSE](LICENSE).
