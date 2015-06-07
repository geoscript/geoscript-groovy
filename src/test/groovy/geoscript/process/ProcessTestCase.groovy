package geoscript.process

import geoscript.feature.Field
import geoscript.layer.Raster
import geoscript.workspace.Memory
import org.junit.Test
import static org.junit.Assert.*
import geoscript.geom.*
import geoscript.layer.*
import org.geotools.process.Processors
import org.opengis.feature.type.Name
import org.geotools.feature.NameImpl
import org.geotools.process.ProcessFactory

/**
 *  The Process UnitTest
 *  @author Jared Erickson
 */
class ProcessTestCase {
    
    private Layer getStatesLayer () {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        new Shapefile(file)
    }
    
    @Test void conversions() {
        def pt = new Point(0,0)
        assertTrue Process.convert(pt, com.vividsolutions.jts.geom.Geometry) instanceof com.vividsolutions.jts.geom.Geometry
        assertTrue Process.convert(pt.g, geoscript.geom.Geometry) instanceof geoscript.geom.Geometry

        def b = new Bounds(0,0,10,10,"EPSG:4326")
        assertTrue Process.convert(b, org.geotools.geometry.jts.ReferencedEnvelope) instanceof org.geotools.geometry.jts.ReferencedEnvelope
        assertTrue Process.convert(b.env, geoscript.geom.Bounds) instanceof geoscript.geom.Bounds

        def layer = getStatesLayer()
        assertTrue Process.convert(layer, org.geotools.feature.FeatureCollection) instanceof org.geotools.feature.FeatureCollection
        assertTrue Process.convert(layer.fs.features, geoscript.layer.Layer) instanceof geoscript.layer.Layer
        assertTrue Process.convert(layer.fs.features, geoscript.layer.Cursor) instanceof geoscript.layer.Cursor
        
        assertTrue Process.convert(layer.cursor, org.geotools.feature.FeatureCollection) instanceof org.geotools.feature.FeatureCollection
        assertTrue Process.convert(layer.fs.features, geoscript.layer.Cursor) instanceof geoscript.layer.Cursor
    }
    
    @Test void getProcessNames() {
        def names = Process.processNames
        assertTrue names.size() > 0
    }
    
    @Test void boundsGeoToolsProcess() {
        def names = Process.processNames
        String processName = "gs:Bounds" in names ? "gs:Bounds" : "vec:Bounds"
        def p = new Process(processName)
        assertEquals processName, p.name
        assertEquals "Bounds", p.title
        assertEquals "Computes the bounding box of the input features.", p.description
        assertEquals "1.0.0", p.version
        assertEquals processName, p.toString()
        
        assertEquals 1, p.parameters.size()
        assertTrue p.parameters.containsKey("features")
        assertEquals geoscript.layer.Cursor, p.parameters.get("features")
        
        assertEquals 1, p.results.size()
        assertTrue p.results.containsKey("bounds")
        assertEquals geoscript.geom.Bounds, p.results.get("bounds")

        Layer layer = getStatesLayer()
        Map results = p.execute(["features": layer])
        assertTrue results.containsKey("bounds")
        assertTrue results.bounds instanceof geoscript.geom.Bounds

        results = p.execute(["features": layer.getCursor("STATE_NAME = 'Washington' OR STATE_NAME = 'Oregon'")])
        assertTrue results.containsKey("bounds")
        assertTrue results.bounds instanceof geoscript.geom.Bounds
    }
    
    @Test void countGeoToolsProcess() {
        def names = Process.processNames
        String processName = "gs:Count" in names ? "gs:Count" : "vec:Count"
        def p = new Process(processName)
        assertEquals processName, p.name
        assertEquals "Count Features", p.title
        assertEquals "Computes the number of features in a feature collection.", p.description
        assertEquals "1.0.0", p.version
        assertEquals processName, p.toString()

        assertEquals 1, p.parameters.size()
        assertTrue p.parameters.containsKey("features")
        assertEquals geoscript.layer.Cursor, p.parameters.get("features")

        assertEquals 1, p.results.size()
        assertTrue p.results.containsKey("result")
        assertEquals java.lang.Number, p.results.get("result")

        Layer layer = getStatesLayer()
        Map results = p.execute(["features": layer])
        assertTrue results.containsKey("result")
        assertEquals 49, results.result

        results = p.execute(["features": layer.getCursor("STATE_NAME = 'Washington' OR STATE_NAME = 'Oregon'")])
        assertTrue results.containsKey("result")
        assertEquals 2, results.result
    }

