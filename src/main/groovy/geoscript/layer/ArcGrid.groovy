package geoscript.layer

import geoscript.proj.Projection
import org.apache.commons.io.input.ReaderInputStream
import org.geotools.factory.Hints

import java.nio.charset.Charset

/**
 * A Format that can read and write ArcGrids
 * @author Jared Erickson
 */
class ArcGrid extends Format {

    /**
     * Create a new ArcGrid
     */
    ArcGrid() {
        super(new org.geotools.gce.arcgrid.ArcGridFormat())
    }

    /**
     * Read a Raster from a String
     * @param source The String source
     * @param charset The Charset which defaults to UTF-8
     * @param proj The Projection
     * @return A Raster
     */
    Raster read(Map options = [:], String source, Projection proj = null) {
        String charset = options.get("charset", "UTF-8")
        Hints hints = new Hints()
        if (proj) {
            hints.put(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, proj.crs)
        }
        def reader = gridFormat.getReader(new ReaderInputStream(new StringReader(source), Charset.forName(charset)), hints)
        new Raster(reader.read(null), this)
    }

    /**
     * Write an ArcGrid Raster to a String
     * @param raster The Raster
     * @param format The string format can either be "arc" which is the default, or grass
     * @return A String
     */
    String writeToString(Raster raster, String format = "arc") {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(raster, out, GRASS: format.equalsIgnoreCase("grass") ? true : false)
        out.close()
        out.toString()
    }
}
