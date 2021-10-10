package geoscript.layer.io

import geoscript.layer.Layer
import geoscript.layer.Shapefile
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.*

/**
 * The MvtWriter Unit Test
 * @author Jared Erickson
 */
class MvtWriterTest {

    @TempDir
    File folder

    @Test void writeToFile() {
        URL url = getClass().getClassLoader().getResource("states.shp")
        Layer layer = new Shapefile(new File(url.toURI()))
        File file = new File(folder,"states.mvt")
        Writer writer = new MvtWriter()
        writer.write(layer, file)
        assertTrue file.length() > 0
    }

    @Test void writeToOutputStream() {
        URL url = getClass().getClassLoader().getResource("states.shp")
        Layer layer = new Shapefile(new File(url.toURI()))
        File file = new File(folder,"states.mvt")
        OutputStream out = new FileOutputStream(file)
        Writer writer = new MvtWriter()
        writer.write(layer, out)
        out.close()
        assertTrue file.length() > 0
    }

    @Test void writeToString() {
        URL url = getClass().getClassLoader().getResource("states.shp")
        Layer layer = new Shapefile(new File(url.toURI()))
        Writer writer = new MvtWriter()
        String str = writer.write(layer)
        assertTrue str.length() > 0
    }

}