    @Test void collectGeometriesGeoToolsProcess() {
        def names = Process.processNames
        String processName = "gs:CollectGeometries" in names ? "gs:CollectGeometries" : "vec:CollectGeometries"
        def p = new Process(processName)
        assertEquals processName, p.name
        assertEquals "Collect Geometries", p.title
        assertEquals "Collects the deafult geometries of the input features and combines them into a single geometry collection", p.description
        assertEquals "1.0.0", p.version
        assertEquals processName, p.toString()

        assertEquals 1, p.parameters.size()
        assertTrue p.parameters.containsKey("features")
        assertEquals geoscript.layer.Cursor, p.parameters.get("features")

        assertEquals 1, p.results.size()
        assertTrue p.results.containsKey("result")
        assertEquals geoscript.geom.Geometry, p.results.get("result")

        Layer layer = getStatesLayer()
        Map results = p.execute(["features": layer])
        assertTrue results.containsKey("result")
        assertTrue results.result instanceof geoscript.geom.Geometry
        assertEquals 39, (results.result as Geometry).numGeometries

        results = p.execute(["features": layer.getCursor("STATE_NAME = 'Washington' OR STATE_NAME = 'Oregon'")])
        assertTrue results.containsKey("result")
        assertTrue results.result instanceof geoscript.geom.Geometry
        assertEquals 3, (results.result as Geometry).numGeometries
    }
    
    @Test void convexHullClosureProcess() {
        Process p = new Process("convexhull",
            "Create a convexhull around the features",
            [features: geoscript.layer.Cursor],
            [result: geoscript.layer.Cursor],
            { inputs ->
                def geoms = new GeometryCollection(inputs.features.collect{f -> f.geom})
                def output = new Layer()
                output.add([geoms.convexHull])
                [result: output]
            }
        )
        assertEquals "geoscript:convexhull", p.name
        assertEquals "convexhull", p.title
        assertEquals "Create a convexhull around the features", p.description
        assertEquals "1.0.0", p.version
        assertEquals "geoscript:convexhull", p.toString()

        assertEquals 1, p.parameters.size()
        assertTrue p.parameters.containsKey("features")
        assertEquals geoscript.layer.Cursor, p.parameters.get("features")

        assertEquals 1, p.results.size()
        assertTrue p.results.containsKey("result")
        assertEquals geoscript.layer.Cursor, p.results.get("result")

        Layer layer = getStatesLayer()
        Map results = p.execute([features: layer.cursor])
        assertTrue results.result instanceof geoscript.layer.Cursor
        Cursor c = results.result
        c.each {f -> f.geom instanceof Polygon}

        // Make sure the Closure Process is registered correctly by looking it up by name
        p = new Process("geoscript:convexhull")
        assertNotNull p
        assertNotNull p.process != null

        // Execute the underlying GeoTools Process (inputs and output should be GeoTools objects)
        Map convexHullResults = p.process.execute([features: layer.fs.features])
        assertTrue convexHullResults.result instanceof org.geotools.feature.FeatureCollection

        // Make sure the ProcessFactory works
        Name name = new NameImpl("geoscript","convexhull")
        ProcessFactory factory = Processors.createProcessFactory(name)
        assertEquals "geoscript:convexhull", name.toString()
        assertEquals "convexhull", factory.getTitle(name).toString()
        assertEquals "Create a convexhull around the features", factory.getDescription(name).toString()
        assertEquals "1.0.0", factory.getVersion(name)

        // The ParameterInfo should be GeoTools types
        Map paramInfo = factory.getParameterInfo(name)
        assertEquals 1, paramInfo.size()
        assertTrue paramInfo.containsKey("features")
        def param = paramInfo.get("features")
        assertTrue param instanceof org.geotools.data.Parameter
        assertTrue org.geotools.feature.FeatureCollection.isAssignableFrom(param.type)

        // The ResultInfo should be GeoTools types
        Map resultInfo = factory.getResultInfo(name,[:])
        assertEquals 1, resultInfo.size()
        assertTrue resultInfo.containsKey("result")
        param = resultInfo.get("result")
        assertTrue param instanceof org.geotools.data.Parameter
        assertTrue org.geotools.feature.FeatureCollection.isAssignableFrom(param.type)
    }

