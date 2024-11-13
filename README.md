# Instana [![Java CI with Maven](https://github.com/manonworldrepository/instana/actions/workflows/maven.yml/badge.svg)](https://github.com/manonworldrepository/instana/actions/workflows/maven.yml)


### Instana distributed tracing task on lightweight container with cloud native build packs (Ahead of Time)
If you're already familiar with Spring Boot container images support, this is the easiest way to get started.
Docker should be installed and configured on your machine prior to creating the image.

To create the image, run the following goal:

```
$ ./mvnw spring-boot:build-image -Pnative
```

Then, you can run the app like any other container:

```
$ docker run -p 8080:8080 --rm instana:0.0.1-SNAPSHOT
```

#### Application [URL: http://localhost:8080/stats](http://localhost:8080/stats)

For single line input (one graph), you can use postman to create a ``` POST ``` request with a file upload (Multipart Request)

Attach a file as an input: ``` src/test/resources/Input/single-line-input.txt ``` and use key as ``` file ``` in the body and use ``` form-data/multipart ```

As shown in the small recording [here](./Recording2024-11-03%20063919.mp4).

And finally, you can also use the file ``` multi-line-input.txt ``` in order to test multi line support.

Note: Every line (graph) will be processed reactively and results will be streamed to the output line by line (graph by graph) over HTTP using octet streams.

### Executable with Native Build Tools
Use this option if you want to explore more options such as running your tests in a native image.
The GraalVM `native-image` compiler should be installed and configured on your machine.

NOTE: GraalVM 22.3+ is required.

To create the executable, run the following goal:

```
$ ./mvnw native:compile -Pnative
```

Then, you can run the app as follows:
```
$ target/instana
```

You can also run your existing tests suite in a native image.
This is an efficient way to validate the compatibility of your application.

To run the existing tests in a native image, run the following goal:

```
$ ./mvnw test -PnativeTest
```




