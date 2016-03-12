GeoScript Groovy
================
GeoScript Groovy is the `Groovy <http://groovy.codehaus.org/>`_ implementation of `GeoScript <http://geoscript.org>`_.  GeoScript is a geospatial scripting API for the JVM that contains one API and four implementations (`Python <https://github.com/jdeolive/geoscript-py>`_, `JavaScript <https://github.com/tschaub/geoscript-js>`_, `Scala <https://github.com/dwins/geoscript.scala>`_, and `Groovy <https://github.com/jericks/geoscript-groovy>`_).

GeoScript is built on the shoulders of giants and essentially wraps the `Java Topology Suite <http://tsusiatsoftware.net/jts/main.html>`_ and the `GeoTools <http://geotools.org/>`_ libraries.

GeoScript provides several modules that includes geometry, projection, features, layers, workspaces, styling and rendering.

Build
-----
Building GeoScript Groovy is quite easy.  You will need to have git, Java, Maven, and Ant installed.

Use git to clone the repository::

    git clone git://github.com/jericks/geoscript-groovy.git

Use maven to build, test, and package::

    mvn clean install

The distribution can be found in target/geoscript-groovy-${version}-app/geoscript-groovy-${version}.

Use
---
To use GeoScript Groovy you need Java and Groovy installed and on your PATH.  Next, download the `latest stable release <https://github.com/jericks/geoscript-groovy/releases>`_ ,
the `latest in development build <http://ares.opengeo.org/geoscript/groovy/>`_, or build the code yourself.  Then put the GeoScript Groovy bin directory on your PATH.  You are now ready to use GeoScript Groovy!

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
    def p2 = Projection.transform(p1, "EPSG:4326", "EPSG:26912")

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

Reading a Raster::

    import geoscript.layer.GeoTIFF

    def format = new GeoTIFF(new File("raster.tif"))
    def raster = format.read()

    println "Format = ${raster.format}"
    println "Proj EPSG = ${raster.proj.id}"
    println "Proj WKT = ${raster.proj.wkt}"
    println "Bounds = ${raster.bounds.geometry.wkt}"
    println "Size = ${raster.size}"
    println "Block Size = ${raster.blockSize}"
    println "Pixel Size = ${raster.pixelSize}"
    println "Band:"
    raster.bands.eachWithIndex{b,i ->
        println "   ${i}). ${b}"
    }

Generating tiles::
    
    import geoscript.layer.*
    import geoscript.style.*

    Shapefile shp = new Shapefile(new File("states.shp"))
    shp.style = new Fill("wheat") + new Stroke("navy", 0.1)

    File file = new File("states.gpkg")
    GeoPackage gpkg = new GeoPackage(file, "states", Pyramid.createGlobalMercatorPyramid(origin: Pyramid.Origin.TOP_LEFT))

    TileRenderer renderer = new ImageTileRenderer(gpkg, shp)
    TileGenerator generator = new TileGenerator(verbose: true)
    generator.generate(gpkg, renderer, 0, 4)

See the `web site <http://geoscript.org>`_ or the `examples directory <https://github.com/jericks/geoscript-groovy/tree/master/examples>`_ for more examples.

You can also use GeoScript Groovy as a library. If you use Maven you will need to add the Boundless Maven Repository::

    <repositories>
        <repository>
            <id>boundless</id>
            <name>Boundless Maven Repository</name>
            <url>http://repo.boundlessgeo.com/main</url>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>

and then include the GeoScript Groovy dependency::

    <dependency>
        <groupId>org.geoscript</groupId>
        <artifactId>geoscript-groovy</artifactId>
        <version>1.6.0</version>
    </dependency>

Modules
-------
GeoScript starting at version 1.7 is divided into modules.  A smaller GeoScript Groovy Core module now contains only the
core classes and interfaces and minimal 3rd party dependencies.  The other granular modules add features like additional
Workspaces, IO readers and writers, and additional 3rd party dependencies.  The original uber geoscript-groovy jar is still
available.

Uber Library
------------

* **GeoScript Groovy**:

    The uber library with core and all modules.

    geoscript-groovy

Geometry Library
----------------

* **GeoScript Groovy Geometry**:

    The geometry, projection, and spatial index modules.

    geoscript-groovy-geom


Core Library
------------

* **GeoScript Groovy Core**:

    The minimal core library.

    geoscript-groovy-core


Workspace Modules
-----------------

