.. _gradle:

Gradle Quickstart
=================
This is a short example of how to use GeoScript Groovy to create a simple command line application with Gradle.

We are going to build command line that can project geometry read from WKT from one projection to another.

First, create project skeleton::

    mkdir geo-gradle

    cd geo-gradle/

    touch build.gradle

    mkdir -p src/main/groovy/org/geo

    touch src/main/groovy/org/geo/App.groovy

Then edit the build.gradle file.

.. code-block:: groovy

    apply plugin: "groovy"
    apply plugin: "application"

    version = 0.1
    mainClassName = "org.geo.App"

    repositories {
    maven {
       url "https://repo.boundlessgeo.com/main"
    }
    maven {
      url "http://download.osgeo.org/webdav/geotools"
    }
    maven {
      url "http://download.java.net/maven/2"
    }
    maven {
      url "http://maven.geo-solutions.it"
    }
    maven {
      url "https://artifacts.unidata.ucar.edu/content/repositories/unidata"
    }
    maven {
      url "https://github.com/ElectronicChartCentre/ecc-mvn-repo/raw/master/releases"
    }
        mavenCentral()
    }

    dependencies {
       compile "org.codehaus.groovy:groovy-all:2.1.9"
       compile "org.geoscript:geoscript-groovy:1.7.0"
    }

    task wrapper(type: Wrapper) {
        gradleVersion = '1.12'
    }

Create a gradle wrapper that will download a specific version of gradle::

    gradle wrapper

Then, write a small application to project a WKT Geometry from one projection to another.

Edit **src/main/groovy/org/geo/App.groovy**:

.. code-block:: groovy

    package org.geo

    import geoscript.geom.Geometry
    import geoscript.proj.Projection

    class App {
        static void main(String[] args) {
            Geometry geom = Geometry.fromString(args[0])
            Projection fromProj = new Projection(args[1])
            Projection toProj = new Projection(args[2])
            Geometry transformedGeom = fromProj.transform(geom, toProj)
            println transformedGeom.wkt
        }
    }

Now you can compile the application (and fix any errors)::

    ./gradlew build

Once your code compiles, you can create a command line application because we are using the "application" plugin::

    ./gradlew installApp

Run it!::

   build/install/geo-gradle/bin/geo-gradle "POINT (1200533.72 648700.31)" "EPSG:2927" "EPSG:4326"

    POINT (-122.26636328086927 47.09868497461313)
