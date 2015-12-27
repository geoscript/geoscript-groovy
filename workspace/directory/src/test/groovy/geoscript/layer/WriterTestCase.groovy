package geoscript.layer

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.geom.Bounds
import geoscript.geom.Geometry
import geoscript.geom.Point
import geoscript.workspace.Directory
import geoscript.workspace.Workspace
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertEquals

/**
 * The Writer Unit Test
 * @author Jared Erickson
 */
class WriterTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    private void testWriter(Workspace w, int numPoints, int batch) {
        Geometry geom = new Bounds(0,0,50,50).geometry
        List pts = geom.createRandomPoints(geom, numPoints).points
        Schema s = new Schema("points", [new Field("the_geom","Point", "EPSG:4326"), new Field("id","int")])
        Layer layer = w.create(s)
        def writer = new Writer(layer, batch: batch)
        long start = System.currentTimeMillis()
        try {
            pts.eachWithIndex{Point pt, int i ->
                Feature f = writer.newFeature
                f.geom = pt
                f['id'] = i
                writer.add(f)
            }
        } finally {
            writer.close()
        }
        long end = System.currentTimeMillis()
        long time = end - start
        println "${w.format} with ${numPoints} points (batch size = ${batch}) took ${time} milleseconds"
        assertEquals numPoints, layer.count
        w.close()
    }

    @Test void testWriterWithShapefile() {
        testWriter(new Directory(folder.newFolder("points")), 100, 65)
    }

    @Test void testStaticWriter() {
        Workspace w = new Directory(folder.newFolder("points"))
        int numPoints = 100
        int batch = 65
        Geometry geom = new Bounds(0,0,50,50).geometry
        List pts = geom.createRandomPoints(geom, numPoints).points
        Schema s = new Schema("points", [new Field("the_geom","Point", "EPSG:4326"), new Field("id","int")])
        Layer layer = w.create(s)
        long start = System.currentTimeMillis()
        def writer = Writer.write(layer, batch: batch) { writer ->
            pts.eachWithIndex{Point pt, int i ->
                Feature f = writer.newFeature
                f.geom = pt
                f['id'] = i
                writer.add(f)
            }
        }
        long end = System.currentTimeMillis()
        long time = end - start
        println "${w.format} with ${numPoints} points (batch size = ${batch}) took ${time} milleseconds"
        assertEquals numPoints, layer.count
        w.close()
    }
}