* **GeoScript Groovy Workspace Directory**

    Adds a Directory Workspace and Shapefile Layer.

    geoscript-groovy-workspace-directory

* **GeoScript Groovy Workspace Geobuf**

    Adds a Geobuf Workspace, Readers, and Writers.

    geoscript-groovy-workspace-geobuf

* **GeoScript Groovy Workspace Property**

    Adds a Property Workspace and Layer.

    geoscript-groovy-workspace-property

* **GeoScript Groovy Workspace OGR**

    Adds an OGR Workspace that requires the OGR native library with JNI support.

    geoscript-groovy-workspace-ogr

* **GeoScript Groovy Workspace WFS**

    Adds a WFS Workspace.

    geoscript-groovy-workspace-wfs


* **GeoScript Groovy Workspace GeoPackage**

    Adds a GeoPackage Workspace.

    geoscript-groovy-workspace-geopackage

* **GeoScript Groovy Workspace MySQL**

    Adds a MySQL Workspace.

    geoscript-groovy-workspace-mysql

* **GeoScript Groovy Workspace H2**

    Adds a H2 Workspace.

    geoscript-groovy-workspace-h2

* **GeoScript Groovy Workspace PostGIS**

    Adds a PostGIS Workspace.

    geoscript-groovy-workspace-postgis

* **GeoScript Groovy Workspace Spatialite**

    Adds a Spatialite Workspace.

    geoscript-groovy-workspace-spatialite

IO Modules
----------

* **GeoScript Groovy IO GeoJSON**:

    Adds GeoJSON Readers and Writers

    geoscript-groovy-io-geojson

* **GeoScript Groovy IO GML**:

    Adds GML Readers and Writers

    geoscript-groovy-io-gml

* **GeoScript Groovy IO KML**:

    Adds KML Readers and Writers

    geoscript-groovy-io-kml

* **GeoScript Groovy IO CSV**:

    Adds CSV Readers and Writers

    geoscript-groovy-io-csv

* **GeoScript Groovy IO GeoRSS**:

    Adds GeoRSS Readers and Writers

    geoscript-groovy-io-georss

* **GeoScript Groovy IO GPX**:

    Adds GPX Readers and Writers

    geoscript-groovy-io-gpx

* **GeoScript Groovy IO MVT**:

    Adds MVT Readers and Writers

    geoscript-groovy-io-mvt

* **GeoScript Groovy IO CSS**:

    Adds CSS Readers and Writers

    geoscript-groovy-io-css

Pyramid IO Modules
------------------

* **GeoScript Groovy Pyramid IO CSV**

    Adds CSV Pyramid IO support.

    geoscript-groovy-pyramid-io-csv

* **GeoScript Groovy Pyramid IO JSON**

    Adds CSV Pyramid JSON support.

    geoscript-groovy-pyramid-io-json

* **GeoScript Groovy Pyramid IO GDAL TMS**

    Adds GDAL TMS Pyramid IO support.

    geoscript-groovy-pyramid-io-gdaltms

* **GeoScript Groovy Pyramid IO XML**

    Adds XML Pyramid IO support.

    geoscript-groovy-pyramid-io-xml


Tile Modules
------------

* **GeoScript Groovy Tile TMS**

    Adds TMS Tile support.

    geoscript-groovy-tile-tms

* **GeoScript Groovy Tile GeoPackage**

    Adds GeoPackage Tile support.

    geoscript-groovy-tile-geopackage

* **GeoScript Groovy Tile MBTiles**

    Adds MBTiles Tile support.

    geoscript-groovy-tile-mbtiles

* **GeoScript Groovy Tile OSM**

    Adds OSM Tile support.

    geoscript-groovy-tile-osm

* **GeoScript Groovy Tile UTF**

    Adds UTF Tile support.

    geoscript-groovy-tile-utf

* **GeoScript Groovy Tile VectorTiles**

    Adds Vector Tiles support.

    geoscript-groovy-tile-vectortiles

Raster Modules
--------------

* **GeoScript Groovy Raster ArcGrid**

    Adds ArcGrid Raster support.

    geoscript-groovy-raster-arcgrid

* **GeoScript Groovy Raster GeoTiff**

    Adds GeoTiff Raster support.

    geoscript-groovy-raster-geotiff

* **GeoScript Groovy Raster Grass**

    Adds Grass Raster support.

    geoscript-groovy-raster-grass

* **GeoScript Groovy Raster GTopo30**

    Adds GTopo30 Raster support.

    geoscript-groovy-raster-gtopo30

