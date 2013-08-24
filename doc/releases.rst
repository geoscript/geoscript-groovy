.. _releases:

GeoScript Groovy Releases
=========================

1.1
---

    The 1.1 release of GeoScript was built on Groovy 1.8.9, GeoTools 9.x, and Java Topology Suite (JTS) 1.13

    The focus was on adding a Raster support (geoscript.layer), Charting (geoscript.plot), and numerous bug fixes and small features.

    **Raster**

        * Format (ArcGri, GeoTIFF, GTopo30, Grass, ImagePyramid, MrSID, WorldImage)

          Formats allow you to read and write Rasters::

            import geoscript.layer.*

            def format = new GeoTIFF()
            def raster = format.read(new File("raster.tif"))

            def format2 = new WorldImage()
            format2.write(raster, new File("raster.png"))

        * Raster::

            import geoscript.layer.*

            def format = new GeoTIFF()
            def raster = format.read(new File("raster.tif"))

            println raster.proj.id
            println raster.bounds
            println raster.size

        * Band::

            import geoscript.layer.*

            def format = new GeoTIFF()
            def raster = format.read(new File("raster.tif"))
            raster.bands.eachWithIndex{b,i ->
                println "Band ${i}:"
                println "   Min: ${b.min}"
                println "   Max: ${b.max}"
                println "   NoData: ${b.noData}"
                println "   Unit: ${b.unit}"
                println "   Scale: ${b.scale}"
                println "   Offset: ${b.offset}"
                println "   Type: ${b.type}"
            }

        * MapAlgebra (which is powered by Jiffle)::

            import geoscript.layer.*

            def format = new GeoTIFF()
            def raster = format.read(new File("raster.tif"))

            Raster rasterPlusTen = raster + 10

            MapAlgebra algebra = new MapAlgebra()
            Raster output = algebra.calculate("dest = src > 200;", [src: raster], size: [600, 400])

        * Process

          Raster support was added to the Process module (geoscript.process.Process) which opens up numerous geospatial algorithms like heatmap, barnes surface, and raster algebra.::


            Process process = new Process("vec:BarnesSurface")
            results = process.execute([
                data: layer.cursor,
                valueAttr: "value",
                scale: 300,
                convergence: 0.3,
                passes: 2,
                minObservations: 1,
                maxObservationDistance: 0,
                pixelsPerCell: 1,
                noDataValue: -999,
                outputWidth: 100,
                outputHeight: 100,
                outputBBOX: layer.bounds
            ])
            Raster raster = results.result

        * Style

          Raster specific Symbolizers were added to the geoscript.style module::

            def raster = new RasterSymbolizer(0.5)

            def channel = new ChannelSelection("red", "green", "blue")

            def colorMap = new ColorMap([[color: "#008000", quantity:70], [color:"#663333", quantity:256]])

            def c = new ContrastEnhancement("histogram", 0.5)

            def shadedRelief = new ShadedRelief(35, true)

        * Rendering

          The geoscript Rendering module (geoscript.render) now supports drawing Rasters::

            import geoscript.layer.*
            import geoscript.render.*

            def format = new GeoTIFF()
            def raster = format.read(new File("raster.tif"))
            Draw.draw(raster)

            Map map = new Map(layers:[new Shapefile("states.shp"), raster])
            def image = map.drawToImage()

    **Plot**

        * Chart

          A Chart can be created by one of the factory classes (Bar, Box, Curve, Pie, Regression, and Scatter).Once created, you can display it as an interactive app, save it to a File, or save it to an Image::

            Chart chart = Box.box(["A":[1,10,20],"B":[45,39,10],"C":[2,4,9],"D":[14,15,19]])
            chart.show()
            chart.save(new File("bar.jpeg"))
            def image = chart.image

        * Bar::

            Chart chart = Bar.xy([[1,10],[45,12],[23,3],[5,20]])

            Chart chart = Bar.category(["A":20,"B":45,"C":2,"D":14])

        * Box::

            Chart chart = Box.box(["A":[1,10,20],"B":[45,39,10],"C":[2,4,9],"D":[14,15,19]])

        * Curve::
            
            Chart chart = Curve.curve([[1,10],[45,12],[23,3],[5,20]])

        * Pie::
        
            Chart chart = Pie.pie(["A":20,"B":45,"C":2,"D":14])

        * Regression::

            def points = Geometry.createRandomPoints(new Bounds(0,0,100,100).geometry, 10)
            List data = points.geometries.collect{pt ->
                [pt.x,pt.y]
            }
                
        * Scatter::

            def points = Geometry.createRandomPoints(new Bounds(0,0,100,100).geometry, 10)
            List data = points.geometries.collect{pt ->
             [pt.x,pt.y]
            }

    **General**

        * Include GroovyDocs in zip distribution

    **Geometry**

        * List<Point> getNearestPoints(Geometry other)
        * List<Point> getPoints()
        * Geometry smooth(double fit)
        * static Geometry cascadedUnion(List<Polygon> polygons)
        * static Geometry fromString(String str)

    **Polygon & MultiPolygon**

        * Geometry split(LineString lineString)
        * Geometry split(MultiLineString multiLineString)

    **Schema**

        * boolean has(def field)
        * Map addSchema(Map options = [:], Schema otherSchema, String newName)
        * Schema changeField(Field oldField, Field newField, String name)
        * Schema changeFields(Map<Field, Field> fieldsToChange, String name)
        * Schema addFields(List<Field> newFields, String name)
        * Schema addField(Field field, String name)
        * Schema removeField(Field field, String name)
        * Schema removeFields(List<Field> fieldsToRemove, String name)

    **Layer**

        * Reproject features on the fly when using a Cursor::

            Cursor c = layer.getCursor(destProj: "EPSG:2927")

        * Set source projection when reprojecting Layers::

            Layer layer2 = layer1.reproject(new Projection("EPSG:2927"), "projected_facilties", 1000, new Projection("EPSG:4326"))

        * Add a List of Maps to a Layer inside of a Transaction::

            layer1.add([
                [geom: new Point(100,-45), name: "Point 1", price: 1.0],
                [geom: new Point(101,-46), name: "Point 2", price: 10.0],
                [geom: new Point(102,-47), name: "Point 3", price: 100.0],
            ])

        * Layer.transform using gt-transform module::

            Layer layer2 = layer.transform("buffered_facilities", [
                geom: "buffer(geom, 10)",
                name: "strToUpperCase(name)",
                price: "price * 10"
            ])

        * geoscript.layer.Property::

            Property prop = new Property('states.properties')

        * Feature first(Map options = [:])

        * Layer.update can take an Expression::

            layer.update(s.get("price"), Expression.fromCQL("price * 2"))

        * Layer.update(groovy script)::

            layer.update(s.get('name'), "return c + '). ' + f.get('name')", Filter.PASS, true)

        * Layer reproject(Projection p, Workspace outputWorkspace, String newName, int chunk=1000, Projection sourceProjection = new Projection("EPSG:4326"))

          Reproject a Layer to another Layer in the given Workspace

        * Layer reproject(Layer projectedLayer, int chunk = 1000, Projection sourceProjection = new Projection("EPSG:4326"))

          Reproject a Layer to another Layer that already exists.

        * Layer getCursor(fields:[])::

            layer.getCursor([fields: ["name"]])

    **Layer IO**

        * KmlReader and KmlWriter

    **Filter**

        * Filter getNot()::

            new Filter("name='foo').not == new Filter("name<>'foot')

        * Filter.simplify()

    **Expression**

        * Object evaluate(Object obj = null)::

            Expression e = new Expression(12)
            assertEquals 12, e.evaluate()

    **Bounds**

        * void setProj(def projection)
        * static Bounds fromString(String str)
        * Bounds fixAspectRatio(int w, int h)
        * boolean contains(Bounds other)
        * double getAspect()
        * Geometry getGrid(int columns, int rows, String type = "polygon")
        * Geometry getGrid(double cellWidth, double cellHeight, String type = "polygon")
        * void generateGrid(int columns, int rows, String type, Closure c)
        * void generateGrid(double cellWidth, double cellHeight, String type, Closure c)

    **WMS**

        * WMS::

            WMS wms = new WMS("http://localhost:8080/geoserver/ows?service=wms&version=1.1.1&request=GetCapabilities")
            println "Name: ${wms.name}"
            println "Title: ${wms.title}"
            def image = wms.getImage("world:borders")

        * WMSLayer::

            WMS wms = new WMS("http://localhost:8080/geoserver/ows?service=wms&version=1.1.1&request=GetCapabilities")
            def map = new geoscript.render.Map(
                layers: [new WMSLayer(wms, ["world:borders","world:cities"])]
            )
            map.render(new File("map_world.png"))

    **Workspace**

        * WFS::

            def wfs = new WFS("http://localhost:8080/geoserver/ows?service=wfs&version=1.1.0&request=GetCapabilities", timeout: 9000)

        * H2 constructor with database file instead of directory

    **Render**

        * GIF

          Image subclass that includes animated GIF support!::

            Map map = new Map(layers: [layer], backgroundColor: "white")
            GIF gif = new GIF()
            def img = gif.render(map)

            Map map = new Map(layers: [layer], backgroundColor: "white")
            GIF gif = new GIF()
            List images = ["WA","OR","CA"].collect {state ->
              map.bounds = layer.getFeatures("STATE_ABBR = '${state}'")[0].bounds
              def image = gif.render(map)
              image
            }
            File file = File.createTempFile("image_",".gif")
            gif.renderAnimated(images, file, 500, true)

        * PNG::

            Map map = new Map(layers: [layer], backgroundColor: "white")
            PNG png = new PNG()
            def img = png.render(map)

        * JPEG::

            Map map = new Map(layers: [layer], backgroundColor: "white")
            JPEG jpeg = new JPEG()
            def img = jpeg.render(map)

1.0
---

   The 1.0 release of GeoScript was built on Groovy 1.8.8, GeoTools 8.x and Java Topology Suite (JTS) 1.12.

   The focus was the following modules:

        * Geometry (geoscript.geom)
        * Projection (geoscript.proj)
        * Vector Layers (geoscript.feature, geoscript.layer, geoscript.workspace)
        * Rendering (geoscript.viewer, geoscript.style, geoscript.render)
        * Process (geoscript.process)
        * Spatial Index (geoscript.index)
        * Expressions (geoscript.filter)
