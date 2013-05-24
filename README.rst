GeoScript Groovy
================
GeoScript Groovy is the `Groovy <http://groovy.codehaus.org/>`_ implementation of `GeoScript <http://geoscript.org>`_.  GeoScript is a geospatial scripting API for the JVM that contains one API and four implementations (`Python <https://github.com/jdeolive/geoscript-py>`_, `JavaScript <https://github.com/tschaub/geoscript-js>`_, `Scala <https://github.com/dwins/geoscript.scala>`_, and `Groovy <https://github.com/jericks/geoscript-groovy>`_).

GeoScript is built the shoulders of giants and essentially wraps the `Java Topology Suite <http://tsusiatsoftware.net/jts/main.html>`_ and the `GeoTools <http://geotools.org/>`_ libraries.

GeoScript provides several modules that includes geometry, projection, features, workspaces, styling and rendering.

Build
-----
Building GeoScript Groovy is quite easy.  You will need to have git, Java, Maven, and Ant installed.

Use git to clone the repository::

    git clone git://github.com/jericks/geoscript-groovy.git

Use maven to build, test, and package::

    mvn clean install

The distribution can be found in target/geoscript-groovy-1.0-app/geoscript-groovy-1.0.

Use
---
To use GeoScript Groovy you need Java and Groovy installed and on your PATH.  Next, download the `latest stable release 1.0 <https://docs.google.com/file/d/0B8cwqNmbcThpQlBmaWsyNDlQMVU/edit?usp=sharing>`_, try the `latest unstable release 1.1-SNAPSHOT <https://docs.google.com/file/d/0B8cwqNmbcThpWFRxQzlHQVJlVW8/edit?usp=sharing>`_  or build the code yourself.  Then put the GeoScript Groovy bin directory on your PATH.  You are now ready to use GeoScript Groovy!

GeoScript Groovy has three commands:

1. geoscript-groovy (which can run Groovy files)
2. geoscript-groovysh (which starts a REPL shell)
3. geoscript-groovyConsole (which starts a graphical editor/mini IDE)

Buffering a Point::

    import geoscript.geom.Point

    def point = new Point(0,0)
    def poly = point.buffer(10)
    println(poly.wkt)

Project a Geomemtry::

    import geoscript.geom.Point
    import geoscript.proj.Projection

    def p1 = new Point(-111.0, 45.7)
    def p2 = Projection.transform(p, "EPSG:4326", "EPSG:26912")

Read a Shapefile::

    import geoscript.layer.Shapefile
    import geoscript.geom.Bounds

    def shp = new Shapefile("states.shp")
    int count = shp.count
    Bounds bounds = shp.bounds
    shp.features.each {f->
        println(f)
    }

Drawing a Shapefile::

    import geoscript.layer.Shapefile
    import geoscript.style.Stroke
    import static geoscript.render.Draw.draw

    def shp = new Shapefile("states.shp")
    shp.style = new Stroke("#999999", 0.1)
    draw(shp)

See the `web site <http://geoscript.org>`_ or the `examples directory <https://github.com/jericks/geoscript-groovy/tree/master/examples>`_ for more examples.

License
-------
GeoScript Groovy is open source and licensed under the MIT license.
