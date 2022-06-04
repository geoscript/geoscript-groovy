.. _mavenapp:

Maven Quickstart
================
This is a short example of how to use GeoScript Groovy to create a simple command line application with Maven.

Create the project skeleton::

     mvn archetype:generate -B -DgroupId=org.geo -DartifactId=geo-app -DarchetypeArtifactId=maven-archetype-quickstart

Move into the new directory::

     cd geo-app

Add geoscript as a dependency in pom.xml.

.. code-block:: xml

    <repositories>
        <repository>
            <id>boundless</id>
            <name>Boundless Maven Repository</name>
            <url>https://repo.boundlessgeo.com/main</url>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>org.geoscript</groupId>
            <artifactId>geoscript-groovy</artifactId>
            <version>1.19.0</version>
        </dependency>
    </dependencies>

Add Groovy Eclipse Compiler Maven Plugin in the pom.xml.

.. code-block:: xml

    <build>
        <plugins>
            <plugin>
                <groupId>org.codehaus.groovy</groupId>
                <artifactId>groovy-eclipse-compiler</artifactId>
                <version>2.8.0-01</version>
                <extensions>true</extensions>
            </plugin>
            <plugin>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.1</version>
                <configuration>
                    <compilerId>groovy-eclipse-compiler</compilerId>
                </configuration>
                <dependencies>
                    <dependency>
                        <groupId>org.codehaus.groovy</groupId>
                        <artifactId>groovy-eclipse-compiler</artifactId>
                        <version>2.8.0-01</version>
                    </dependency>
                    <dependency>
                        <groupId>org.codehaus.groovy</groupId>
                        <artifactId>groovy-eclipse-batch</artifactId>
                        <version>2.1.5-03</version>
                    </dependency>
                </dependencies>
            </plugin>
        </plugins>
    </build>

Create src and test directories::

    mkdir -p src/main/groovy/org/geo

    mkdir -p src/test/groovy/org/geo

    rm -r src/main/java

    rm -r src/test/java

Create App.grovy::

    touch src/main/groovy/org/geo/App.groovy

.. code-block:: groovy

    package org.geo

    import geoscript.geom.Geometry

    class App {
        static void main(String[] args) {
            if (args.length < 2) {
                println "USAGE: org.geo.App <geometry> <buffer distance>"
            } else {
                Geometry geom = Geometry.fromString(args[0]);
                println geom.buffer(args[1] as double);
            }
        }
    }

Create an executable jar.

.. code-block:: xml

    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-jar-plugin</artifactId>
        <version>2.4</version>
        <configuration>
            <archive>
                <manifest>
                    <addClasspath>true</addClasspath>
                    <classpathPrefix>lib/</classpathPrefix>
                    <mainClass>org.geo.App</mainClass>
                </manifest>
            </archive>
        </configuration>
    </plugin>

Collect jar dependencies.

.. code-block:: xml

    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-dependency-plugin</artifactId>
        <version>2.8</version>
        <executions>
            <execution>
                <id>copy</id>
                <phase>package</phase>
                <goals>
                    <goal>copy-dependencies</goal>
                </goals>
                <configuration>
                    <outputDirectory>
                        ${project.build.directory}/lib
                    </outputDirectory>
                </configuration>
            </execution>
        </executions>
    </plugin>

Build it!::

    mvn clean install

Run it!::

    java -jar target/geo-app-1.0-SNAPSHOT.jar
    USAGE: org.geo.App <geometry> <buffer distance>

    java -jar target/geo-app-1.0-SNAPSHOT.jar "POINT (1 1)" 10
    POLYGON ((...)