* **GeoScript Groovy Raster Image Pyramid**

    Adds Image Pyramid Raster support.

    geoscript-groovy-raster-imagepyramid

* **GeoScript Groovy Raster Mosaic**

    Adds Mosaic Raster support.

    geoscript-groovy-raster-mosaic

* **GeoScript Groovy Raster MrSid**

    Adds MrSid Raster support.

    geoscript-groovy-raster-mrsid

* **GeoScript Groovy Raster NetCDF**

    Adds NetCDF Raster support.

    geoscript-groovy-raster-netcdf

* **GeoScript Groovy Raster WorldImage**

    Adds World Image Raster support.

    geoscript-groovy-raster-worldimage

* **GeoScript Groovy Raster MapAlgebra**

    Adds MapAlgebra Raster support.

    geoscript-groovy-raster-mapalgebra


Render Modules
--------------
* **GeoScript Groovy Render GUI**

    Adds a GUI Renderer

    geoscript-groovy-render-gui

* **GeoScript Groovy Render PDF**

    Adds a PDF Renderer

    geoscript-groovy-render-pdf

* **GeoScript Groovy Render SVG**

    Adds a SVG Renderer

    geoscript-groovy-render-svg

Other Modules
-------------

* **GeoScript Groovy Graticule**:

    Adds Graticule support.

    geoscript-groovy-graticule

* **GeoScript Groovy WMS**

    Adds WMS support.

    geoscript-groovy-wms


* **GeoScript Groovy Viewer**

    Adds interactive Viewer

    geoscript-groovy-viewer

* **GeoScript Groovy Plot**

    Adds ploting or charting support.

    geoscript-groovy-plot

Using Modules with Groovy Grape
-------------------------------
Now that GeoScript Groovy is modular, it is easier to use in regular Groovy Scripts using Groovy's Grape dependency
management system.::

    @GrabResolver(name='boundless', root='http://repo.boundlessgeo.com/main/')
    @GrabExclude('org.codehaus.groovy:groovy-all')
    @Grab('org.geoscript:geoscript-groovy-core:2.0-SNAPSHOT')
    @Grab('org.geoscript:geoscript-groovy-io-geojson:2.0-SNAPSHOT')
    import geoscript.geom.Point

    Point p = new Point(-122,47)
    println p.wkt
    println p.geoJSON

Versions
--------

+-----------+----------+-----------+------+---------+
| GeoScript | GeoTools | GeoServer | JTS  | Groovy  |
+-----------+----------+-----------+------+---------+
| 1.6       | 14       | 2.8       | 1.13 | 2.4.5   |
+-----------+----------+-----------+------+---------+
| 1.5       | 13       | 2.7       | 1.13 | 2.3.10  |
+-----------+----------+-----------+------+---------+
| 1.4       | 12       | 2.6       | 1.13 | 2.2.2   |
+-----------+----------+-----------+------+---------+
| 1.3       | 11       | 2.5       | 1.13 | 2.1.9   |
+-----------+----------+-----------+------+---------+
| 1.2       | 10       | 2.4       | 1.13 | 2.1.6   |
+-----------+----------+-----------+------+---------+
| 1.1       | 9        | 2.3       | 1.13 | 1.8.9   |
+-----------+----------+-----------+------+---------+
| 1.0       | 8        | 2.2       | 1.12 | 1.8.8   |
+-----------+----------+-----------+------+---------+

Presentations
-------------
`GeoScript: The GeoSpatial Swiss Army Knife (FOSS4G 2014) <http://geoscript.github.io/foss4g2014-talk/#/>`_

`Using GeoScript Groovy (CUGOS 2014) <http://www.slideshare.net/JaredErickson/using-geoscript-groovy>`_

`Rendering Maps in GeoScript (CUGOS 2012) <http://www.slideshare.net/JaredErickson/geo-scriptstylerendering>`_

`Scripting GeoServer (CUGOS 2012) <http://www.slideshare.net/JaredErickson/scripting-geoserver>`_

`GeoScript: Spatial Capabilities for Scripting Languages (FOSS4G 2011) <http://www.slideshare.net/jdeolive/geoscript-spatial-capabilities-for-scripting-languages>`_

License
-------
GeoScript Groovy is open source and licensed under the MIT license.

.. image:: https://travis-ci.org/geoscript/geoscript-groovy.svg?branch=master
    :target: https://travis-ci.org/geoscript/geoscript-groovy
