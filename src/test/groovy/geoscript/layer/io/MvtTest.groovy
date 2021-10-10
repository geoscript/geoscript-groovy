package geoscript.layer.io

import geoscript.feature.Field
import geoscript.layer.Layer
import geoscript.layer.Property
import geoscript.layer.Shapefile
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import static org.junit.jupiter.api.Assertions.*

/**
 * The Mvt Unit Test
 * @author Jared Erickson
 */
class MvtTest {

    @TempDir
    File folder

    @Test void writeRead() {
        URL url = getClass().getClassLoader().getResource("states.shp")
        Layer layer = new Shapefile(new File(url.toURI()))
        File file = new File(folder,"states.mvt")
        Mvt.write(layer, file)
        assertTrue file.length() > 0
        Layer layer2 = Mvt.read(file)
        assertTrue layer2.count > 0
        layer.schema.fields.each { Field fld ->
            if (!fld.isGeometry()) {
                assertTrue layer2.schema.has(fld.name)
            }
        }
    }

    @Test void writeToFile() {
        URL url = getClass().getClassLoader().getResource("states.shp")
        Layer layer = new Shapefile(new File(url.toURI()))
        File file = new File(folder,"states.mvt")
        Mvt.write(layer, file)
        assertTrue file.length() > 0
    }

    @Test void writeToOutputStream() {
        URL url = getClass().getClassLoader().getResource("states.shp")
        Layer layer = new Shapefile(new File(url.toURI()))
        File file = new File(folder,"states.mvt")
        OutputStream out = new FileOutputStream(file)
        Mvt.write(layer, out)
        out.close()
        assertTrue file.length() > 0
    }

    @Test void readFromUrl() {
        URL url = getClass().getClassLoader().getResource("1582.mvt")
        Layer layer = Mvt.read(url)
        assertEquals 6, layer.count
    }

    @Test void readFromFile() {
        URL url = getClass().getClassLoader().getResource("1582.mvt")
        Layer layer = Mvt.read(new File(url.toURI()))
        assertEquals 6, layer.count
    }

    @Test void readFromInputStream() {
        URL url = getClass().getClassLoader().getResource("1582.mvt")
        InputStream input = url.openStream()
        Layer layer = Mvt.read(input)
        input.close()
        assertEquals 6, layer.count
    }

    @Test void writeAndReadEarthquakes() {
        URL url = getClass().getClassLoader().getResource("earthquakes.properties")
        File file = new File(url.toURI())
        Layer inLayer = new Property(file)
        File outFile = new File(folder,"earthquakes.mvt")
        Mvt.write(inLayer, outFile)
        Layer outLayer = Mvt.read(outFile)
        assertEquals inLayer.count, outLayer.count
    }
}