    @Test void centroidsClosureProcess() {
        Process p = new Process("centroids",
                "Create a centroid from the geometry for each feature",
                [features: geoscript.layer.Cursor],
                [result: geoscript.layer.Cursor],
                { inputs ->
                    def output = new Layer()
                    inputs.features.each{f -> output.add(["geom": f.geom.centroid])}
                    [result: output]
                }
        )
        assertEquals "geoscript:centroids", p.name
        assertEquals "centroids", p.title
        assertEquals "Create a centroid from the geometry for each feature", p.description
        assertEquals "1.0.0", p.version
        assertEquals "geoscript:centroids", p.toString()

        assertEquals 1, p.parameters.size()
        assertTrue p.parameters.containsKey("features")
        assertEquals geoscript.layer.Cursor, p.parameters.get("features")

        assertEquals 1, p.results.size()
        assertTrue p.results.containsKey("result")
        assertEquals geoscript.layer.Cursor, p.results.get("result")

        Layer layer = getStatesLayer()
        Map results = p.execute([features: layer.cursor])
        assertTrue results.result instanceof geoscript.layer.Cursor
        Cursor c = results.result
        int count = 0
        c.each {f -> f.geom instanceof Point; count++}
        assertEquals 49, count
    }

    @Test void rasterAddProcess() {
        Bounds bounds = new Bounds(0, 0, 7, 5, "EPSG:4326")
        List data1 = [
                [0,0,0,0,0,0,0],
                [0,1,1,1,1,1,0],
                [0,1,2,3,2,1,0],
                [0,1,1,1,1,1,0],
                [0,0,0,0,0,0,0]
        ]
        Raster raster1 = new Raster(data1, bounds)

        List data2 = [
                [1,1,1,1,1,1,1],
                [1,2,2,2,2,2,1],
                [1,2,3,4,3,2,1],
                [1,2,2,2,2,2,1],
                [1,1,1,1,1,1,1]
        ]
        Raster raster2 = new Raster(data2, bounds)

        Process p = new Process("ras:AddCoverages")
        Map results = p.execute(["coverageA": raster1, "coverageB": raster2])
        Raster raster3 = results.result

        assertEquals 1, raster3.eval(new Point(0.5,0.5))[0], 0.1
        assertEquals 3, raster3.eval(new Point(1.5,1.5))[0], 0.1
        assertEquals 5, raster3.eval(new Point(2.5,2.5))[0], 0.1
        assertEquals 7, raster3.eval(new Point(3.5,2.5))[0], 0.1
    }

    @Test void rasterToPointsProcess() {
        Bounds bounds = new Bounds(0, 0, 7, 5, "EPSG:4326")
        List data = [
                [0,0,0,0,0,0,0],
                [0,1,1,1,1,1,0],
                [0,1,2,3,2,1,0],
                [0,1,1,1,1,1,0],
                [0,0,0,0,0,0,0]
        ]
        Raster raster = new Raster(data, bounds)

        Process p = new Process("ras:RasterAsPointCollection")
        Map results = p.execute(["data": raster])
        Cursor c = results.result
        int count = 0
        c.each {f -> f.geom instanceof Point; count++}
        assertEquals 35, count
    }

    @Test void vectorToRasterProcess() {
        Bounds bounds = new Bounds(0,0,10,11,"EPSG:4326")
        Geometry geom = bounds.getGrid(5,5,"poygon")
        Layer layer = new Memory().create("grid",[
            new Field("geom","Polygon","EPSG:4326"),
            new Field("value","Double")
        ])
        geom.geometries.eachWithIndex{g,i ->
            layer.add([geom:g, value: i + 10])
        }
        Process p = new Process("vec:VectorToRaster")
        Map results = p.execute([
            features: layer,
            rasterWidth: 400,
            rasterHeight: 400,
            attribute: "value",
            bounds: layer.bounds
        ])
        Raster raster = results.result
        assertNotNull raster
        assertEquals layer.bounds, raster.bounds
    }
}
